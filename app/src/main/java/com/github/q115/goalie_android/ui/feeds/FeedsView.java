package com.github.q115.goalie_android.ui.feeds;

import com.github.q115.goalie_android.ui.BaseView;

/**
 * Created by Qi on 8/10/2017.
 */

public interface FeedsView extends BaseView<FeedsPresenter> {
    void showRefresher(boolean shouldShow);

    void syncError(String msg);

    void syncSuccess();

    void reload();
}
