package com.zowdow.direct_api.network.models.init;

import com.google.gson.annotations.SerializedName;
import com.zowdow.direct_api.network.models.unified.MetaDTO;

import java.util.HashMap;

public class InitResponse {
    @SerializedName("_meta") private MetaDTO mMeta;
    @SerializedName("records") private HashMap<String, Object> mRecords;

    public MetaDTO getMeta() {
        return mMeta;
    }

    public void setMeta(MetaDTO meta) {
        mMeta = meta;
    }

    public HashMap<String, Object> getRecords() {
        return mRecords;
    }

    public void setRecords(HashMap<String, Object> records) {
        mRecords = records;
    }
}
