package com.github.q115.goalie_android.ui.requests;

import android.graphics.Bitmap;

import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.ui.BaseView;

/**
 * Created by Qi on 8/10/2017.
 */

public interface RequestsView extends BaseView<RequestsPresenter> {
    void syncComplete(boolean isSuccessful, String errMsg);

    void reload();

    void showDialog(String title, String end, String start, String reputation, String encouragment,
                    String referee, Bitmap profileImage, Goal.GoalCompleteResult goalCompleteResult, String guid);
}
