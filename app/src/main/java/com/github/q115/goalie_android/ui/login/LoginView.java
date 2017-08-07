package com.github.q115.goalie_android.ui.login;

import com.github.q115.goalie_android.ui.BaseView;

/**
 * Created by Qi on 8/6/2017.
 */

public interface LoginView extends BaseView<LoginPresenter> {
    void showRegisterError(String msg);

    void registerSuccess(String msg);

    void updateProgress(boolean shouldShow);
}
