package com.github.q115.goalie_android.ui.login;

import android.content.Context;
import androidx.annotation.NonNull;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.https.RESTRegister;
import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;
/*
 * Copyright 2017 Qi Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class LoginFragmentPresenter implements BasePresenter {
    private final LoginFragmentView mLoginView;

    public LoginFragmentPresenter(@NonNull LoginFragmentView loginView) {
        mLoginView = loginView;
        mLoginView.setPresenter(this);
    }

    public void start() {
    }

    public void register(Context context, String username) {
        if (UserHelper.isUsernameValid(username)) {
            mLoginView.updateProgress(true);

            final String welcome = context.getString(R.string.welcome);
            final String usernameTaken = context.getString(R.string.username_taken);

            RESTRegister rest = new RESTRegister(username, PreferenceHelper.getInstance().getPushID());
            rest.setListener(new RESTRegister.Listener() {
                @Override
                public void onSuccess() {
                    mLoginView.updateProgress(false);
                    mLoginView.registerComplete(true, welcome);
                }

                @Override
                public void onFailure(String errMsg) {
                    if (errMsg.equals("Already Registered"))
                        errMsg = usernameTaken;

                    mLoginView.updateProgress(false);
                    mLoginView.registerComplete(false, errMsg);
                }
            });
            rest.execute();
        } else
            mLoginView.registerComplete(false, context.getString(R.string.username_error));
    }
}
