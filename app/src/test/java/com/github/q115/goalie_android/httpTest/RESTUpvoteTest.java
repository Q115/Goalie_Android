package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.https.RESTNewGoal;
import com.github.q115.goalie_android.https.RESTUpvote;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.Pair;

import static junit.framework.Assert.assertTrue;
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
public class RESTUpvoteTest extends BaseREST {

    @Test
    public void doUpvote() throws Exception {
        final String friendUsername = getValidFriendUsername();
        final Pair<Integer, RESTUpvote.Listener> pair = createAListener();

        // set up a new goal
        RESTNewGoal sm = new RESTNewGoal(username, "title", 12000, 120000, 55, "encouragement", friendUsername, true);
        sm.setListener(new RESTNewGoal.Listener() {
            @Override
            public void onSuccess() {
                // upvote on that goal
                Object[] array = UserHelper.getInstance().getOwnerProfile().activeGoals.values().toArray();
                RESTUpvote sm = new RESTUpvote(username, ((Goal) array[0]).guid);
                sm.setListener(pair.second);
                sm.execute();
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
    }

    private synchronized Pair<Integer, RESTUpvote.Listener> createAListener() {
        final Integer index = isOperationCompleteList.size();
        isOperationCompleteList.add(false);

        RESTUpvote.Listener listener = Mockito.spy(new RESTUpvote.Listener() {
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