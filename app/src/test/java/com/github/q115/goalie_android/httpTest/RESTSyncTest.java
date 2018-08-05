package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.https.RESTNewGoal;
import com.github.q115.goalie_android.https.RESTSync;
import com.github.q115.goalie_android.utils.GoalHelper;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.Pair;

import static com.github.q115.goalie_android.Constants.FAILED;
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
public class RESTSyncTest extends BaseRESTTest {
    @Test()
    public void sync() throws Exception {
        final Pair<Integer, RESTSync.Listener> pair = createAListener();

        RESTNewGoal sm = new RESTNewGoal(username, "title", 12000, 120000, 55, "encouragement", username, true);
        sm.setListener(new RESTNewGoal.Listener() {
            @Override
            public void onSuccess(String guid) {
                assertTrue(guid != null);
                assertEquals(GoalHelper.getInstance().getFeeds().size(), 0);
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

        assertEquals(GoalHelper.getInstance().getRequests().size(), 1);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activeGoals.size(), 1);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 0);
    }

    @Test()
    public void onResponseEmpty() throws Exception {
        final Pair<Integer, RESTSync.Listener> pair = createAListener();

        RESTSync sm2 = new RESTSync(username, PreferenceHelper.getInstance().getLastSyncedTimeEpoch());
        sm2.setListener(pair.second);
        sm2.onResponse("");

        verify(pair.second).onFailure(FAILED);
    }

    @Test()
    public void onResponseBasic() throws Exception {
        final Pair<Integer, RESTSync.Listener> pair = createAListener();

        RESTSync sm2 = new RESTSync(username, PreferenceHelper.getInstance().getLastSyncedTimeEpoch());
        sm2.setListener(pair.second);
        sm2.onResponse("{\"feed\":[], \"my\":[],\"referee\":[],\"info\":{\"reputation\":999},\"time\":123}");

        verify(pair.second).onSuccess();
        assertEquals(UserHelper.getInstance().getOwnerProfile().reputation, 999);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activeGoals.size(), 0);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 0);
        assertEquals(GoalHelper.getInstance().getRequests().size(), 0);
        assertEquals(GoalHelper.getInstance().getFeeds().size(), 0);
        assertEquals(PreferenceHelper.getInstance().getLastSyncedTimeEpoch(), 123);
    }

    @Test()
    public void onResponseInvalid() throws Exception {
        final Pair<Integer, RESTSync.Listener> pair = createAListener();

        RESTSync sm2 = new RESTSync(username, PreferenceHelper.getInstance().getLastSyncedTimeEpoch());
        sm2.setListener(pair.second);
        sm2.onResponse("{\"feed\":[], \"my\":[],\"referee\":[],\"info\":{\"reputation\":999},\"time\":\"invalid\"}");

        verify(pair.second).onFailure(FAILED);
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

