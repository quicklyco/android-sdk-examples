package com.zowdow.direct_api.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zowdow.direct_api.R;
import com.zowdow.direct_api.network.models.unified.suggestions.Suggestion;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for suggestions list.
 */
public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionViewHolder> {
    private Context context;
    private List<Suggestion> suggestions;
    private OnCardClickListener cardClickListener;

    public SuggestionsAdapter(Context context, List<Suggestion> suggestions) {
        this.context = context;
        this.suggestions = new ArrayList<>();
        this.suggestions.addAll(suggestions);
    }

    public SuggestionsAdapter(Context context, List<Suggestion> suggestions, OnCardClickListener cardClickListener) {
        this(context, suggestions);
        this.cardClickListener = cardClickListener;
    }

    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent, false);
        return new SuggestionViewHolder(itemView, cardClickListener);
    }

    @Override
    public void onBindViewHolder(SuggestionViewHolder holder, int position) {
        Suggestion currentSuggestion = suggestions.get(position);
        holder.setupCarousel(currentSuggestion);
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public void setSuggestions(List<Suggestion> suggestions) {
        this.suggestions.clear();
        this.suggestions.addAll(suggestions);
        notifyDataSetChanged();
    }
}
