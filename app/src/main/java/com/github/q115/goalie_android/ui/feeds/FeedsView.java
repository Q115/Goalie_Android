package com.github.q115.goalie_android.ui.feeds;

import com.github.q115.goalie_android.models.GoalFeed;
import com.github.q115.goalie_android.ui.BaseView;

import java.util.ArrayList;

/**
 * Created by Qi on 8/10/2017.
 */

public interface FeedsView extends BaseView<FeedsPresenter> {
    void syncComplete(boolean isSuccessful, String errMsg);

    void reload();
}
