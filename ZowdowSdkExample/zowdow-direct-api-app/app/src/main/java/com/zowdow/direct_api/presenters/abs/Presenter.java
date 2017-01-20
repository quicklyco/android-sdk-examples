package com.zowdow.direct_api.presenters.abs;

public interface Presenter<V> {
    void onViewAttached(V view);
    void onViewDetached();
}
