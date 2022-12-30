package com.github.q115.goalie_android.uiTest;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.friends.FriendsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
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
public class FriendActivityInstrumentedTest {
    @Rule
    public ActivityTestRule<FriendsActivity> mActivityRule =
            new ActivityTestRule<>(FriendsActivity.class);

    @Test
    public void viewCorrectlyLaidout() throws Exception {
        // menu
        onView(withId(R.id.action_add_friends)).check(matches(isDisplayed()));
        onView(withId(R.id.action_invite_friends)).check(matches(isDisplayed()));

        //empty view
        onView(withId(R.id.empty)).check(matches(isDisplayed()));
    }

    @Test
    public void addContact() throws Exception {
        onView(withId(R.id.action_add_friends)).perform(click());
        onView(withId(R.id.add_username)).check(matches(isDisplayed()));

        onView(withId(R.id.add_username)).perform(clearText(), typeText("tes"));
        onView(withText(mActivityRule.getActivity().getString(R.string.add))).perform(click());
        onView(withId(R.id.add_friend_status)).check(matches(isDisplayed()));

        onView(withText(mActivityRule.getActivity().getString(R.string.cancel))).perform(click());
        // expecting to fail as view doesn't exist
        try {
            onView(withId(R.id.add_username)).check(matches(isDisplayed()));
        } catch (NoMatchingViewException e) {
            return;
        }
        assertTrue(false);
    }
}
