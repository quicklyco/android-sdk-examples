package com.zowdow.direct_api.network.models.abs;

public class ResultApiCall<T> {
    public final T      response;
    public final String api;
    public final String url;
    public final String reason;

    public ResultApiCall(T response, String api, String url, String reason) {
        this.response = response;
        this.api = api;
        this.url = url;
        this.reason = reason;
    }
}
