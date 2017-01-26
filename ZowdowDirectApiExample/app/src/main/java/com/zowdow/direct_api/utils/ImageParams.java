package com.zowdow.direct_api.utils;

import android.content.res.Resources;

import com.zowdow.direct_api.network.models.unified.suggestions.Card;
import com.zowdow.direct_api.utils.constants.CardFormats;

public class ImageParams {
    public String path;
    public int width;
    public int height;

    private ImageParams(String path, int width, int height) {
        this.path = path;
        this.width = width;
        this.height = height;
    }

    public static ImageParams create(final Card card) {
        return create(card, false);
    }

    public static ImageParams create(final Card card, final int density) {
        return create(card, density, false);
    }

    public static ImageParams create(final Card card, final boolean isTablet) {
        return create(card, Math.round(Resources.getSystem().getDisplayMetrics().density), isTablet);
    }

    public static ImageParams create(final Card card, final int density, boolean isTablet) {
        final String path;
        final int width;
        final int height;

        if (density == 1) {
            path = card.getX1();
        } else if (density == 2) {
            path = card.getX2();
        } else if (density == 3 || isTablet) {
            path = card.getX3();
        } else {
            path = card.getX4();
        }

        switch (card.getCardFormat()) {
            case CardFormats.CARD_FORMAT_ANIMATED_GIF:
                width = card.getX1w();
                height = card.getX1h();
                break;
            default:
                width = ViewUtils.dpToPx(card.getX1w());
                height = ViewUtils.dpToPx(card.getX1h());
                break;
        }

        return new ImageParams(path, width, height);
    }
}