package com.zowdow.direct_api.injection.components;

import com.zowdow.direct_api.injection.modules.NetworkModule;
import com.zowdow.direct_api.network.services.InitApiService;
import com.zowdow.direct_api.network.services.TrackingApiService;
import com.zowdow.direct_api.network.services.UnifiedApiService;
import com.zowdow.direct_api.presenters.home.HomeDemoPresenter;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@Singleton
@Component(modules = NetworkModule.class)
public interface NetworkComponent {
    void inject(HomeDemoPresenter presenter);
    OkHttpClient okHttpClient();
    Retrofit.Builder retrofitBuilder();
    InitApiService initApiService();
    UnifiedApiService unifiedApiService();
    TrackingApiService trackingApiService();
}
