package com.zowdow.direct_api.network.models.unified.suggestions;

import android.support.annotation.StringDef;

import com.zowdow.direct_api.utils.constants.CardFormats;

/**
 * Represents existing card formats, supported by server.
 */
@StringDef({CardFormats.CARD_FORMAT_INLINE, CardFormats.CARD_FORMAT_STAMP, CardFormats.CARD_FORMAT_TICKET, CardFormats.CARD_FORMAT_ANIMATED_GIF})
public @interface CardFormat {}