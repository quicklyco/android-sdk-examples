package com.zowdow.direct_api.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zowdow.direct_api.R;
import com.zowdow.direct_api.ZowdowDirectApplication;
import com.zowdow.direct_api.network.models.unified.suggestions.Card;
import com.zowdow.direct_api.network.models.unified.suggestions.Suggestion;
import com.zowdow.direct_api.ui.views.DividerItemDecoration;
import com.zowdow.direct_api.ui.views.CardImageView;
import com.zowdow.direct_api.utils.ViewUtils;
import com.zowdow.direct_api.utils.ImageParams;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Vieholder for each suggestion item. Most suggestions contain cards,
 * so RecyclerView for cards is also initialized here.
 */
public class SuggestionViewHolder extends RecyclerView.ViewHolder {
    private static final float SCALE_FACTOR = 1.3f;
    private static final int ITEMS_SPACING = 8;
    private static final int SUGGESTION_HEIGHT = 36;

    private Context context;
    private LinearLayoutManager layoutManager;
    private CardsAdapter cardsAdapter;
    private Suggestion currentSuggestion;
    private DividerItemDecoration dividerItemDecoration;
    private OnCardClickListener cardClickListener;

    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    @BindView(R.id.suggestion_text_view)
    TextView suggestionTextView;
    @BindView(R.id.cards_list_view)
    RecyclerView cardsListView;

    public SuggestionViewHolder(@NonNull View itemView, OnCardClickListener clickListener) {
        super(itemView);
        context = itemView.getContext();
        dividerItemDecoration = new DividerItemDecoration();
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        cardClickListener = clickListener;
        ButterKnife.bind(this, itemView);
        ZowdowDirectApplication.getNetworkComponent().inject(this);
    }

    public void setupCarousel(Suggestion currentSuggestion) {
        this.currentSuggestion = currentSuggestion;
        setupSuggestionView();
        setupCarousel();
    }

    private void setupSuggestionView() {
        suggestionTextView.setText(currentSuggestion.getSuggestion());
    }

    /**
     * Sets list of cards with horizontal scroll.
     */
    private void setupCarousel() {
        cardsAdapter = new CardsAdapter(context, currentSuggestion, currentSuggestion.getCards());
        cardsAdapter.setCardClickListener(cardClickListener);

        cardsListView.clearOnScrollListeners();
        cardsListView.setChildDrawingOrderCallback(null);
        cardsListView.setLayoutManager(layoutManager);
        cardsListView.setAdapter(cardsAdapter);
        cardsListView.addOnScrollListener(createTrackOnScrollListener());

        dividerItemDecoration.setTopBottomPadding(ITEMS_SPACING);
        rootLayout.getLayoutParams().height = getRowHeight(SUGGESTION_HEIGHT + ITEMS_SPACING);
    }

    private int getRowHeight(int additionalHeight) {
        Card card = currentSuggestion.getCards().get(0);
        ImageParams imageParams = ImageParams.create(card);
        return (int) (imageParams.height + ViewUtils.dpToPx(additionalHeight) * SCALE_FACTOR);
    }

    @NonNull
    private RecyclerView.OnScrollListener createTrackOnScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                final int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                    View container = recyclerView.getChildAt(i);
                    if (container != null) {
                        CardImageView image = (CardImageView) container.findViewById(R.id.card_image_view);
                        image.sendTrackInfo();
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        };
    }
}
