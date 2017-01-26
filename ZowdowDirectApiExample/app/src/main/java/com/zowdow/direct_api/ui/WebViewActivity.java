package com.zowdow.direct_api.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zowdow.direct_api.R;
import com.zowdow.direct_api.utils.constants.ExtraKeys;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends AppCompatActivity {
    @BindView(R.id.web_view)
    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_content);
        ButterKnife.bind(this);

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        if (getIntent() != null) {
            String url = getIntent().getStringExtra(ExtraKeys.EXTRA_ARTICLE_URL);
            String title = getIntent().getStringExtra(ExtraKeys.EXTRA_ARTICLE_TITLE);
            updateToolbarWithArticleTitle(title);
            webView.loadUrl(url);
        }
    }

    private void updateToolbarWithArticleTitle(String articleTitle) {
        if (articleTitle == null) {
            return;
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(articleTitle);
        }
    }
}
