package com.github.q115.goalie_android.ui.my_goals.new_goal;

import com.github.q115.goalie_android.ui.BaseView;

/**
 * Created by Qi on 8/11/2017.
 */

public interface NewGoalView extends BaseView<NewGoalPresenter> {
    void updateTime(boolean isStart, String date);

    void updateWager(long wagering, long total, int percent);

    void updateReferee(boolean isFromSpinner, int position);

    void resetReferee(boolean isFromSpinner);

    void onSetGoal(boolean isSuccessful, String errMsg);

    void updateProgress(boolean shouldShow);
}
