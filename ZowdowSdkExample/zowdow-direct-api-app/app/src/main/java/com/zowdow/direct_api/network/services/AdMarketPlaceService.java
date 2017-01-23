package com.zowdow.direct_api.network.services;

import com.zowdow.direct_api.network.models.admarketplace.AdListingResponse;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

public interface AdMarketPlaceService {
    @GET
    Observable<AdListingResponse> loadAdListings(@Url String adsUrl);
}
