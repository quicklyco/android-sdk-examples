package com.zowdow.direct_api.ui.sections.home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zowdow.direct_api.R;
import com.zowdow.direct_api.network.models.unified.suggestions.Suggestion;
import com.zowdow.direct_api.presenters.abs.PresenterFactory;
import com.zowdow.direct_api.presenters.home.HomeDemoPresenter;
import com.zowdow.direct_api.presenters.home.IHomeView;
import com.zowdow.direct_api.ui.sections.abs.BaseActivity;
import com.zowdow.direct_api.ui.sections.adapters.SuggestionsAdapter;
import com.zowdow.direct_api.ui.sections.web.WebViewActivity;
import com.zowdow.direct_api.utils.constants.CardFormats;
import com.zowdow.direct_api.utils.constants.ExtraKeys;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeDemoActivity extends BaseActivity<HomeDemoPresenter, IHomeView> implements IHomeView {
    private HomeDemoPresenter presenter;
    private SuggestionsAdapter suggestionsAdapter;
    private LinearLayoutManager layoutManager;

    @BindView(R.id.suggestion_query_edit_text)
    EditText suggestionQueryEditText;
    @BindView(R.id.suggestions_list_view)
    RecyclerView suggestionsListView;
    @BindView(R.id.placeholder_text_view)
    TextView noItemsPlaceholderTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_demo);
        ButterKnife.bind(this);

        suggestionsAdapter = new SuggestionsAdapter(this, new ArrayList<>(), this::onCardClicked);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        suggestionsListView.setLayoutManager(layoutManager);
        suggestionsListView.setAdapter(suggestionsAdapter);
    }

    private void onCardClicked(String webUrl, String suggestionTitle) {
        Intent webIntent = new Intent(this, WebViewActivity.class);
        webIntent.putExtra(ExtraKeys.EXTRA_ARTICLE_TITLE, suggestionTitle);
        webIntent.putExtra(ExtraKeys.EXTRA_ARTICLE_URL, webUrl);
        startActivity(webIntent);
    }

    @Override
    protected void onPresenterPrepared(@NonNull HomeDemoPresenter presenter) {
        this.presenter = presenter;
        this.presenter.onViewAttached(this);
        this.presenter.initializeZowdowApi();
    }

    @Override
    public void onApiInitialized() {
        suggestionQueryEditText.setEnabled(true);
        suggestionQueryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.onSearchQueryChanged(s.toString());
            }
        });
    }

    @Override
    public void onSuggestionsLoaded(List<Suggestion> suggestions) {
        suggestionsAdapter.setSuggestions(suggestions);
        if (suggestions != null && !suggestions.isEmpty()) {
            noItemsPlaceholderTextView.setVisibility(View.GONE);
            suggestionsListView.setVisibility(View.VISIBLE);
        } else {
            noItemsPlaceholderTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRestoreSearchQuery(String searchQuery) {
        suggestionQueryEditText.setText(searchQuery);
    }

    @Override
    public void onApiInitializationFailed() {
        Snackbar.make(suggestionsListView, R.string.warning_no_connection, Snackbar.LENGTH_LONG).show();
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
                presenter.onCardFormatChanged(CardFormats.CARD_FORMAT_INLINE);
                return true;
            case R.id.action_stamp_card:
                presenter.onCardFormatChanged(CardFormats.CARD_FORMAT_STAMP);
                return true;
            case R.id.action_ticket_card:
                presenter.onCardFormatChanged(CardFormats.CARD_FORMAT_TICKET);
                return true;
            case R.id.action_gif_card:
                presenter.onCardFormatChanged(CardFormats.CARD_FORMAT_ANIMATED_GIF);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    @Override
    protected PresenterFactory<HomeDemoPresenter> getPresenterFactory() {
        return () -> new HomeDemoPresenter(HomeDemoActivity.this);
    }
}
