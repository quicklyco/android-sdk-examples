package com.searchmaster.searchapp.advanced;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import co.zowdow.sdk.android.OnCardClickListener;
import co.zowdow.sdk.android.OnSuggestionClickListener;
import co.zowdow.sdk.android.Suggestion;
import co.zowdow.sdk.android.SuggestionData;
import co.zowdow.sdk.android.Zowdow;

public class AdvancedDemoAdapter extends BaseAdapter {
    private static final int VIEW_TYPE_Q = 0;
    private static final int VIEW_TYPE_S = 1;

    private Zowdow.Params mParams;

    private List<? extends SuggestionData> mSuggestions;

    public AdvancedDemoAdapter(OnSuggestionClickListener onSuggestionClickListener, OnCardClickListener onCardClickListener) {
        mParams = new Zowdow.Params();
        mParams.setCarouselType(Zowdow.CAROUSEL_LINEAR_FULL);
        mParams.setOnSuggestionClickListener(onSuggestionClickListener);
        mParams.setOnCardClickListener(onCardClickListener);
    }

    @Override
    public int getCount() {
        return mSuggestions == null ? 0 : mSuggestions.size();
    }

    @Override
    public SuggestionData getItem(int position) {
        return mSuggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return mSuggestions.get(position) instanceof Suggestion ? VIEW_TYPE_Q : VIEW_TYPE_S;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == VIEW_TYPE_Q) {
            convertView = Zowdow.bindView(
                    position,
                    convertView,
                    parent,
                    (Suggestion) mSuggestions.get(position),
                    mParams);
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            ((TextView) (convertView.findViewById(android.R.id.text1))).setText(mSuggestions.get(position).getSuggestion());
        }

        return convertView;
    }

    public void setItems(List<? extends SuggestionData> suggestions) {
        mSuggestions = suggestions;
        notifyDataSetChanged();
    }
}
