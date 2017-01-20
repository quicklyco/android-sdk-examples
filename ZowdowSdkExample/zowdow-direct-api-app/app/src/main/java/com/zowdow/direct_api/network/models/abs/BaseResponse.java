package com.zowdow.direct_api.network.models.abs;

import com.google.gson.annotations.SerializedName;
import com.zowdow.direct_api.network.models.unified.MetaDTO;

import java.util.List;

public class BaseResponse<T> {
    @SerializedName("_meta") private MetaDTO mMeta;
    @SerializedName("records") private List<T> mRecords;

    public MetaDTO getMeta() {
        return mMeta;
    }

    public void setMeta(MetaDTO meta) {
        mMeta = meta;
    }

    public List<T> getRecords() {
        return mRecords;
    }

    public void setRecords(List<T> records) {
        mRecords = records;
    }
}
