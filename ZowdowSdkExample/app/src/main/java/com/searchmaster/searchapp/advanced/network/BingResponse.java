package com.searchmaster.searchapp.advanced.network;

import java.util.ArrayList;
import java.util.List;

public class BingResponse extends ArrayList {
    public String getFragment() {
        return (String) get(0);
    }

    public List<String> getSuggestions() {
        return (List<String>) get(1);
    }

    public String getSuggestion(int position) {
        return (String) ((ArrayList) get(1)).get(position);
    }
}
