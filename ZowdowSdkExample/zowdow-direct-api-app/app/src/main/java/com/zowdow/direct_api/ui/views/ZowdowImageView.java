package com.zowdow.direct_api.ui.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.zowdow.direct_api.ZowdowDirectApplication;
import com.zowdow.direct_api.network.models.admarketplace.AdListingResponse;
import com.zowdow.direct_api.network.models.unified.ActionDTO;
import com.zowdow.direct_api.network.models.unified.suggestions.Card;
import com.zowdow.direct_api.network.services.AdMarketPlaceService;
import com.zowdow.direct_api.utils.RequestUtils;
import com.zowdow.direct_api.utils.constants.ActionTypes;
import com.zowdow.direct_api.utils.helpers.tracking.TrackHelper;

import java.util.HashMap;

import javax.inject.Inject;

import rx.schedulers.Schedulers;

/**
 * Customized ImageView class which provides interaction with
 * Zowdow tracking mechanism out-of-the-box.
 */
public class ZowdowImageView extends ImageView {
    private static final String TAG = ZowdowImageView.class.getSimpleName();

    private Card currentCard;
    private String currentSuggestion;

    @Inject TrackHelper trackHelper;
    @Inject AdMarketPlaceService adService;

    {
        ZowdowDirectApplication.getNetworkComponent().inject(this);
    }

    public ZowdowImageView(Context context) {
        super(context);
    }

    public ZowdowImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZowdowImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if (drawable != null) {
            post(() -> {
                if (currentCard != null && isShown() && !currentCard.hasBeenTracked()) {
                    onCardShown();
                }
            });
        }
        super.setImageDrawable(drawable);
    }

    /**
     * Called when image drawable is defined for this ImageView.
     */
    private void onCardShown() {
        HashMap<String, String> actions = new HashMap<>();
        for (ActionDTO action : currentCard.getActions()) {
            actions.put(action.getActionType(), action.getActionTarget());
        }
        if (actions.containsKey(ActionTypes.ACTION_AD_CALL)) {
            String adUrl = RequestUtils.getCustomizedAdCallActionTarget(getContext(), actions.get(ActionTypes.ACTION_AD_CALL));
            fetchAdMarketInfo(adUrl);
        } else {
            track();
        }
    }

    /**
     * Calls ads available for a certain card downloading.
     * @param rawAdsUrl
     */
    private void fetchAdMarketInfo(String rawAdsUrl) {
        adService.loadAdListings(rawAdsUrl)
                .subscribeOn(Schedulers.io())
                .cache()
                .map(AdListingResponse::getAdlistings)
                .map(adListingDTOs -> adListingDTOs.get(0))
                .subscribe(adListing -> {
                    currentCard.setClickUrl(adListing.getClickUrl());
                    currentCard.setImpressionUrl(adListing.getImpressionUrl());
                }, throwable -> Log.e(TAG, "Could not load ads data for card"), this::track);
    }

    public void setTrackInfo(Card card, String suggestion) {
        this.currentCard = card;
        this.currentSuggestion = suggestion;
    }

    public void sendTrackInfo() {
        if (currentCard != null && !currentCard.hasBeenTracked()) {
            track();
        }
    }

    private void track() {
        trackHelper.trackImpression(currentCard.getImpressionUrl());
    }
}
