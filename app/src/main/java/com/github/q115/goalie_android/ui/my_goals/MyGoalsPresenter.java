package com.github.q115.goalie_android.ui.my_goals;

import android.support.annotation.NonNull;

import com.github.q115.goalie_android.ui.BasePresenter;

/**
 * Created by Qi on 8/4/2017.
 */

public class MyGoalsPresenter implements BasePresenter {
    private final MyGoalsView mMyGoalsView;
    private boolean isFABOpen;

    public MyGoalsPresenter(@NonNull MyGoalsView myGoalsView) {
        isFABOpen = false;
        mMyGoalsView = myGoalsView;
        mMyGoalsView.setPresenter(this);
    }

    public void start() {

    }

    public void toggleFAB() {
        if (!isFABOpen) {
            showFABMenu();
        } else {
            closeFABMenu();
        }
    }

    public void closeFABMenu() {
        isFABOpen = false;
        mMyGoalsView.closeFABMenu();
    }

    public void showFABMenu() {
        isFABOpen = true;
        mMyGoalsView.showFABMenu();
    }

    public boolean isFABOpen() {
        return isFABOpen;
    }
}
