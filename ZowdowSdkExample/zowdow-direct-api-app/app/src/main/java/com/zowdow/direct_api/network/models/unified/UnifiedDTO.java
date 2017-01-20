package com.zowdow.direct_api.network.models.unified;

import com.google.gson.annotations.SerializedName;

public class UnifiedDTO {
    @SerializedName("suggestion") private SuggestionDTO mSuggestion;

    public SuggestionDTO getSuggestion() {
        return mSuggestion;
    }

    public void setSuggestion(SuggestionDTO suggestion) {
        mSuggestion = suggestion;
    }
}
