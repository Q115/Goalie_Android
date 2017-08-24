package com.github.q115.goalie_android.modelsTest;

import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.User;

import org.junit.Test;

import test_util.TestUtil;

import static org.junit.Assert.assertEquals;
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

public class UserTest {
    @Test
    public void initalization() throws Exception {
        User userTest = null;

        User user1 = new User();
        assertEquals("", user1.username);
        assertEquals("", user1.bio);
        assertEquals(100, user1.reputation);
        assertEquals(0, user1.lastPhotoModifiedTime);
        assertEquals(null, user1.profileBitmapImage);
        assertEquals(0, user1.activieGoals.size());
        assertEquals(0, user1.finishedGoals.size());

        User user2 = new User("username");
        userTest = new User();
        userTest.username = "username";
        assertEquals("username", user2.username);
        assertTrue(TestUtil.isUserEqual(user2, userTest));

        User user3 = new User("username2", 200);
        userTest = new User();
        userTest.username = "username2";
        userTest.reputation = 200;
        assertTrue(TestUtil.isUserEqual(user3, userTest));

        User user4 = new User("username3", "bio", 300, 999);
        userTest = new User();
        userTest.username = "username3";
        userTest.reputation = 300;
        userTest.bio = "bio";
        userTest.lastPhotoModifiedTime = 999;
        assertTrue(TestUtil.isUserEqual(user4, userTest));
    }

    @Test
    public void addGoals() throws Exception {
        User user1 = new User();
        assertEquals(0, user1.activieGoals.size());
        user1.addActivitGoal(new Goal());
        user1.addActivitGoal(new Goal());
        assertEquals(2, user1.activieGoals.size());

        assertEquals(0, user1.finishedGoals.size());
        user1.addCompleteGoal(new Goal());
        user1.addCompleteGoal(new Goal());
        assertEquals(2, user1.finishedGoals.size());
    }
}