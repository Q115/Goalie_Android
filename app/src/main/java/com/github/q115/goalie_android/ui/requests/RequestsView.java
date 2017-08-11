package com.github.q115.goalie_android.ui.requests;

import com.github.q115.goalie_android.ui.BaseView;

/**
 * Created by Qi on 8/10/2017.
 */

public interface RequestsView extends BaseView<RequestsPresenter>{
    void showRefresher(boolean shouldShow);

    void syncError(String msg);

    void syncSuccess();

    void reload();
}
