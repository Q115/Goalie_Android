package com.github.q115.goalie_android.ui.my_goals;

import android.support.annotation.NonNull;

import com.github.q115.goalie_android.https.RESTSync;
import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.utils.UserHelper;

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
        RESTSync sm = new RESTSync(UserHelper.getInstance().getOwnerProfile().username);
        sm.setListener(new RESTSync.Listener() {
            @Override
            public void onSuccess() {
                mMyGoalsView.showRefresher(false);

                //TODO
                mMyGoalsView.syncSuccess();
            }

            @Override
            public void onFailure(String errMsg) {
                mMyGoalsView.showRefresher(false);
                mMyGoalsView.syncError(errMsg);
            }
        });
        sm.execute();
    }

    public void reload() {
        mMyGoalsView.reload();
    }
}
