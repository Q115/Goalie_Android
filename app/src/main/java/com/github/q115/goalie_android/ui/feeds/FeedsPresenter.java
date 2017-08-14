package com.github.q115.goalie_android.ui.feeds;

import android.support.annotation.NonNull;

import com.github.q115.goalie_android.https.RESTGetFeeds;
import com.github.q115.goalie_android.models.GoalFeed;
import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.utils.UserHelper;

import java.util.ArrayList;

/**
 * Created by Qi on 8/10/2017.
 */

public class FeedsPresenter implements BasePresenter {
    private final FeedsView mFeedsView;

    private ArrayList<GoalFeed> mGoalFeedArrayList;

    public ArrayList<GoalFeed> getGoalFeedArrayList() {
        return mGoalFeedArrayList;
    }


    public FeedsPresenter(@NonNull FeedsView feedsView) {
        mFeedsView = feedsView;
        mFeedsView.setPresenter(this);
    }

    public void start() {
        RESTGetFeeds sm = new RESTGetFeeds(UserHelper.getInstance().getOwnerProfile().username);
        sm.setListener(new RESTGetFeeds.Listener() {
            @Override
            public void onSuccess(ArrayList<GoalFeed> goalFeedArrayList) {
                mGoalFeedArrayList = goalFeedArrayList;
                mFeedsView.getFeedComplete(true, "");
            }

            @Override
            public void onFailure(String errMsg) {
                mFeedsView.getFeedComplete(false, errMsg);
            }
        });
        sm.execute();
    }

    public void reload() {
    }
}
