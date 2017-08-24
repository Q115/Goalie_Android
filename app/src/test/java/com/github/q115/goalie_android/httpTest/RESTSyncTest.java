package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.https.RESTNewGoal;
import com.github.q115.goalie_android.https.RESTSync;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
public class RESTSyncTest extends BaseTest {
    private int operation1;

    @Test(timeout = 10000)
    public void sync() throws Exception {
        operation1 = 1;
        final String username = UUID.randomUUID().toString();

        // register self
        RESTRegisterTest.registerUser(username, null);
        Thread.sleep(1500);

        RESTNewGoal sm = new RESTNewGoal(username, "title", 12000, 120000, 55, "encouragement", username);
        sm.setListener(null);
        sm.execute();
        Thread.sleep(1500);

        // get userinfo on RESTNewGoal
        assertEquals(UserHelper.getInstance().getFeeds().size(), 0);
        RESTSync sm2 = new RESTSync(username, PreferenceHelper.getInstance().getLastSyncedTimeEpoch());
        sm2.setListener(new RESTSync.Listener() {
            @Override
            public void onSuccess() {
                assertNotEquals(PreferenceHelper.getInstance().getLastSyncedTimeEpoch(), 0);
                assertNotEquals(UserHelper.getInstance().getFeeds().size(), 0); // depends on server, can ignore this test if empty

                assertEquals(UserHelper.getInstance().getRequests().size(), 1);
                assertEquals(UserHelper.getInstance().getOwnerProfile().activieGoals.size(), 1);
                assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 0);
                operation1--;
            }

            @Override
            public void onFailure(String errMsg) {
                operation1--;
            }
        });
        sm2.execute();

        while (operation1 != 0)
            Thread.sleep(1000);
    }
}

