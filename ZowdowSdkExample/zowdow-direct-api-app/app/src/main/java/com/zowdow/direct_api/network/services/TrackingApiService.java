package com.zowdow.direct_api.network.services;

import com.google.gson.JsonObject;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface TrackingApiService {
    @POST("log")
    Observable<Object> track(@Body JsonObject body);
}
