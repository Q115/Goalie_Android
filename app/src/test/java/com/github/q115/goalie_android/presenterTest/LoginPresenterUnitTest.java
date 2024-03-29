package com.github.q115.goalie_android.presenterTest;

import android.content.Context;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.login.LoginFragmentPresenter;
import com.github.q115.goalie_android.ui.login.LoginFragmentView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
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
    private LoginFragmentPresenter mPresenter;

    @Mock
    private LoginFragmentView mView;

    @Mock
    private Context mContext = RuntimeEnvironment.application.getApplicationContext();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mView = mock(LoginFragmentView.class);
        mPresenter = new LoginFragmentPresenter(mView);
        verify(mView).setPresenter(mPresenter);
    }

    @Test
    public void registerBadUsername() throws Exception {
        when(mContext.getString(R.string.username_error))
                .thenReturn("username_error");

        mPresenter.register(mContext, " x ");
        verify(mView).registerComplete(false, "username_error");
    }

    @Test
    public void register() throws Exception {
        when(mContext.getString(R.string.welcome))
                .thenReturn("welcome");

        String username = UUID.randomUUID().toString().substring(20);
        mPresenter.register(mContext, username);
        verify(mView).updateProgress(true);

        verify(mView, timeout(Constants.ASYNC_CONNECTION_EXTENDED_TIMEOUT).times(1)).updateProgress(false);
        verify(mView).registerComplete(true, "welcome");
    }

    @Test
    public void registerSameUsername() throws Exception {
        when(mContext.getString(R.string.username_taken))
                .thenReturn("registered");

        String username = UUID.randomUUID().toString().substring(20);
        mPresenter.register(mContext, username);
        verify(mView).updateProgress(true);
        verify(mView, timeout(Constants.ASYNC_CONNECTION_EXTENDED_TIMEOUT).times(1)).updateProgress(false);

        // username is taken
        mPresenter.register(mContext, username);
        verify(mView, timeout(Constants.ASYNC_CONNECTION_EXTENDED_TIMEOUT).times(2)).updateProgress(false);
        verify(mView).registerComplete(false, "registered");
    }
}