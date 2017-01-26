package com.zowdow.direct_api.network.models.unified;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.zowdow.direct_api.network.models.unified.suggestions.Card;
import com.zowdow.direct_api.network.models.unified.suggestions.CardFormat;
import com.zowdow.direct_api.network.models.unified.suggestions.Suggestion;

import java.util.ArrayList;
import java.util.List;
/**
 * Represents Suggestion
 */
public class SuggestionDTO implements Parcelable {
    @SerializedName("id") private int mId;
    @SerializedName("cardCount") private int mCardCount;
    @SerializedName("suggRank") private int mSuggRank;
    @SerializedName("suggestion") private String mSuggestion;
    @SerializedName("queryFragment") private String mQueryFragment;
    @SerializedName("cards") private JsonArray mCards;
    private String mRid;
    private String mCarouselType;
    private String mCardFormat;
    private long mTtl;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getSuggestion() {
        return mSuggestion;
    }

    public void setSuggestion(String suggestion) {
        mSuggestion = suggestion;
    }

    public JsonArray getCards() {
        return mCards;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mCardCount);
        dest.writeInt(mSuggRank);
        dest.writeString(mSuggestion);
        dest.writeString(mQueryFragment);
        dest.writeString(mCards.toString());
        dest.writeString(mRid);
        dest.writeLong(mTtl);
        dest.writeString(mCarouselType);
        dest.writeString(mCardFormat);
    }

    public static final Creator<SuggestionDTO> CREATOR = new Creator<SuggestionDTO>() {
        @Override
        public SuggestionDTO createFromParcel(Parcel source) {
            return new SuggestionDTO(source);
        }

        @Override
        public SuggestionDTO[] newArray(int size) {
            return new SuggestionDTO[size];
        }
    };

    private SuggestionDTO(Parcel source) {
        mId = source.readInt();
        mCardCount = source.readInt();
        mSuggRank = source.readInt();
        mSuggestion = source.readString();
        mQueryFragment = source.readString();
        mCards = new JsonParser().parse(source.readString()).getAsJsonArray();
        mRid = source.readString();
        mTtl = source.readLong();
        mCarouselType = source.readString();
        mCardFormat = source.readString();
    }

    public Suggestion toSuggestion(final String rid, String carouselType, String cardFormat) {
        Suggestion suggestion = new Suggestion();
        suggestion.setId(mId);
        suggestion.setRid(rid);
        suggestion.setCarouselType(carouselType);
        suggestion.setSuggestion(mSuggestion);
        suggestion.setSuggRank(mSuggRank);
        suggestion.setQueryFragment(mQueryFragment);
        suggestion.setCardFormat(TextUtils.isEmpty(mCardFormat) ? cardFormat : mCardFormat);

        List<Card> cards = new ArrayList<>();
        Gson gson = new Gson();
        JsonArray cardsArray = getCards();
        for (JsonElement jsonCard : cardsArray) {
            Card card = gson.fromJson(jsonCard, Card.class);
            card.setRid(rid);
            cards.add(card);
        }
        suggestion.setCards(cards);
        return suggestion;
    }
}
