package com.github.q115.goalie_android.ui.my_goals;

import android.graphics.Bitmap;

import com.github.q115.goalie_android.ui.BaseView;

/**
 * Created by Qi on 8/4/2017.
 */

public interface MyGoalsView extends BaseView<MyGoalsPresenter> {
    void showFABMenu();

    void closeFABMenu();

    void showDialog(String title, String end, String start, String reputation, String encouragement, String referee, Bitmap profileImage);

    void showRefresher(boolean shouldShow);

    void syncError(String msg);

    void syncSuccess();

    void reload();
}
