package com.zowdow.android.example.widget;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.zowdow.android.example.R;

import co.zowdow.sdk.android.OnCardClickListener;
import co.zowdow.sdk.android.Zowdow;

public class DiscoveryWidgetPagerActivity extends AppCompatActivity implements OnCardClickListener {
    private ViewPager mDiscoveryPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery_widget_pager);

        mDiscoveryPager = (ViewPager) findViewById(R.id.discovery_widget_pager);

        initializeZowdow();
        attachDiscoveryWidget();
    }

    private void initializeZowdow() {
        Zowdow.initialize(this);
    }

    protected void attachDiscoveryWidget() {
        DiscoveryPagerAdapter pagerAdapter = new DiscoveryPagerAdapter(getSupportFragmentManager());
        mDiscoveryPager.setOffscreenPageLimit(pagerAdapter.getCount());
        mDiscoveryPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onCardClick(String suggestion, String cardUrl) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(cardUrl));
        startActivity(i);
    }
}
