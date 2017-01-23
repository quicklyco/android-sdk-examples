package com.zowdow.direct_api.ui.adapters;

/**
 * Interface definition for a callback to be invoked when a card is clicked.
 */
public interface OnCardClickListener {
    /**
     * Called when card has been tapped
     *
     * @param suggestionTitle   suggestion text
     * @param webUrl            can be a deep link
     */
    void onCardClicked(String webUrl, String suggestionTitle);
}
