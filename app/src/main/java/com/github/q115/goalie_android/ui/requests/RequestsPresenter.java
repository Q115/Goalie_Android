package com.github.q115.goalie_android.ui.requests;

import android.support.annotation.NonNull;

import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsView;

/**
 * Created by Qi on 8/10/2017.
 */

public class RequestsPresenter implements BasePresenter {
    private final RequestsView mRequestsView;

    public RequestsPresenter(@NonNull RequestsView requestsView) {
        mRequestsView = requestsView;
    }

    public void start() {
    }

    public void reload() {
    }
}