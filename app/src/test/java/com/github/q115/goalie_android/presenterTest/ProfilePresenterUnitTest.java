package com.github.q115.goalie_android.presenterTest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.ui.profile.ProfileFragmentPresenter;
import com.github.q115.goalie_android.ui.profile.ProfileFragmentView;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
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
public class ProfilePresenterUnitTest extends BaseTest {
    private ProfileFragmentPresenter mPresenter;

    @Mock
    private ProfileFragmentView mView;

    @Before
    public void setup() {
        mView = mock(ProfileFragmentView.class);
        mPresenter = spy(new ProfileFragmentPresenter(getValidFriendUsername(), mView));
    }

    @Test
    public void onStart() throws Exception {
        UserHelper.getInstance().getOwnerProfile().username = "fake";
        mPresenter.start();
        verify(mView).toggleOwnerSpecificFeatures(false);

        User user = new User(getValidFriendUsername(), "bio", 999, 0);
        UserHelper.getInstance().setOwnerProfile(user);
        UserHelper.getInstance().getOwnerProfile().username = getValidFriendUsername();
        mPresenter.start();
        verify(mView).toggleOwnerSpecificFeatures(true);
        verify(mView).setupView(user.username, user.bio, user.reputation);
    }

    @Test
    public void newProfileImageSelected() throws Exception {
        Bitmap image = null;
        mPresenter.newProfileImageSelected(image);
        verify(mView, never()).updateProgress(true);

        image = BitmapFactory.decodeFile("../test.png");
        mPresenter.newProfileImageSelected(image);
        verify(mView).updateProgress(true);

        verify(mView, timeout(Constants.ASYNC_CONNECTION_EXTENDED_TIMEOUT).times(1)).updateProgress(false);
        verify(mView).uploadComplete(true, image, null);
    }
}
