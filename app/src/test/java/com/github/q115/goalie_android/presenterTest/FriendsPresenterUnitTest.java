package com.github.q115.goalie_android.presenterTest;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.ui.friends.FriendsActivityPresenter;
import com.github.q115.goalie_android.ui.friends.FriendsActivityView;
import com.github.q115.goalie_android.ui.friends.FriendsListPresenter;
import com.github.q115.goalie_android.ui.friends.FriendsListView;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
public class FriendsPresenterUnitTest extends BaseTest {
    private FriendsActivityPresenter mActivityPresenter;

    @Mock
    private FriendsActivityView mView;

    @Mock
    private Context mContext = RuntimeEnvironment.application.getApplicationContext();

    private FriendsListPresenter mListPresenter;

    @Mock
    private FriendsListView mListView;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mView = mock(FriendsActivityView.class);
        mActivityPresenter = spy(new FriendsActivityPresenter(mView));

        mListView = mock(FriendsListView.class);
        mListPresenter = spy(new FriendsListPresenter(mListView));
    }

    @Test
    public void sendSMSInvite() {
        when(mContext.getContentResolver())
                .thenReturn(new ContentResolver(mContext) {
                    @Nullable
                    @Override
                    public String[] getStreamTypes(@NonNull Uri url, @NonNull String mimeTypeFilter) {
                        return super.getStreamTypes(url, mimeTypeFilter);
                    }
                });

        mActivityPresenter.sendSMSInvite(mContext, Uri.EMPTY);
        verify(mView, never()).sendSMSInvite("");

        mActivityPresenter.sendSMSInvite(mContext, null);
        verify(mView, never()).sendSMSInvite("");
    }

    @Test
    public void delete() {
        User user = new User("username");
        UserHelper.getInstance().addUser(user);
        assertNotNull(UserHelper.getInstance().getAllContacts().get(user.username));

        mListPresenter.delete(user.username);
        assertNull(UserHelper.getInstance().getAllContacts().get(user.username));
        verify(mListView).reload();
    }

    @Test
    public void onRefresh() throws Exception {
        mListPresenter.refresh(getValidFriendUsername());
        verify(mListView, timeout(Constants.ASYNC_CONNECTION_EXTENDED_TIMEOUT).times(1)).reload();
    }

    @Test
    public void onAddContactDialogNoSuchUser() throws Exception {
        assertNull(UserHelper.getInstance().getAllContacts().get(getValidFriendUsername()));

        mListPresenter.onAddContactDialogComplete(getValidFriendUsername());

        verify(mListView, timeout(Constants.ASYNC_CONNECTION_EXTENDED_TIMEOUT).times(1)).reload();
        assertNotNull(UserHelper.getInstance().getAllContacts().get(getValidFriendUsername()));
        assertNotNull(UserHelper.getInstance().getAllContacts().get(getValidFriendUsername()).profileBitmapImage);
    }

    @Test
    public void onAddContactDialog() throws Exception {
        UserHelper.getInstance().addUser(new User(getValidFriendUsername()));
        assertNull(UserHelper.getInstance().getAllContacts().get(getValidFriendUsername()).profileBitmapImage);

        mListPresenter.onAddContactDialogComplete(getValidFriendUsername());

        verify(mListView, timeout(Constants.ASYNC_CONNECTION_EXTENDED_TIMEOUT).times(1)).reload();
        assertNotNull(UserHelper.getInstance().getAllContacts().get(getValidFriendUsername()).profileBitmapImage);
    }
}
