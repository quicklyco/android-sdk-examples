package com.zowdow.direct_api;

import android.app.Application;

import com.zowdow.direct_api.network.injection.DaggerNetworkComponent;
import com.zowdow.direct_api.network.injection.NetworkComponent;

public class ZowdowDirectApplication extends Application {
    private static NetworkComponent networkComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        networkComponent = DaggerNetworkComponent.builder().build();
    }

    public static NetworkComponent getNetworkComponent() {
        return networkComponent;
    }
}