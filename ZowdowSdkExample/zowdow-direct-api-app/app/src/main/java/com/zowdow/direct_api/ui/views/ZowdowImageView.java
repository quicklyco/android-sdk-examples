package com.zowdow.direct_api.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.zowdow.direct_api.network.models.unified.suggestions.Card;
import com.zowdow.direct_api.utils.helpers.tracking.TrackHelper;

public class ZowdowImageView extends ImageView {
    private Card currentCard;
    private String currentSuggestion;
    private String cardFormat;
    private String clickUrl;
    private String impressionUrl;

    private TrackHelper trackHelper;

    public ZowdowImageView(Context context) {
        super(context);
    }

    public ZowdowImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZowdowImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTrackInfo(Card card, String suggestion, String cardFormat, String clickUrl, String impressionUrl) {
        this.currentCard = card;
        this.currentSuggestion = suggestion;
        this.cardFormat = cardFormat;
        this.clickUrl = clickUrl;
        this.impressionUrl = impressionUrl;
    }

    public void sendTrackInfo(TrackHelper trackHelper) {
        if (currentCard != null && !currentCard.hasBeenTracked()) {
            this.trackHelper = trackHelper;
            track();
        }
    }

    private void track() {
        trackHelper.trackImpression(impressionUrl);
    }
}
