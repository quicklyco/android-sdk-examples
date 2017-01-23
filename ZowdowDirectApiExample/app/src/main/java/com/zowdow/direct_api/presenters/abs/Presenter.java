package com.zowdow.direct_api.presenters.abs;

/**
 * Contract-interface which defines methods required to be overriden in each presenter.
 * @param <V>
 */
public interface Presenter<V> {
    void onViewAttached(V view);
    void onViewDetached();
}
