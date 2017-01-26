package com.zowdow.direct_api.network.models.base;

import com.google.gson.annotations.SerializedName;
import com.zowdow.direct_api.network.models.unified.MetaDTO;

import java.util.List;

public class BaseResponse<T> {
    @SerializedName("_meta") private MetaDTO mMeta;
    @SerializedName("records") private List<T> mRecords;

    public MetaDTO getMeta() {
        return mMeta;
    }

    public List<T> getRecords() {
        return mRecords;
    }
}
