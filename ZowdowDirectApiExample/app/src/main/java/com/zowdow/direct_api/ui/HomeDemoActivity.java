package com.zowdow.direct_api.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zowdow.direct_api.R;
import com.zowdow.direct_api.ZowdowDirectApplication;
import com.zowdow.direct_api.network.injection.NetworkComponent;
import com.zowdow.direct_api.network.models.base.BaseResponse;
import com.zowdow.direct_api.network.models.init.InitResponse;
import com.zowdow.direct_api.network.models.unified.UnifiedDTO;
import com.zowdow.direct_api.network.models.unified.suggestions.CardFormat;
import com.zowdow.direct_api.network.models.unified.suggestions.Suggestion;
import com.zowdow.direct_api.network.services.InitApiService;
import com.zowdow.direct_api.network.services.UnifiedApiService;
import com.zowdow.direct_api.ui.adapters.SuggestionsAdapter;
import com.zowdow.direct_api.utils.QueryUtils;
import com.zowdow.direct_api.utils.constants.CardFormats;
import com.zowdow.direct_api.utils.constants.ExtraKeys;
import com.zowdow.direct_api.utils.location.LocationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeDemoActivity extends AppCompatActivity {
    private static final String TAG = HomeDemoActivity.class.getSimpleName();
    private static final String DEFAULT_CAROUSEL_TYPE = "stream";

    private boolean apiInitialized;
    private Integer storedListViewPosition;
    private String currentCardFormat = CardFormats.CARD_FORMAT_INLINE;
    private String currentSearchKeyWord = "";

    private SuggestionsAdapter suggestionsAdapter;
    private Subscription initApiSubscription;
    private Subscription unifiedApiSubscription;
    private Subscription suggestionsSubscription;

    @Inject InitApiService initApiService;
    @Inject UnifiedApiService unifiedApiService;

    @BindView(R.id.suggestion_query_edit_text) EditText suggestionQueryEditText;
    @BindView(R.id.suggestions_list_view) RecyclerView suggestionsListView;
    @BindView(R.id.placeholder_text_view) TextView noItemsPlaceholderTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_demo);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            apiInitialized = savedInstanceState.getBoolean(ExtraKeys.EXTRA_API_INITIALIZED);
            currentCardFormat = savedInstanceState.getString(ExtraKeys.EXTRA_RESTORED_CARD_FORMAT);
            currentSearchKeyWord = savedInstanceState.getString(ExtraKeys.EXTRA_RESTORED_SEARCH_KEYWORD);
        }
        setupSuggestionsListView();
        initializeApiServices();
        initializeZowdowApi();
    }

    private void setupSuggestionsListView() {
        suggestionsAdapter = new SuggestionsAdapter(this, new ArrayList<>(), this::onCardClicked);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        suggestionsListView.setLayoutManager(layoutManager);
        suggestionsListView.setAdapter(suggestionsAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void onCardClicked(String webUrl, String suggestionTitle) {
        Intent webIntent = new Intent(this, WebViewActivity.class);
        webIntent.putExtra(ExtraKeys.EXTRA_ARTICLE_TITLE, suggestionTitle);
        webIntent.putExtra(ExtraKeys.EXTRA_ARTICLE_URL, webUrl);
        startActivity(webIntent);
    }

    private void initializeApiServices() {
        NetworkComponent networkComponent = ZowdowDirectApplication.getNetworkComponent();
        networkComponent.inject(this);
    }

    private void initializeZowdowApi() {
        LocationManager.get().start(this);
        Map<String, Object> initQueryMap = QueryUtils.createQueryMap(this);
        if (apiInitialized) {
            onApiInitialized();
            restoreSuggestions();
        } else {
            initApiSubscription = initApiService.init(initQueryMap)
                    .subscribeOn(Schedulers.io())
                    .map(InitResponse::getRecords)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(records -> {
                        Log.d(TAG, "Initialization was performed successfully!");
                        apiInitialized = true;
                    }, throwable -> Log.e(TAG, "Something went wrong during initialization: " + throwable.getMessage()), this::onApiInitialized);
        }
    }

    public void onApiInitialized() {
        suggestionQueryEditText.setEnabled(true);
        suggestionQueryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String queryString = s.toString();
                findSuggestions(queryString);
                currentSearchKeyWord = queryString;
            }
        });
    }

    private void restoreSuggestions() {
        if (!currentSearchKeyWord.isEmpty()) {
            findSuggestions(currentSearchKeyWord);
        }
    }

    private void findSuggestions(String searchKeyWord) {
        Map<String, Object> queryMap = QueryUtils.createQueryMapForUnifiedApi(this, searchKeyWord, currentCardFormat);
        unifiedApiSubscription = unifiedApiService.loadSuggestions(queryMap)
                .subscribeOn(Schedulers.io())
                .cache()
                .subscribe(this::processSuggestionsResponse, throwable -> {
                    Toast.makeText(HomeDemoActivity.this, "Could not load suggestions", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Could not load suggestions: " + throwable.getMessage());
                });
    }

    private void processSuggestionsResponse(BaseResponse<UnifiedDTO> suggestionsResponse) {
        final String rId = suggestionsResponse.getMeta().getRid();
        suggestionsSubscription = Observable.just(suggestionsResponse)
                .subscribeOn(Schedulers.io())
                .flatMapIterable(BaseResponse::getRecords) // converts response wrapper into an iterable list of suggestions
                .map(suggestionItem -> // performs suggestion deserialization
                        suggestionItem
                                .getSuggestion()
                                .toSuggestion(rId, DEFAULT_CAROUSEL_TYPE, currentCardFormat)
                )
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuggestionsLoaded, throwable -> {
                    Log.e(TAG, "Could not load suggestions: " + throwable.getMessage());
                });
    }

    public void onSuggestionsLoaded(List<Suggestion> suggestions) {
        suggestionsAdapter.setSuggestions(suggestions);
        if (suggestions != null && !suggestions.isEmpty()) {
            noItemsPlaceholderTextView.setVisibility(View.GONE);
            suggestionsListView.setVisibility(View.VISIBLE);
            restoreSuggestionsListViewPosition();
        } else {
            noItemsPlaceholderTextView.setVisibility(View.VISIBLE);
        }
    }

    private void restoreSuggestionsListViewPosition() {
        if (storedListViewPosition != null) {
            suggestionsListView.getLayoutManager().scrollToPosition(storedListViewPosition);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card_types, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_label_card:
                onCardFormatChanged(CardFormats.CARD_FORMAT_INLINE);
                return true;
            case R.id.action_stamp_card:
                onCardFormatChanged(CardFormats.CARD_FORMAT_STAMP);
                return true;
            case R.id.action_ticket_card:
                onCardFormatChanged(CardFormats.CARD_FORMAT_TICKET);
                return true;
            case R.id.action_gif_card:
                onCardFormatChanged(CardFormats.CARD_FORMAT_ANIMATED_GIF);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onCardFormatChanged(@CardFormat String newCardFormat) {
        this.currentCardFormat = newCardFormat;
        findSuggestions(currentSearchKeyWord);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationManager.get().stop();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            storedListViewPosition = savedInstanceState.getInt(ExtraKeys.EXTRA_RECYCLER_VIEW_STATE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ExtraKeys.EXTRA_RECYCLER_VIEW_STATE, ((LinearLayoutManager) suggestionsListView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
        outState.putBoolean(ExtraKeys.EXTRA_API_INITIALIZED, apiInitialized);
        outState.putString(ExtraKeys.EXTRA_RESTORED_CARD_FORMAT, currentCardFormat);
        outState.putString(ExtraKeys.EXTRA_RESTORED_SEARCH_KEYWORD, currentSearchKeyWord);
    }

    @Override
    protected void onDestroy() {
        if (initApiSubscription != null && initApiSubscription.isUnsubscribed()) {
            initApiSubscription.unsubscribe();
        }
        if (suggestionsSubscription != null && suggestionsSubscription.isUnsubscribed()) {
            suggestionsSubscription.unsubscribe();
        }
        if (unifiedApiSubscription != null && unifiedApiSubscription.isUnsubscribed()) {
            unifiedApiSubscription.unsubscribe();
        }
        super.onDestroy();
    }
}