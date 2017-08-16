package com.github.q115.goalie_android.ui.requests;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.github.q115.goalie_android.https.RESTSync;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.GoalFeed;
import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsView;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import java.util.ArrayList;

/**
 * Created by Qi on 8/10/2017.
 */

public class RequestsPresenter implements BasePresenter {
    private final RequestsView mRequestsView;

    public RequestsPresenter(@NonNull RequestsView requestsView) {
        mRequestsView = requestsView;
        mRequestsView.setPresenter(this);
    }

    public void start() {
    }

    public void reload() {
        mRequestsView.reload();
    }

    public void onRefresherRefresh() {
        RESTSync sm = new RESTSync(UserHelper.getInstance().getOwnerProfile().username, PreferenceHelper.getInstance().getLastSyncedTimeEpoch());
        sm.setListener(new RESTSync.Listener() {
            @Override
            public void onSuccess() {
                mRequestsView.syncComplete(true, "");
            }

            @Override
            public void onFailure(String errMsg) {
                mRequestsView.syncComplete(false, errMsg);
            }
        });
        sm.execute();
    }

    public void showDialog(String title, String end, String start, String reputation, String encouragement,
                           String referee, Bitmap profileImage, Goal.GoalCompleteResult goalCompleteResult,String guid) {
        mRequestsView.showDialog(title, end, start, reputation, encouragement, referee, profileImage, goalCompleteResult, guid);
    }
}