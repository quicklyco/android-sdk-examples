package com.zowdow.direct_api.ui.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zowdow.direct_api.utils.ViewUtils;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private int mGap;
    private int mEndGap;
    private int mTopBottomPadding;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int gap = mGap;
        if (((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition() == parent.getAdapter().getItemCount() - 1) {
            gap = ViewUtils.dpToPx(mEndGap);
        }
        outRect.set(0, mTopBottomPadding, gap, mTopBottomPadding);
    }

    /**
     * Set top and bottom padding
     *
     * @param topBottomPadding
     */
    public void setTopBottomPadding(int topBottomPadding) {
        mTopBottomPadding = topBottomPadding;
    }
}
