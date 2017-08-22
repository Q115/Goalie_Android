package com.github.q115.goalie_android.ui.feeds;

import android.support.annotation.NonNull;

import com.github.q115.goalie_android.https.RESTSync;
import com.github.q115.goalie_android.models.GoalFeed;
import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import java.util.ArrayList;

/**
 * Created by Qi on 8/10/2017.
 */

public class FeedsPresenter implements BasePresenter {
    private final FeedsView mFeedsView;

    public FeedsPresenter(@NonNull FeedsView feedsView) {
        mFeedsView = feedsView;
        mFeedsView.setPresenter(this);
    }

    public void onRefresherRefresh() {
        RESTSync sm = new RESTSync(UserHelper.getInstance().getOwnerProfile().username, PreferenceHelper.getInstance().getLastSyncedTimeEpoch());
        sm.setListener(new RESTSync.Listener() {
            @Override
            public void onSuccess() {
                mFeedsView.syncComplete(true, "");
            }

            @Override
            public void onFailure(String errMsg) {
                mFeedsView.syncComplete(false, errMsg);
            }
        });
        sm.execute();
    }

    public void start() {
    }

    public void reload() {
        mFeedsView.reload();
    }
}
