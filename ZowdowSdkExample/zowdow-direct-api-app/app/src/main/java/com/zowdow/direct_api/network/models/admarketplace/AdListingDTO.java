package com.zowdow.direct_api.network.models.admarketplace;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "listing", strict = false)
public class AdListingDTO implements Parcelable {
    @Element(name = "clickurl")
    private String mClickUrl;
    @Element(name = "impressionurl")
    private String mImpressionUrl;

    public AdListingDTO() {}

    private AdListingDTO(Parcel source) {
        mClickUrl = source.readString();
        mImpressionUrl = source.readString();
    }

    public String getClickUrl() {
        return mClickUrl;
    }

    public void setClickUrl(String mClickUrl) {
        this.mClickUrl = mClickUrl;
    }

    public String getImpressionUrl() {
        return mImpressionUrl;
    }

    public void setImpressionUrl(String impressionUrl) {
        this.mImpressionUrl = impressionUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mClickUrl);
        dest.writeString(mImpressionUrl);
    }

    public static final Parcelable.Creator<AdListingDTO> CREATOR = new Parcelable.Creator<AdListingDTO>() {
        @Override
        public AdListingDTO createFromParcel(Parcel in) {
            return new AdListingDTO(in);
        }

        @Override
        public AdListingDTO[] newArray(int size) {
            return new AdListingDTO[size];
        }
    };
}
