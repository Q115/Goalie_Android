package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.https.RESTRegister;
import com.github.q115.goalie_android.https.RESTUpdateUserInfo;
import com.github.q115.goalie_android.utils.PreferenceHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
public class RESTRegisterTest extends BaseTest {
    private int operations1;
    private int operations2;

    @Test(timeout = 30000)
    public void executeWith_WithoutPushID() throws Exception {
        executeWithoutPushID();
        executeWithPushID();
    }

    private void executeWithoutPushID() throws Exception {
        final RESTUpdateUserInfo restUpdateMeta = mock(RESTUpdateUserInfo.class);
        operations1 = 2;

        // with no pushID saved
        assertFalse(RESTRegister.isRegistering());
        RESTRegisterTest.registerUser(UUID.randomUUID().toString(), new RESTRegister.Listener() {
            @Override
            public void onSuccess() {
                operations1--;
                verify(restUpdateMeta, never()).execute();
                assertFalse(RESTRegister.isRegistering());
            }

            @Override
            public void onFailure(String errMsg) {
                operations1--;
                assertFalse(RESTRegister.isRegistering());
            }
        });
        assertTrue(RESTRegister.isRegistering());

        // with pushID saved
        PreferenceHelper.getInstance().setPushID("newPushID");
        RESTRegisterTest.registerUser(UUID.randomUUID().toString(), new RESTRegister.Listener() {
            @Override
            public void onSuccess() {
                operations1--;
                verify(restUpdateMeta).execute();
                assertFalse(RESTRegister.isRegistering());
            }

            @Override
            public void onFailure(String errMsg) {
                operations1--;
                assertFalse(RESTRegister.isRegistering());
            }
        });

        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        while (operations1 != 0)
            Thread.sleep(1000);
    }

    private void executeWithPushID() throws Exception {
        final RESTUpdateUserInfo restUpdateMeta = mock(RESTUpdateUserInfo.class);
        operations2 = 1;
        assertFalse(RESTRegister.isRegistering());
        RESTRegister rest = new RESTRegister(UUID.randomUUID().toString(), "pushID");
        rest.setListener(new RESTRegister.Listener() {
            @Override
            public void onSuccess() {
                operations2--;
                verify(restUpdateMeta, never()).execute();
                assertFalse(RESTRegister.isRegistering());
            }

            @Override
            public void onFailure(String errMsg) {
                operations2--;
                assertFalse(RESTRegister.isRegistering());
            }
        });
        rest.execute();
        assertTrue(RESTRegister.isRegistering());

        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        while (operations2 != 0)
            Thread.sleep(1000);
    }

    public static void registerUser(String username, RESTRegister.Listener listener) {
        RESTRegister rest = new RESTRegister(username, "");
        rest.setListener(listener);
        rest.execute();
    }
}