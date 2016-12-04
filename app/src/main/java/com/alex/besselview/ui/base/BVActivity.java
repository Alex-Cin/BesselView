package com.alex.besselview.ui.base;

import org.alex.baseui.AbsActivity;

/**
 * 作者：Alex
 * 时间：2016年12月01日
 * 简述：
 */

public abstract class BVActivity extends AbsActivity {
    @Override
    public int getBodyViewId() {
        return 0;
    }

    @Override
    public void showPreviewLayout() {

    }

    @Override
    public void showLoadingLayout() {

    }

    @Override
    public void showBodyLayout() {

    }

    @Override
    public void showEmptyLayout() {

    }

    @Override
    public void showFailLayout() {

    }
}
