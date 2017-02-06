package com.zowdow.android.example.advanced;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.squareup.okhttp.OkHttpClient;
import com.zowdow.android.example.R;
import com.zowdow.android.example.advanced.network.BingNetworkService;
import com.zowdow.android.example.advanced.network.BingResponse;
import com.zowdow.android.example.advanced.network.BingSuggestion;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import co.zowdow.sdk.android.LoaderConfiguration;
import co.zowdow.sdk.android.OnCardClickListener;
import co.zowdow.sdk.android.OnSuggestionClickListener;
import co.zowdow.sdk.android.Suggestion;
import co.zowdow.sdk.android.SuggestionData;
import co.zowdow.sdk.android.Zowdow;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class AdvancedIntegrationDemoActivity extends AppCompatActivity implements OnSuggestionClickListener, OnCardClickListener {
    private static final String BING = "bing";

    private AdvancedDemoAdapter  mAdapter;
    private List<Suggestion>     mZowDowResponse;
    private List<BingSuggestion> mBingResponse;

    private BingNetworkService mBingService;
    private Call<BingResponse> mBingCall;

    private Zowdow.ZowdowCallback mZowDowCallback;

    private Zowdow              mZowDow;
    private LoaderConfiguration mLoaderConfig;

    private boolean mZowDowLoaded;
    private boolean mBingLoaded;

    {
        OkHttpClient client = new OkHttpClient();
        mBingService = new Retrofit.Builder()
                .baseUrl(BingNetworkService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build().create(BingNetworkService.class);

        mZowDowCallback = createZowDowCallback();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integration_demo);
        setupZowDow();
        setupEditText();
        setupListView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZowDow.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mZowDow.onStop();
    }

    private void setupZowDow() {
        Zowdow.initialize(this);
        mZowDow = new Zowdow(this, mZowDowCallback);
        mLoaderConfig = new LoaderConfiguration().cardFormats(Zowdow.CARD_FORMAT_INLINE);
    }

    private void setupEditText() {
        EditText editText = (EditText) findViewById(R.id.editText);
        if (editText != null) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mZowDowLoaded = false;
                    mBingLoaded = false;

                    if (mBingCall != null) {
                        mBingCall.cancel();
                    }

                    mZowDow.loadSuggestions(s.toString(), mLoaderConfig);

                    try {
                        mBingCall = mBingService.bing(URLEncoder.encode(s.toString(), "UTF-8"));
                        mBingCall.enqueue(createBingResponseCallback(s.toString()));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    private void setupListView() {
        mAdapter = new AdvancedDemoAdapter(this, this);
        ListView listView = (ListView) findViewById(R.id.listView);
        if (listView != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String suggestion = mAdapter.getItem(position).getSuggestion();
                    Zowdow.trackDidChooseSuggestion(AdvancedIntegrationDemoActivity.this, "bing", suggestion);
                    onSuggestionClick(suggestion);
                }
            });
            listView.setAdapter(mAdapter);
        }
    }

    private void merge() {
        for (int i = 0; i < mZowDowResponse.size(); i++) {
            Iterator<? extends SuggestionData> it = mBingResponse.iterator();
            while (it.hasNext()) {
                SuggestionData suggestion = it.next();
                if (mZowDowResponse.get(i).getSuggestion().toLowerCase(Locale.ENGLISH)
                        .equals(suggestion.getSuggestion().toLowerCase(Locale.ENGLISH))) {
                    it.remove();
                    break;
                }
            }
        }
        List<SuggestionData> data = new ArrayList<>();
        data.addAll(mZowDowResponse);
        data.addAll(mBingResponse);
        Collections.sort(data, new Comparator<SuggestionData>() {
            @Override
            public int compare(SuggestionData lhs, SuggestionData rhs) {
                return lhs.getSuggestion().toLowerCase(Locale.ENGLISH).compareTo(rhs.getSuggestion().toLowerCase(Locale.ENGLISH));
            }
        });
        mAdapter.setItems(data);
    }

    @Override
    public void onSuggestionClick(String suggestion) {
        Zowdow.trackSearch(AdvancedIntegrationDemoActivity.this, "google", suggestion);
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com.ua/search?q=" + suggestion));
        startActivity(i);
    }

    @Override
    public void onCardClick(String suggestion, String cardUrl) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(cardUrl));
        startActivity(i);
    }

    private Callback<BingResponse> createBingResponseCallback(final String queryFragment) {
        return new Callback<BingResponse>() {
            @Override
            public void onResponse(Response<BingResponse> response, Retrofit retrofit) {
                mBingResponse = new ArrayList<>();
                for (String suggestion : response.body().getSuggestions()) {
                    mBingResponse.add(new BingSuggestion(suggestion));
                }
                if (mBingResponse.size() > 0) {
                    Zowdow.trackSuggestionsReceived(AdvancedIntegrationDemoActivity.this, BING, queryFragment, mBingResponse);
                }
                Collections.sort(mBingResponse, new Comparator<BingSuggestion>() {
                    @Override
                    public int compare(BingSuggestion lhs, BingSuggestion rhs) {
                        return lhs.getSuggestion().compareTo(rhs.getSuggestion());
                    }
                });
                mBingLoaded = true;
                if (mZowDowLoaded) {
                    merge();
                } else {
                    mAdapter.setItems(mBingResponse);
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        };
    }

    private Zowdow.ZowdowCallback createZowDowCallback() {
        return new Zowdow.ZowdowCallback() {
            @Override
            public void onSuggestionsDidLoad(List<Suggestion> suggestions, boolean isTakenFromCache) {
                mZowDowLoaded = true;
                mZowDowResponse = new ArrayList<>();
                mZowDowResponse.addAll(suggestions);
                Collections.sort(mZowDowResponse, new Comparator<Suggestion>() {
                    @Override
                    public int compare(Suggestion lhs, Suggestion rhs) {
                        return lhs.getSuggestion().compareTo(rhs.getSuggestion());
                    }
                });
                if (mBingLoaded) {
                    merge();
                } else {
                    mAdapter.setItems(suggestions);
                }
            }

            @Override
            public void onSuggestionsDidFailToLoad(Throwable e) {
            }
        };
    }
}
