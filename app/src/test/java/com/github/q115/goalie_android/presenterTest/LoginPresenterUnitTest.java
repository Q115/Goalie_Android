package com.github.q115.goalie_android.presenterTest;

import android.test.mock.MockContext;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.login.LoginPresenter;
import com.github.q115.goalie_android.ui.login.LoginView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

@RunWith(RobolectricTestRunner.class)
public class LoginPresenterUnitTest extends BaseTest {
    private LoginPresenter mPresenter;

    @Mock
    private LoginView mView;

    @Mock
    private MockContext mContext;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mView = mock(LoginView.class);
        mPresenter = new LoginPresenter(mView);
        verify(mView).setPresenter(mPresenter);
    }

    @Test
    public void register() throws Exception {
        when(mContext.getString(R.string.username_error))
                .thenReturn("username_error");
        when(mContext.getString(R.string.welcome))
                .thenReturn("welcome");
        when(mContext.getString(R.string.username_taken))
                .thenReturn("registered");

        String username = UUID.randomUUID().toString().substring(20);
        mPresenter.register(mContext, username);
        verify(mView).updateProgress(true);
        Thread.sleep(2000);
        verify(mView).updateProgress(false);
        verify(mView).registerComplete(true, "welcome");

        // username is taken
        mPresenter.register(mContext, username);
        Thread.sleep(2000);
        verify(mView).registerComplete(false, "registered");

        // bad username
        mPresenter.register(mContext, " x ");
        Thread.sleep(2000);
        verify(mView).registerComplete(false, "username_error");
    }
}