package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.https.RESTNewGoal;
import com.github.q115.goalie_android.https.RESTSync;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.Pair;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
public class RESTSyncTest extends BaseREST {
    @Test()
    public void sync() throws Exception {
        final Pair<Integer, RESTSync.Listener> pair = createAListener();

        RESTNewGoal sm = new RESTNewGoal(username, "title", 12000, 120000, 55, "encouragement", username);
        sm.setListener(new RESTNewGoal.Listener() {
            @Override
            public void onSuccess() {
                assertEquals(UserHelper.getInstance().getFeeds().size(), 0);
                RESTSync sm2 = new RESTSync(username, PreferenceHelper.getInstance().getLastSyncedTimeEpoch());
                sm2.setListener(pair.second);
                sm2.execute();
            }

            @Override
            public void onFailure(String errMsg) {
                assertTrue(false);
            }
        });
        sm.execute();

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onSuccess();
        assertNotEquals(PreferenceHelper.getInstance().getLastSyncedTimeEpoch(), 0);
        assertNotEquals(UserHelper.getInstance().getFeeds().size(), 0); // depends on server, can ignore this test if empty

        assertEquals(UserHelper.getInstance().getRequests().size(), 1);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activieGoals.size(), 1);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 0);
    }

    private synchronized Pair<Integer, RESTSync.Listener> createAListener() {
        final Integer index = isOperationCompleteList.size();
        isOperationCompleteList.add(false);

        RESTSync.Listener listener = Mockito.spy(new RESTSync.Listener() {
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

