package com.zowdow.direct_api.network.models.unified;

import com.google.gson.annotations.SerializedName;

/**
 * Represents Meta
 */
public class MetaDTO {
    @SerializedName("status") private String mStatus;
    @SerializedName("count") private         int    mCount;
    @SerializedName("rid") private String mRid;
    @SerializedName("ttl") private           long   mTtl;
    @SerializedName("carousel_type") private String mCarouselType;

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public String getRid() {
        return mRid;
    }

    public void setRid(String rid) {
        mRid = rid;
    }

    public long getTtl() {
        return mTtl;
    }

    public void setTtl(long ttl) {
        mTtl = ttl;
    }

    public String getCarouselType() {
        return mCarouselType;
    }

    public void setCarouselType(String carouselType) {
        this.mCarouselType = carouselType;
    }
}
