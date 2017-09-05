package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.https.RESTGetUserInfo;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.Pair;

import static com.github.q115.goalie_android.Constants.FAILED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static test_util.RESTUtil.getTestUsername;
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
public class RESTGetUserInfoTest extends BaseREST {

    @Test
    public void getUserInfo() throws Exception {
        String friendUsername = getValidFriendUsername();
        Pair<Integer, RESTGetUserInfo.Listener> pair = createAListener();

        RESTGetUserInfo sm = new RESTGetUserInfo(friendUsername);
        sm.setListener(pair.second);
        sm.execute();

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onSuccess();
        User user = UserHelper.getInstance().getAllContacts().get(friendUsername);
        assertNotNull(user);
        assertEquals(user.username, friendUsername);
    }

    @Test
    public void onResponseEmpty() throws Exception {
        String friendUsername = getValidFriendUsername();
        Pair<Integer, RESTGetUserInfo.Listener> pair = createAListener();

        RESTGetUserInfo sm = new RESTGetUserInfo(friendUsername);
        sm.setListener(pair.second);
        sm.onResponse("");

        verify(pair.second).onFailure(FAILED);
    }

    @Test
    public void onResponseMissingFields() throws Exception {
        String friendUsername = getValidFriendUsername();
        Pair<Integer, RESTGetUserInfo.Listener> pair = createAListener();

        RESTGetUserInfo sm = new RESTGetUserInfo(friendUsername);
        sm.setListener(pair.second);
        sm.onResponse("{\"lastPhotoModifiedTime\":123, \"bio\":\"bio\",\"reputation\":99,\"missing\":[]}");

        verify(pair.second).onFailure(FAILED);
    }

    @Test
    public void onResponseInvalidFields() throws Exception {
        String friendUsername = getValidFriendUsername();
        Pair<Integer, RESTGetUserInfo.Listener> pair = createAListener();

        RESTGetUserInfo sm = new RESTGetUserInfo(friendUsername);
        sm.setListener(pair.second);
        sm.onResponse("{\"lastPhotoModifiedTime\":123, \"bio\":\"bio\",\"reputation\":99,\"goals\":invalid}");

        verify(pair.second).onFailure(FAILED);
    }

    @Test
    public void onResponseValidValuesSaved() throws Exception {
        String friendUsername = getTestUsername();
        Pair<Integer, RESTGetUserInfo.Listener> pair = createAListener();

        RESTGetUserInfo sm = new RESTGetUserInfo(friendUsername);
        sm.setListener(pair.second);
        sm.onResponse("{\"lastPhotoModifiedTime\":123, \"bio\":\"bio\",\"reputation\":99,\"goals\":[]}");

        verify(pair.second).onSuccess();

        User user = UserHelper.getInstance().getAllContacts().get(friendUsername);
        assertNotNull(user);
        assertEquals(user.finishedGoals.size(), 0);
        assertEquals(user.bio, "bio");
        assertEquals(user.reputation, 99);
        assertEquals(user.lastPhotoModifiedTime, 123);
    }

    private synchronized Pair<Integer, RESTGetUserInfo.Listener> createAListener() {
        final Integer index = isOperationCompleteList.size();
        isOperationCompleteList.add(false);

        RESTGetUserInfo.Listener listener = Mockito.spy(new RESTGetUserInfo.Listener() {
            @Override
            public void onSuccess() {
                isOperationCompleteList.set(index, true);
            }

            @Override
            public void onFailure(String errMsg) {
                isOperationCompleteList.set(index, true);
            }
        });

        return new Pair<>(index, listener);
    }
}
