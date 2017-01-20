package com.zowdow.direct_api.presenters.abs;

public interface PresenterFactory<T extends Presenter> {
    T createPresenter();
}