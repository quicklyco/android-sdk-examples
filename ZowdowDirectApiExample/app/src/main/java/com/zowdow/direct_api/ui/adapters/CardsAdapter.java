package com.zowdow.direct_api.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.zowdow.direct_api.R;
import com.zowdow.direct_api.ZowdowDirectApplication;
import com.zowdow.direct_api.network.models.unified.ActionDTO;
import com.zowdow.direct_api.network.models.unified.suggestions.Card;
import com.zowdow.direct_api.network.models.unified.suggestions.Suggestion;
import com.zowdow.direct_api.ui.VideoActivity;
import com.zowdow.direct_api.ui.views.CardImageView;
import com.zowdow.direct_api.utils.ViewUtils;
import com.zowdow.direct_api.utils.constants.ActionTypes;
import com.zowdow.direct_api.utils.ImageParams;
import com.zowdow.direct_api.utils.TrackHelper;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for cards list inside each suggestion carousel.
 */
public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardViewHolder> {
    private Context context;
    private Suggestion currentSuggestion;
    private List<Card> cards;
    private OnCardClickListener cardClickListener;

    @Inject TrackHelper trackHelper;

    private CardsAdapter() {
        ZowdowDirectApplication.getNetworkComponent().inject(this);
    }

    public CardsAdapter(@NonNull Context context, @NonNull Suggestion currentSuggestion, @NonNull List<Card> cards) {
        this();
        this.context = context;
        this.currentSuggestion = currentSuggestion;
        this.cards = cards;
    }

    public CardsAdapter(Context context, Suggestion currentSuggestion, List<Card> cards, OnCardClickListener cardClickListener) {
        this(context, currentSuggestion, cards);
        this.cardClickListener = cardClickListener;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Card currentCard = cards.get(position);
        ImageParams imageParamsForCard = ImageParams.create(currentCard);

        DrawableTypeRequest<String> glideRequest = Glide.with(context).load(imageParamsForCard.path);

        if (currentCard.isGif()) {
            glideRequest.asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE);
        } else {
            glideRequest.asBitmap().fitCenter();
        }
        glideRequest
                .override(imageParamsForCard.width, imageParamsForCard.height)
                .into(new ImageViewTarget<GlideDrawable>(holder.cardImageView) {
                    @Override
                    protected void setResource(GlideDrawable resource) {
                        holder.cardRootView.setCardElevation(ViewUtils.dpToPx(2));
                        holder.cardImageView.setImageDrawable(resource);
                        holder.cardImageView.setTrackInfo(currentCard);
                    }
                });
        holder.cardImageView.setOnClickListener(v -> {
            processCardClick(context, currentCard);
        });
    }

    /**
     * Handles card click events depending on their type & target.
     * @see ActionTypes
     * @param context
     * @param card
     */
    private void processCardClick(Context context, Card card) {
        HashMap<String, String> actions = new HashMap<>();
        for (ActionDTO action : card.getActions()) {
            actions.put(action.getActionType(), action.getActionTarget());
        }
        String actionType;
        String actionTarget;
        if (actions.containsKey(ActionTypes.ACTION_VIDEO)) {
            actionType = ActionTypes.ACTION_VIDEO;
            actionTarget = actions.get(actionType);
            onVideoCardClicked(context, actionTarget, card.getClickUrl());
        } else if (actions.containsKey(ActionTypes.ACTION_DEEP_LINK) &&
                new Intent(Intent.ACTION_VIEW, Uri.parse(actions.get(ActionTypes.ACTION_DEEP_LINK))).resolveActivity(context.getPackageManager()) != null) {
            actionType = ActionTypes.ACTION_DEEP_LINK;
            actionTarget = actions.get(actionType);
            onWebContentCardClicked(actionTarget, card.getClickUrl());
        } else if (actions.containsKey(ActionTypes.ACTION_WEB_URL)) {
            actionType = ActionTypes.ACTION_WEB_URL;
            actionTarget = actions.get(actionType);
            onWebContentCardClicked(actionTarget, card.getClickUrl());
        }
    }

    private void onWebContentCardClicked(String actionTarget, String clickUrl) {
        trackHelper.trackClick(clickUrl);
        if (cardClickListener != null && actionTarget != null) {
            cardClickListener.onCardClicked(actionTarget, currentSuggestion.getSuggestion());
        }
    }

    private void onVideoCardClicked(Context context, String actionTarget, String clickUrl) {
        trackHelper.trackClick(clickUrl);
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(VideoActivity.EXTRA_VIDEO, actionTarget);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setCardClickListener(OnCardClickListener cardClickListener) {
        this.cardClickListener = cardClickListener;
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_image_view)
        CardImageView cardImageView;
        @BindView(R.id.card_root_view)
        CardView cardRootView;

        CardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
