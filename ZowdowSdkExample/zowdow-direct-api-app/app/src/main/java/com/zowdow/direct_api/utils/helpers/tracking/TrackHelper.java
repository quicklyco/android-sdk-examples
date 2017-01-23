package com.zowdow.direct_api.utils.helpers.tracking;

import android.support.annotation.NonNull;
import android.util.Log;

import com.zowdow.direct_api.network.services.UnifiedApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackHelper {
    public static final String TAG = TrackHelper.class.getSimpleName();

    private UnifiedApiService unifiedApiService;

    public TrackHelper(UnifiedApiService unifiedApiService) {
        this.unifiedApiService = unifiedApiService;
    }

    public void trackImpression(@NonNull final String impressionUrl) {
        unifiedApiService.performTracking(impressionUrl).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "Tracked impression for " + impressionUrl + " successfully!");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Tracked impression for " + impressionUrl + " with failure...");
            }
        });
    }

    public void trackClick(@NonNull final String clickUrl) {
        unifiedApiService.performTracking(clickUrl).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "Tracked clicking " + clickUrl + " successfully!");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Tracked clicking " + clickUrl + " with failure...");
            }
        });
    }
}
