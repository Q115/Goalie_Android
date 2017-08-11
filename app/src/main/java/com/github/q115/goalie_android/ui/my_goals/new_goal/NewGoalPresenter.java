package com.github.q115.goalie_android.ui.my_goals.new_goal;

import android.support.annotation.NonNull;

import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsView;

/**
 * Created by Qi on 8/11/2017.
 */

public class NewGoalPresenter implements BasePresenter {
    private final NewGoalView mNewGoalView;

    public NewGoalPresenter(@NonNull NewGoalView newGoalView) {
        mNewGoalView = newGoalView;
        mNewGoalView.setPresenter(this);
    }

    public void start() {
    }
}
