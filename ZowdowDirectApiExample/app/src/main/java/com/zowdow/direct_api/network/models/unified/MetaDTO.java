package com.zowdow.direct_api.network.models.unified;

import com.google.gson.annotations.SerializedName;

/**
 * Represents Meta
 */
public class MetaDTO {
    @SerializedName("rid") private String mRid;

    public String getRid() {
        return mRid;
    }
}
