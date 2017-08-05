package com.github.q115.goalie_android.ui;

import android.support.annotation.NonNull;

/**
 * Created by Qi on 8/4/2017.
 */

public class MainActivityPresenter implements BasePresenter {
    private final MainActivityView mMainActivityView;

    public MainActivityPresenter(@NonNull MainActivityView mainActivityView) {
        mMainActivityView = mainActivityView;
        mMainActivityView.setPresenter(this);
    }

    public void start() {
    }
}
