package com.zowdow.direct_api.presenters.home;

import com.zowdow.direct_api.network.models.unified.suggestions.Suggestion;

import java.util.List;

public interface IHomeView {
    /**
     * Called as soon as user authorized with init API successfully.
     */
    void onApiInitialized();

    /**
     * Invoked when new suggestions list is loaded.
     * @param suggestions
     */
    void onSuggestionsLoaded(List<Suggestion> suggestions);

    /**
     * Called after activity recreation if there already was a keyword, typed in text field.
     * @param searchQuery
     */
    void onRestoreSearchQuery(String searchQuery);

    /**
     * Called if suggestions were not loaded due to connectivity or other reasons.
     */
    void onSuggestionsLoadingFailed();
}
