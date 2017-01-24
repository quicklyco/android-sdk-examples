package com.zowdow.direct_api.injection.modules;

import com.google.gson.GsonBuilder;
import com.zowdow.direct_api.network.ApiBaseUrls;
import com.zowdow.direct_api.network.services.AdMarketPlaceService;
import com.zowdow.direct_api.network.services.InitApiService;
import com.zowdow.direct_api.network.services.UnifiedApiService;
import com.zowdow.direct_api.utils.helpers.tracking.TrackHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Module which provides an access to network services.
 */
@Module
public class NetworkModule {
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .build();
    }

    @Provides
    @Singleton
    Retrofit.Builder provideRetrofitBuilder(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
    }

    @Provides
    @Singleton
    InitApiService provideInitApiService(Retrofit.Builder retrofitBuilder) {
        return retrofitBuilder
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeSpecialFloatingPointValues().create()))
                .baseUrl(ApiBaseUrls.INIT_API).build()
                .create(InitApiService.class);
    }

    @Provides
    @Singleton
    UnifiedApiService provideUnifiedApiService(Retrofit.Builder retrofitBuilder) {
        return retrofitBuilder
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeSpecialFloatingPointValues().create()))
                .baseUrl(ApiBaseUrls.UNIFIED_API).build()
                .create(UnifiedApiService.class);
    }

    @Provides
    @Singleton
    AdMarketPlaceService provideAdMarketPlaceService(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .baseUrl(ApiBaseUrls.UNIFIED_API).build()
                .create(AdMarketPlaceService.class);
    }

    @Provides
    @Singleton
    TrackHelper provideTrackHelper(UnifiedApiService unifiedApiService) {
        return new TrackHelper(unifiedApiService);
    }
}