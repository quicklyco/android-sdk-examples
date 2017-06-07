package com.zowdow.android.example.external;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zowdow.android.example.advanced.network.BingSuggestion;

import java.util.List;

public class ExternalDemoAdapter extends BaseAdapter {
    private List<BingSuggestion> mBingSuggestions;

    @Override
    public int getCount() {
        return mBingSuggestions.size();
    }

    @Override
    public BingSuggestion getItem(int position) {
        return mBingSuggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BingSuggestion currentSuggestion = getItem(position);

        convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(currentSuggestion.getSuggestion());

        return convertView;
    }

    public void setItems(List<BingSuggestion> suggestions) {
        mBingSuggestions = suggestions;
        notifyDataSetChanged();
    }
}
