package com.github.q115.goalie_android.ui.my_goals;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.github.q115.goalie_android.https.RESTSync;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.GoalFeed;
import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import java.util.ArrayList;
import java.util.HashMap;

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
        if (isFABOpen)
            showFABMenu();
        else
            closeFABMenu();
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

    public void onRefresherRefresh() {
        RESTSync sm = new RESTSync(UserHelper.getInstance().getOwnerProfile().username, PreferenceHelper.getInstance().getLastSyncedTimeEpoch());
        sm.setListener(new RESTSync.Listener() {
            @Override
            public void onSuccess() {
                mMyGoalsView.syncComplete(true, "");
            }

            @Override
            public void onFailure(String errMsg) {
                mMyGoalsView.syncComplete(false, errMsg);
            }
        });
        sm.execute();
    }

    public void showDialog(String title, String end, String start, String reputation, String encouragement,
                           String referee, Bitmap profileImage, Goal.GoalCompleteResult goalCompleteResult, String guid) {
        mMyGoalsView.showDialog(title, end, start, reputation, encouragement, referee, profileImage, goalCompleteResult, guid);
    }

    public void reload() {
        mMyGoalsView.reload();
    }
}
