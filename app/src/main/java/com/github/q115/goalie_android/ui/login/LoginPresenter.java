package com.github.q115.goalie_android.ui.login;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.https.RESTRegister;
import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsView;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

/**
 * Created by Qi on 8/6/2017.
 */

public class LoginPresenter implements BasePresenter {
    private final LoginView mLoginView;

    public LoginPresenter(@NonNull LoginView loginView) {
        mLoginView = loginView;
        mLoginView.setPresenter(this);
    }

    public void start() {
    }

    public void register(Context context, String username) {
        if (UserHelper.isUsernameValid(username)) {
            mLoginView.updateProgress(true);
            final String welcome = context.getString(R.string.welcome);

            RESTRegister rest = new RESTRegister(username, PreferenceHelper.getInstance().getPushID());
            rest.setListener(new RESTRegister.Listener() {
                @Override
                public void onSuccess() {
                    mLoginView.updateProgress(false);
                    mLoginView.registerSuccess(welcome);
                }

                @Override
                public void onFailure(String errMsg) {
                    mLoginView.updateProgress(false);
                    mLoginView.showRegisterError(errMsg);
                }
            });
            rest.execute();
        }
        else
            mLoginView.showRegisterError(context.getString(R.string.username_error));
    }
}
