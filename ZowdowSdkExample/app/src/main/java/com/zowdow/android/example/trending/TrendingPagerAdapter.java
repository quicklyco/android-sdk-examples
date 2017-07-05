package com.zowdow.android.example.trending;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import co.zowdow.sdk.android.TrendingConfiguration;
import co.zowdow.sdk.android.ZowdowTrendingFragment;

class TrendingPagerAdapter extends FragmentStatePagerAdapter {
    private static final int SIMPLE_WIDGET = 0;
    private static final int CUSTOMIZED_WIDGET = 1;
    private static final int WIDGETS_COUNT = 2;

    TrendingPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case SIMPLE_WIDGET:
                return ZowdowTrendingFragment.newInstance();
            case CUSTOMIZED_WIDGET:
                return ZowdowTrendingFragment
                        .newInstance(new TrendingConfiguration()
                                .showCategoryTitle(false)
                        );
        }
        return new Fragment();
    }

    @Override
    public int getCount() {
        return WIDGETS_COUNT;
    }
}