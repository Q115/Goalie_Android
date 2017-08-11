package com.github.q115.goalie_android.ui.feeds;

import android.support.annotation.NonNull;

import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.ui.requests.RequestsView;

/**
 * Created by Qi on 8/10/2017.
 */

public class FeedsPresenter implements BasePresenter {
    private final FeedsView mFeedsView;

    public FeedsPresenter(@NonNull FeedsView feedsView) {
        mFeedsView = feedsView;
    }

    public void start() {
    }

    public void reload() {
    }
}
