package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.https.RESTGetUserInfo;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static test_util.TestUtil.getValidUsername;

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
public class RESTGetUserInfoTest extends BaseTest {
    private int operation1;

    @Test(timeout = 10000)
    public void getUserInfo() throws Exception {
        operation1 = 1;
        String username = UUID.randomUUID().toString();

        // register friend
        final String friendUsername = getValidUsername();

        // register self
        RESTRegisterTest.registerUser(username, null);
        Thread.sleep(1000);

        // get userinfo on friend
        RESTGetUserInfo sm = new RESTGetUserInfo(friendUsername);
        sm.setListener(new RESTGetUserInfo.Listener() {
            @Override
            public void onSuccess() {
                User user = UserHelper.getInstance().getAllContacts().get(friendUsername);
                assertNotNull(user);
                assertEquals(user.username, friendUsername);

                operation1--;
            }

            @Override
            public void onFailure(String errMsg) {
                operation1--;
            }
        });
        sm.execute();

        while (operation1 != 0)
            Thread.sleep(1000);
    }
}
