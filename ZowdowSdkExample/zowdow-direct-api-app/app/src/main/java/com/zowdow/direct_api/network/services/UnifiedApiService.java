package com.zowdow.direct_api.network.services;

import com.zowdow.direct_api.network.models.abs.BaseResponse;
import com.zowdow.direct_api.network.models.unified.UnifiedDTO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

public interface UnifiedApiService {
    @GET("unified")
    Observable<BaseResponse<UnifiedDTO>> loadSuggestions(@QueryMap Map<String, Object> queryMap);

    @GET
    Call<Void> performTracking(@Url String urlToTrack);
}