package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.https.RESTUpdateUserInfo;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
public class RESTUpdateUserInfoTest extends BaseTest {
    private int operations1;
    private int operations2;

    @Test
    public void updatePushID() throws Exception {
        operations1 = 2;

        // update with a registered username
        String username = UUID.randomUUID().toString();
        RESTRegisterTest.registerUser(username, null);

        Thread.sleep(1000);
        final String newPushID = "pushID";
        RESTUpdateUserInfo restUpdateMeta = new RESTUpdateUserInfo(username, "", newPushID);
        restUpdateMeta.setListener(new RESTUpdateUserInfo.Listener() {
            @Override
            public void onSuccess() {
                operations1--;
                assertTrue(newPushID.equals(PreferenceHelper.getInstance().getPushID()));
            }

            @Override
            public void onFailure(String errMsg) {
                operations1--;

            }
        });
        restUpdateMeta.execute();

        // update with invalid username
        final String newPushID2 = "pushID2";
        restUpdateMeta = new RESTUpdateUserInfo(UUID.randomUUID().toString(), "", newPushID2);
        restUpdateMeta.setListener(new RESTUpdateUserInfo.Listener() {
            @Override
            public void onSuccess() {
                operations1--;
                assertFalse(newPushID2.equals(PreferenceHelper.getInstance().getPushID()));
            }

            @Override
            public void onFailure(String errMsg) {
                operations1--;

            }
        });
        restUpdateMeta.execute();

        while (operations1 != 0)
            Thread.sleep(1000);
    }

    @Test
    public void updateUserInfo() throws Exception {
        operations2 = 1;

        String username = UUID.randomUUID().toString();
        RESTRegisterTest.registerUser(username, null);

        Thread.sleep(1000);
        final String bio = "new bio";
        RESTUpdateUserInfo restUpdateMeta = new RESTUpdateUserInfo(username, bio, "pushID");
        restUpdateMeta.setListener(new RESTUpdateUserInfo.Listener() {
            @Override
            public void onSuccess() {
                operations2--;
                assertTrue(UserHelper.getInstance().getOwnerProfile().bio.equals(bio));
            }

            @Override
            public void onFailure(String errMsg) {
                operations2--;
            }
        });
        restUpdateMeta.execute();

        while (operations2 != 0)
            Thread.sleep(1000);
    }
}