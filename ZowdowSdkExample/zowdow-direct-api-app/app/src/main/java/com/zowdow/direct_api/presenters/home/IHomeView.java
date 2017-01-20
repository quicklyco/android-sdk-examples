package com.zowdow.direct_api.presenters.home;

import com.zowdow.direct_api.network.models.unified.suggestions.Suggestion;

import java.util.List;

public interface IHomeView {
    void onApiInitialized();
    void onSuggestionsLoaded(List<Suggestion> suggestions);
    void onRestoreSearchQuery(String searchQuery);
    void onApiInitializationFailed();
}
