package com.github.q115.goalie_android.ui.my_goals.popular_goal;

import android.support.annotation.NonNull;

import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.ui.my_goals.new_goal.NewGoalView;

/**
 * Created by Qi on 8/11/2017.
 */

public class PopularGoalPresenter implements BasePresenter {
    private final PopularGoalView mPopularGoalView;

    public PopularGoalPresenter(@NonNull PopularGoalView popularGoalView) {
        mPopularGoalView = popularGoalView;
        mPopularGoalView.setPresenter(this);
    }

    public void start() {
    }
}
