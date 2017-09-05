package com.github.q115.goalie_android.modelsTest;

import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.User;

import org.junit.Test;

import test_util.ModelUtil;

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
    public void defaultConstructor() throws Exception {
        User user = new User();
        assertEquals("", user.username);
        assertEquals("", user.bio);
        assertEquals(100, user.reputation);
        assertEquals(0, user.lastPhotoModifiedTime);
        assertEquals(null, user.profileBitmapImage);
        assertEquals(0, user.activieGoals.size());
        assertEquals(0, user.finishedGoals.size());
    }

    @Test
    public void initConstructor1() throws Exception {
        User user = new User("username");
        User userTest = new User();
        userTest.username = "username";
        assertEquals("username", user.username);
        assertTrue(ModelUtil.isUserEqual(user, userTest));
    }

    @Test
    public void initConstructor2() throws Exception {
        User user = new User("username2", 200);
        User userTest = new User();
        userTest.username = "username2";
        userTest.reputation = 200;
        assertTrue(ModelUtil.isUserEqual(user, userTest));
    }

    @Test
    public void initConstructor3() throws Exception {
        User user = new User("username3", "bio", 300, 999);
        User userTest = new User();
        userTest.username = "username3";
        userTest.reputation = 300;
        userTest.bio = "bio";
        userTest.lastPhotoModifiedTime = 999;
        assertTrue(ModelUtil.isUserEqual(user, userTest));
    }

    @Test
    public void addGoals() throws Exception {
        User user = new User();
        assertEquals(0, user.activieGoals.size());
        user.addActivitGoal(new Goal());
        user.addActivitGoal(new Goal());
        assertEquals(2, user.activieGoals.size());

        assertEquals(0, user.finishedGoals.size());
        user.addCompleteGoal(new Goal());
        user.addCompleteGoal(new Goal());
        assertEquals(2, user.finishedGoals.size());
    }
}