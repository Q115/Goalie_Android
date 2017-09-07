package com.github.q115.goalie_android.uiTest;

import android.content.pm.ActivityInfo;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.login.LoginActivity;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import test_util.RESTUtil;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;
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
@RunWith(AndroidJUnit4.class)
public class LoginActivityInstrumentedTest {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void viewCorrectlyLaidout() throws Exception {
        // status is hidden
        onView(withId(R.id.username)).check(matches(isDisplayed()));
        onView(withId(R.id.register_server_response)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
    }

    @Test
    public void invalidClientUsername() throws Exception {
        String usernameERR = mActivityRule.getActivity().getString(R.string.username_error);

        // too short
        onView(withId(R.id.username)).perform(clearText(), typeText("123"));
        onView(withId(R.id.btn_register)).perform(click());
        onView(withId(R.id.register_server_response)).check(matches(isDisplayed()));
        onView(withId(R.id.register_server_response)).check(matches(withText(usernameERR)));

        // invalid character
        onView(withId(R.id.username)).perform(clearText(), typeText("1234 "));
        onView(withId(R.id.btn_register)).perform(click());
        onView(withId(R.id.register_server_response)).check(matches(withText(usernameERR)));

        // invalid character
        onView(withId(R.id.username)).perform(clearText(), typeText("1234:"));
        onView(withId(R.id.btn_register)).perform(click());
        onView(withId(R.id.register_server_response)).check(matches(withText(usernameERR)));

        // invalid character
        onView(withId(R.id.username)).perform(clearText(), typeText("1234/"));
        onView(withId(R.id.btn_register)).perform(click());
        onView(withId(R.id.register_server_response)).check(matches(withText(usernameERR)));

        // invalid character
        onView(withId(R.id.username)).perform(clearText(), typeText("1234\\"));
        onView(withId(R.id.btn_register)).perform(click());
        onView(withId(R.id.register_server_response)).check(matches(withText(usernameERR)));
    }

    @Test
    public void invalidServerUsername() throws Exception {
        String usernameERR = mActivityRule.getActivity().getString(R.string.username_taken);

        onView(withId(R.id.username)).perform(clearText(), typeText(RESTUtil.getValidFriendUsername()));
        onView(withId(R.id.btn_register)).perform(click());
        onView(withId(R.id.register_server_response)).check(matches(withText(usernameERR)));
    }

    @Test
    public void rotate() throws Exception {
        onView(withId(R.id.username)).perform(clearText(), typeText(""));
        onView(withId(R.id.username)).check(matches(withText("")));
        onView(withId(R.id.username)).perform(clearText(), typeText(RESTUtil.getValidFriendUsername()));
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Thread.sleep(1000);
        onView(withId(R.id.username)).check(matches(withText(RESTUtil.getValidFriendUsername())));
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Thread.sleep(1000);
    }

    @AfterClass
    public static void validUsername() throws Exception {
        onView(withId(R.id.username)).perform(clearText(), typeText(UUID.randomUUID().toString()));
        onView(withId(R.id.btn_register)).perform(click());

        // expecting to fail as view doesn't exist since it's dismissed
        try {
            onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
        } catch (NoMatchingViewException e) {
            return;
        }
        assertTrue(false);
    }
}
