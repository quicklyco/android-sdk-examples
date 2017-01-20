package com.zowdow.direct_api.ui.sections.abs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.zowdow.direct_api.presenters.abs.Presenter;
import com.zowdow.direct_api.presenters.abs.PresenterFactory;
import com.zowdow.direct_api.presenters.abs.PresenterLoader;

public abstract class BaseActivity<P extends Presenter<V>, V> extends AppCompatActivity {
    private static final int LOADER_ID = 101;

    private Presenter<V> presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);
    }

    private LoaderManager.LoaderCallbacks<P> loaderCallbacks = new LoaderManager.LoaderCallbacks<P>() {
        @Override
        public Loader<P> onCreateLoader(int id, Bundle args) {
            return new PresenterLoader<>(BaseActivity.this, getPresenterFactory());
        }

        @Override
        public void onLoadFinished(Loader<P> loader, P presenter) {
            BaseActivity.this.presenter = presenter;
            onPresenterPrepared(presenter);
        }

        @Override
        public void onLoaderReset(Loader<P> loader) {
            BaseActivity.this.presenter = null;
        }
    };

    @Override
    protected void onDestroy() {
        presenter.onViewDetached();
        super.onDestroy();
    }

    @NonNull
    protected abstract PresenterFactory<P> getPresenterFactory();

    protected abstract void onPresenterPrepared(@NonNull P presenter);

    protected void onPresenterDestroyed() {}
}
