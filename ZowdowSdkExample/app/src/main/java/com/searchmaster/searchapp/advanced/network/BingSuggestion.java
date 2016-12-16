package com.searchmaster.searchapp.advanced.network;

import co.zowdow.sdk.android.SuggestionData;

public class BingSuggestion implements SuggestionData {
    private final String mSuggestion;

    public BingSuggestion(String suggestion) {
        mSuggestion = suggestion;
    }

    @Override
    public String getSuggestion() {
        return mSuggestion;
    }

    @Override
    public String toString() {
        return mSuggestion;
    }
}
