package com.github.q115.goalie_android.modelsTest;

import com.github.q115.goalie_android.models.User;

import org.junit.Test;

import test_util.TestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Qi on 8/3/2017.
 */

public class UserTest {
    @Test
    public void initalization_isCorrect() throws Exception {
        User userTest = null;

        User user1 = new User();
        assertEquals("", user1.username);
        assertEquals("", user1.bio);
        assertEquals(100, user1.points);
        assertEquals(0, user1.lastPhotoModifiedTime);
        assertEquals(null, user1.profileBitmapImage);

        User user2 = new User("username");
        userTest = new User();
        userTest.username = "username";
        assertEquals("username", user2.username);
        assertTrue(TestUtil.isUserEqual(user2, userTest));

        User user3 = new User("username2", 200);
        userTest = new User();
        userTest.username = "username2";
        userTest.points = 200;
        assertTrue(TestUtil.isUserEqual(user3, userTest));

        User user4 = new User("username3", "bio", 300, 999);
        userTest = new User();
        userTest.username = "username3";
        userTest.points = 300;
        userTest.bio = "bio";
        userTest.lastPhotoModifiedTime = 999;
        assertTrue(TestUtil.isUserEqual(user4, userTest));
    }
}