package com.github.q115.goalie_android.presenterTest;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.ui.main.MainActivityPresenter;
import com.github.q115.goalie_android.ui.main.MainActivityView;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static test_util.RESTUtil.getValidFriendUsername;

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
public class MainActivityPresenterUnitTest extends BaseTest {
    private MainActivityPresenter mPresenter;

    @Mock
    private MainActivityView mView;

    @Before
    public void setup() {
        mView = mock(MainActivityView.class);
        mPresenter = spy(new MainActivityPresenter(mView));
    }

    @Test
    public void onStart() throws Exception {
        PreferenceHelper.getInstance().setAccountUsername("");
        mPresenter.start();
        verify(mView).showLogin();

        PreferenceHelper.getInstance().setAccountUsername(getValidFriendUsername());
        UserHelper.getInstance().getOwnerProfile().username = getValidFriendUsername();
        mPresenter.start();
        verify(mView, times(1)).reloadAll();
    }
}