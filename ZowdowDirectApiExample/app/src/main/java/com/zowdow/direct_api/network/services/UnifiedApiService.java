package com.zowdow.direct_api.network.services;

import com.zowdow.direct_api.network.models.base.BaseResponse;
import com.zowdow.direct_api.network.models.unified.UnifiedDTO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Service which is responsible for suggestions loading, and other operations related to
 * Zowdow API.
 */
public interface UnifiedApiService {
    /**
     * Loads suggestions for a given keyword.
     * @param queryMap Map of request parameters. Same as for init API + suggestions filtering params.
     * @return
     */
    @GET("unified")
    Observable<BaseResponse<UnifiedDTO>> loadSuggestions(@QueryMap Map<String, Object> queryMap);

    /**
     * Sends tracking call to Unified API.
     * Needs to be called when there's some info that needs to be tracked: in case of this demo app,
     * cards' impressions and clicks.
     * @param urlToTrack
     * @return
     */
    @GET
    Call<Void> performTracking(@Url String urlToTrack);
}