package com.zowdow.direct_api.network.services;

import com.zowdow.direct_api.network.models.init.InitResponse;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface InitApiService {
    @GET("init")
    Observable<InitResponse> init(@QueryMap Map<String, Object> queryMap);
}
