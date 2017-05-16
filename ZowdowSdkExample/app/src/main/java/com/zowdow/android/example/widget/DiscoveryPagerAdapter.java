package com.zowdow.android.example.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import co.zowdow.sdk.android.DiscoveryCategory;
import co.zowdow.sdk.android.DiscoveryWidgetConfiguration;
import co.zowdow.sdk.android.ZowdowDiscoveryFragment;

class DiscoveryPagerAdapter extends FragmentStatePagerAdapter {
    private static final int SIMPLE_WIDGET = 0;
    private static final int CUSTOMIZED_WIDGET = 1;
    private static final int WIDGETS_COUNT = 2;

    DiscoveryPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case SIMPLE_WIDGET:
                return ZowdowDiscoveryFragment.newInstance();
            case CUSTOMIZED_WIDGET:
                return ZowdowDiscoveryFragment
                        .newInstance(new DiscoveryWidgetConfiguration()
                                .cardLimit(8)
                                .categories(DiscoveryCategory.MUSIC,
                                        DiscoveryCategory.APPS,
                                        DiscoveryCategory.PRODUCTS,
                                        DiscoveryCategory.FOOD)
                        );
        }
        return new Fragment();
    }

    @Override
    public int getCount() {
        return WIDGETS_COUNT;
    }
}