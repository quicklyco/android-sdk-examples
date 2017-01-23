package com.zowdow.direct_api.network.services;

import com.zowdow.direct_api.network.models.admarketplace.AdListingResponse;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Service that fetches AdMarketPlace ad listings for special cards with action type ad_call.
 */
public interface AdMarketPlaceService {
    /**
     * Loads ad listings XML for specific AdMarketPlace card.
     * @param adsUrl
     * @return
     */
    @GET
    Observable<AdListingResponse> loadAdListings(@Url String adsUrl);
}
