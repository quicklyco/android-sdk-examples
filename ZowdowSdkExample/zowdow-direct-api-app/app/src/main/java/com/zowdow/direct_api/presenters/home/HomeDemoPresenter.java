package com.zowdow.direct_api.presenters.home;

import android.content.Context;
import android.util.Log;

import com.zowdow.direct_api.ZowdowDirectApplication;
import com.zowdow.direct_api.injection.components.NetworkComponent;
import com.zowdow.direct_api.network.models.abs.BaseResponse;
import com.zowdow.direct_api.network.models.init.InitResponse;
import com.zowdow.direct_api.network.models.unified.UnifiedDTO;
import com.zowdow.direct_api.network.services.InitApiService;
import com.zowdow.direct_api.network.services.UnifiedApiService;
import com.zowdow.direct_api.presenters.abs.Presenter;
import com.zowdow.direct_api.utils.RequestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.zowdow.direct_api.utils.constants.CardFormats.CARD_FORMAT_INLINE;

public class HomeDemoPresenter implements Presenter<IHomeView> {
    private static final String TAG = "HomeDemoPresenter";
    private static final String DEFAULT_CAROUSEL_TYPE = "stream";
    private static final String DEFAULT_CARD_FORMAT = CARD_FORMAT_INLINE;
    private static final int DEFAULT_SUGGESTIONS_LIMIT = 10;
    private static final int DEFAULT_CARDS_LIMIT = 15;

    private volatile boolean apiInitialized;
    private Context context;
    private String searchQuery = "";
    private IHomeView view;
    private PublishSubject<String> searchQuerySubject;
    private Subscription initApiSubscription;
    private Subscription unifiedApiSubscription;
    private Subscription suggestionsSubscription;
    private Subscription dynamicSearchSubscription;
    @Inject InitApiService initApiService;
    @Inject UnifiedApiService unifiedApiService;

    public HomeDemoPresenter(Context context) {
        this.context = context;
        this.searchQuerySubject = PublishSubject.create();
        NetworkComponent networkComponent = ZowdowDirectApplication.getNetworkComponent();
        networkComponent.inject(this);
    }

    @Override
    public void onViewAttached(IHomeView view) {
        this.view = view;
    }

    public void initializeZowdowApi() {
        Map<String, Object> initQueryMap = RequestUtils.createQueryMap(context);
        initQueryMap.put(RequestUtils.DEVICE_ID, RequestUtils.getDeviceId(context));
        if (apiInitialized) {
            onApiInitialized();
        } else {
            initApiSubscription = initApiService.init(initQueryMap)
                    .subscribeOn(Schedulers.io())
                    .map(InitResponse::getRecords)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(records -> {
                        Log.d(TAG, "Initialization was performed successfully!");
                    }, throwable -> {
                        Log.e(TAG, "Something went wrong during initialization: " + throwable.getMessage());
                    }, this::onApiInitialized);
        }
    }

    private void onApiInitialized() {
        if (!apiInitialized) {
            startTrackingSuggestionsSearch();
            apiInitialized = true;
        }
        view.onApiInitialized();
        if (searchQuery != null && !searchQuery.isEmpty()) {
            view.onRestoreSearchQuery(searchQuery);
        }
    }

    public void onSearchQueryChanged(String searchQuery) {
        this.searchQuery = searchQuery;
        searchQuerySubject.onNext(searchQuery);
    }

    private void startTrackingSuggestionsSearch() {
        dynamicSearchSubscription = searchQuerySubject.debounce(100, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::retrieveSuggestions, throwable -> {
                    Log.e(TAG, "Incorrect search query: " + throwable.getMessage());
                });
    }

    private void retrieveSuggestions(String searchQuery) {
        Map<String, Object> queryMap = RequestUtils.createQueryMap(context);
        try {
            queryMap.putAll(getDefaultUnifiedQueryMap(searchQuery));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Could not create query map: " + e.getMessage());
        } finally {
            requestSuggestionsFromServer(queryMap);
        }
    }

    private Map<String, Object> getDefaultUnifiedQueryMap(String searchQuery) throws UnsupportedEncodingException {
        Map<String, Object> unifiedQueryMap = new HashMap<>();
        unifiedQueryMap.put("q", URLEncoder.encode(searchQuery, "UTF-8").replace("+", " "));
        unifiedQueryMap.put("s_limit", DEFAULT_SUGGESTIONS_LIMIT);
        unifiedQueryMap.put("c_limit", DEFAULT_CARDS_LIMIT);
        unifiedQueryMap.put(RequestUtils.CARD_FORMAT, DEFAULT_CARD_FORMAT);
        unifiedQueryMap.put(RequestUtils.DEVICE_ID, RequestUtils.getDeviceId(context.getApplicationContext()));
        return unifiedQueryMap;
    }

    private void requestSuggestionsFromServer(Map<String, Object> queryMap) {
        unifiedApiSubscription = unifiedApiService.loadSuggestions(queryMap)
                .subscribeOn(Schedulers.io())
                .cache()
                .subscribe(this::processSuggestionsResponse, throwable -> {
                    view.onApiInitializationFailed();
                    Log.e(TAG, "Could not load suggestions: " + throwable.getMessage());
                });
    }

    private void processSuggestionsResponse(BaseResponse<UnifiedDTO> suggestionsResponse) {
        final String rId = suggestionsResponse.getMeta().getRid();
        suggestionsSubscription = Observable.just(suggestionsResponse)
                .subscribeOn(Schedulers.io())
                .flatMapIterable(BaseResponse::getRecords)
                .map(suggestionItem ->
                    suggestionItem
                            .getSuggestion()
                            .toSuggestion(rId, DEFAULT_CAROUSEL_TYPE, DEFAULT_CARD_FORMAT)
                )
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(suggestions -> {
                    view.onSuggestionsLoaded(suggestions);
                }, throwable -> {
                    Log.e(TAG, "Could not load suggestions: " + throwable.getMessage());
                });
    }

    private void unsubscribeFromNetworkCalls() {
        if (suggestionsSubscription != null && !suggestionsSubscription.isUnsubscribed()) {
            suggestionsSubscription.unsubscribe();
        }
        if (unifiedApiSubscription != null && !unifiedApiSubscription.isUnsubscribed()) {
            unifiedApiSubscription.unsubscribe();
        }
        if (initApiSubscription != null && !initApiSubscription.isUnsubscribed()) {
            initApiSubscription.unsubscribe();
        }
    }

    @Override
    public void onViewDetached() {
        unsubscribeFromNetworkCalls();
        this.view = null;
    }
}
