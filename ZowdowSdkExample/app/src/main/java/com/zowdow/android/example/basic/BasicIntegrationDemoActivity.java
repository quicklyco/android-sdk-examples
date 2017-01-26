package com.zowdow.android.example.basic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import com.zowdow.android.example.R;

import java.util.List;

import co.zowdow.sdk.android.LoaderConfiguration;
import co.zowdow.sdk.android.OnCardClickListener;
import co.zowdow.sdk.android.OnSuggestionClickListener;
import co.zowdow.sdk.android.Suggestion;
import co.zowdow.sdk.android.Zowdow;
import co.zowdow.sdk.android.ZowdowAdapter;

public class BasicIntegrationDemoActivity extends AppCompatActivity {
    private EditText            mEditText;
    private ListView            mListView;
    private Zowdow              mZowdow;
    private LoaderConfiguration mLoaderConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integration_demo);
        setupZowdow();
        setupEditText();
        setupListView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZowdow.onStart();
    }

    @Override
    protected void onStop() {
        mZowdow.onStop();
        super.onStop();
    }

    private void setupZowdow() {
        Zowdow.initialize(this);
        mZowdow = new Zowdow(this, new Zowdow.ZowdowCallback() {
            @Override
            public void onSuggestionsDidLoad(List<Suggestion> list, boolean isTakenFromCache) {

            }

            @Override
            public void onSuggestionsDidFailToLoad(Throwable throwable) {

            }
        });
        mLoaderConfig = new LoaderConfiguration().cardFormats(Zowdow.CARD_FORMAT_INLINE);
    }

    private void setupEditText() {
        mEditText = (EditText) findViewById(R.id.editText);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mZowdow.loadSuggestions(s.toString(), mLoaderConfig);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupListView() {
        mListView = (ListView) findViewById(R.id.listView);
        ZowdowAdapter adapter = mZowdow.createAdapter(null);
        adapter.setCarouselType(Zowdow.CAROUSEL_STREAM);
        adapter.setOnSuggestionClickListener(new OnSuggestionClickListener() {
            @Override
            public void onSuggestionClick(String suggestion) {
                Zowdow.trackSearch(BasicIntegrationDemoActivity.this, "google", suggestion);
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com.ua/search?q=" + suggestion));
                startActivity(i);
            }
        });
        adapter.setOnCardClickListener(new OnCardClickListener() {
            @Override
            public void onCardClick(String suggestion, String cardUrl) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(cardUrl));
                startActivity(i);
            }
        });
        mListView.setAdapter(adapter);
    }
}
