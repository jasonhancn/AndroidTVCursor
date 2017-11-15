package com.github.jasonhancn.tvcursor;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

class TvCursorManager {
    private TvCursorView mCursorView;
    private ViewGroup mParentView;

    TvCursorManager(ViewGroup parentView) {
        mParentView = parentView;
        Context mContext = parentView.getContext();
        mCursorView = new TvCursorView(mContext, parentView);
        mCursorView.setVisibility(View.GONE);
    }

    boolean isShowCursor() {
        return mCursorView.isShowCursor();
    }

    void setShowCursor(boolean is) {
        if (isShowCursor() != is) {
            mCursorView.setShowCursor(is);
            if (is) {
                mCursorView.bringToFront();
                mCursorView.setVisibility(View.VISIBLE);
            } else {
                mCursorView.setVisibility(View.GONE);
            }
            mCursorView.requestLayout();
        }
    }

    boolean onDpadClicked(KeyEvent event) {
        return mCursorView.onDpadClicked(event);
    }

    void showCursorView() {
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (mCursorView != null) {
            mParentView.addView(mCursorView, lp);
        }
    }

    void setScrollTargetView(View scrollTargetView) {
        mCursorView.setScrollTargetView(scrollTargetView);
    }

    void setCursorResource(int pointerResource, int pointerSize, int pointerX, int pointerY) {
        mCursorView.setPointerResource(pointerResource, pointerX, pointerY, pointerSize);
    }

    void setCursorSize(int pointerSize) {
        mCursorView.setPointerSize(pointerSize);
    }
}
