package com.zowdow.direct_api.network.services;

import com.zowdow.direct_api.network.models.init.InitResponse;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Service that is responsible for Zowdow API initialization.
 */
public interface InitApiService {
    /**
     * Calls API initialization request.
     * @param queryMap Request parameters that affect suggestion responses' targetting.
     * @return
     */
    @GET("init")
    Observable<InitResponse> init(@QueryMap Map<String, Object> queryMap);
}
