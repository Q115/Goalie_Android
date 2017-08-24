package com.github.q115.goalie_android.utilsTest;

import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import test_util.TestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static test_util.TestUtil.ReadDatabase;

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
@RunWith(AndroidJUnit4.class)
public class UserHelperInstrumentedTest {

    @BeforeClass
    public static void init() {
        FlowManager.init(InstrumentationRegistry.getTargetContext());
    }

    @AfterClass
    public static void teardown() {
        FlowManager.reset();
    }

    @Test
    public void initalization() throws Exception {
        UserHelper.getInstance().initialize();
        assertNotNull(UserHelper.getInstance().getAllContacts());
        assertNotNull(UserHelper.getInstance().getOwnerProfile());
        assertEquals(0, UserHelper.getInstance().getAllContacts().size());
        assertTrue(TestUtil.isUserEqual(UserHelper.getInstance().getOwnerProfile(), new User()));
    }

    @Test
    public void isUsernameValid() throws Exception {
        assertTrue(UserHelper.isUsernameValid("username"));
        assertFalse(UserHelper.isUsernameValid("ue"));
        assertFalse(UserHelper.isUsernameValid("u:sername"));
        assertFalse(UserHelper.isUsernameValid(""));
        assertFalse(UserHelper.isUsernameValid("u\\e"));
        assertFalse(UserHelper.isUsernameValid("u/e"));
        assertFalse(UserHelper.isUsernameValid("u e"));
        assertFalse(UserHelper.isUsernameValid("u/e"));
        assertFalse(UserHelper.isUsernameValid("admin"));
    }

    @Test
    public void add_deleteUserIsPersisted() throws Exception {
        User testUser = new User("username2", "bio2", 200, 9999);
        addUser(testUser);
        UserHelper.getInstance().getAllContacts().clear();

        ReadDatabase();

        int size = UserHelper.getInstance().getAllContacts().size();
        assertTrue(UserHelper.getInstance().getAllContacts().size() > 0);
        assertTrue(TestUtil.isUserEqual(testUser, UserHelper.getInstance().getAllContacts().get(testUser.username)));

        // delete user
        UserHelper.getInstance().deleteUser(testUser.username);
        assertTrue(UserHelper.getInstance().getAllContacts().size() < size);
        assertNull(UserHelper.getInstance().getAllContacts().get(testUser.username));
    }

    @Test
    public void loadContacts() throws Exception {
        User testUser = new User("username3", "bio3", 300, 99999);
        addUser(testUser);
        assertNull(UserHelper.getInstance().getAllContacts().get(testUser.username).profileBitmapImage);
        UserHelper.getInstance().getAllContacts().clear();

        // save a image for this user
        ImageHelper.getInstance().initialize(InstrumentationRegistry.getTargetContext());
        Bitmap newImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        ImageHelper.getInstance().saveImageToPrivateSorageSync(testUser.username, newImage, ImageHelper.ImageType.PNG);
        assertTrue(ImageHelper.getInstance().isImageOnPrivateStorage(testUser.username, ImageHelper.ImageType.PNG));
        testUser.profileBitmapImage = newImage;

        ReadDatabase();
        UserHelper.getInstance().LoadContacts();

        assertTrue(UserHelper.getInstance().getAllContacts().size() > 0);
        assertTrue(TestUtil.isUserEqual(testUser, UserHelper.getInstance().getAllContacts().get(testUser.username)));
        assertNotNull(UserHelper.getInstance().getAllContacts().get(testUser.username).profileBitmapImage);
    }

    private void addUser(User user) throws Exception {
        UserHelper.getInstance().initialize();
        assertEquals(0, UserHelper.getInstance().getAllContacts().size());
        UserHelper.getInstance().addUser(user);
        assertEquals(1, UserHelper.getInstance().getAllContacts().size());
        assertTrue(TestUtil.isUserEqual(user, UserHelper.getInstance().getAllContacts().get(user.username)));
    }

    @Test
    public void goalTest() throws Exception {
        assertEquals(UserHelper.getInstance().getOwnerProfile().activieGoals.size(), 0);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 0);

        Goal goal = new Goal("goal", Goal.GoalCompleteResult.Pending);
        UserHelper.getInstance().addGoal(goal);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activieGoals.size(), 1);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 0);

        Goal goal2 = new Goal("goal2", Goal.GoalCompleteResult.Ongoing);
        UserHelper.getInstance().addGoal(goal2);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activieGoals.size(), 2);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 0);

        Goal goal3 = new Goal("goal3", Goal.GoalCompleteResult.Success);
        UserHelper.getInstance().addGoal(goal3);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activieGoals.size(), 2);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 1);

        Goal goal4 = new Goal("goal4", Goal.GoalCompleteResult.Failed);
        UserHelper.getInstance().addGoal(goal4);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activieGoals.size(), 2);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 2);

        Goal goal5 = new Goal("goal5", Goal.GoalCompleteResult.Cancelled);
        UserHelper.getInstance().addGoal(goal5);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activieGoals.size(), 2);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 3);

        UserHelper.getInstance().getOwnerProfile().finishedGoals.clear();
        UserHelper.getInstance().getOwnerProfile().activieGoals.clear();
        UserHelper.getInstance().getAllContacts().put("", UserHelper.getInstance().getOwnerProfile());
        ReadDatabase();
        assertEquals(UserHelper.getInstance().getOwnerProfile().activieGoals.size(), 2);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 3);

        deleteGoal(goal2);

        assertEquals(UserHelper.getInstance().getOwnerProfile().activieGoals.size(), 1);
        modifyGoal(goal4);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activieGoals.size(), 2);
    }

    private void deleteGoal(Goal goal) throws Exception {
        int size = UserHelper.getInstance().getOwnerProfile().activieGoals.size()
                + UserHelper.getInstance().getOwnerProfile().finishedGoals.size();

        UserHelper.getInstance().deleteGoal(goal.guid);

        int newSize = UserHelper.getInstance().getOwnerProfile().activieGoals.size()
                + UserHelper.getInstance().getOwnerProfile().finishedGoals.size();
        assertEquals(newSize, size - 1);
    }

    private void modifyGoal(Goal goal) throws Exception {
        Goal newGoal = new Goal();
        newGoal.guid = goal.guid;
        goal.goalCompleteResult = Goal.GoalCompleteResult.Ongoing;

        UserHelper.getInstance().modifyGoal(newGoal);
        UserHelper.getInstance().getOwnerProfile().finishedGoals.clear();
        ReadDatabase();
    }
}