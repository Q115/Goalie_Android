package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.https.RESTNewGoal;
import com.github.q115.goalie_android.https.RESTRemind;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;

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
public class RESTRemindTest extends BaseTest {
    private int operation1;

    @Test(timeout = 10000)
    public void sendReminder() throws Exception {
        operation1 = 1;
        final String username = UUID.randomUUID().toString();

        // register friend
        final String friendUsername = getValidUsername();

        // register self
        RESTRegisterTest.registerUser(username, null);
        Thread.sleep(1000);

        // set up a new goal
        RESTNewGoal sm = new RESTNewGoal(username, "title", 12000, 120000, 55, "encouragement", friendUsername);
        sm.setListener(new RESTNewGoal.Listener() {
            @Override
            public void onSuccess(String guid) {
                // remind on that goal
                RESTRemind sm = new RESTRemind(username, friendUsername, UserHelper.getInstance().getOwnerProfile().activieGoals.get(0).guid, true);
                sm.setListener(new RESTRemind.Listener() {
                    @Override
                    public void onSuccess() {
                        operation1--;
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        operation1--;
                    }
                });
                sm.execute();
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