package com.zowdow.android.example.trending;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zowdow.android.example.R;

import co.zowdow.sdk.android.OnCardClickListener;
import co.zowdow.sdk.android.ZowdowTrendingFragment;

public class DiscoveryWidgetActivity extends AppCompatActivity implements OnCardClickListener {
    private static final String FRAGMENT_TAG = "d_widget";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery_widget);
        initializeZowdow();
        attachDiscoveryWidget();
    }

    private void initializeZowdow() {
    }

    protected void attachDiscoveryWidget() {
        getSupportFragmentManager().beginTransaction()
                .add(getContainerId(), ZowdowTrendingFragment.newInstance(), FRAGMENT_TAG)
                .commit();
    }

    @IdRes private int getContainerId() {
        return R.id.discovery_widget_container;
    }

    @Override
    public void onCardClick(String suggestion, String cardUrl) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(cardUrl));
        startActivity(i);
    }
}
