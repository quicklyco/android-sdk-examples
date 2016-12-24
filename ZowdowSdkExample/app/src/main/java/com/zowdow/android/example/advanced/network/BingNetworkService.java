package com.zowdow.android.example.advanced.network;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface BingNetworkService {
    String BASE_URL = "http://api.bing.com/";

    @GET("osjson.aspx")
    Call<BingResponse> bing(@Query(value = "query", encoded = true) String query);
}
