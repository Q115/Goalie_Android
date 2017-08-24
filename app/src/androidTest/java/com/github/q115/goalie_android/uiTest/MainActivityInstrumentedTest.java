package com.github.q115.goalie_android.uiTest;

import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.MainActivity;
import com.github.q115.goalie_android.ui.friends.FriendsActivity;
import com.github.q115.goalie_android.ui.profile.ProfileActivity;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;

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
public class MainActivityInstrumentedTest {
    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void init() {
        UserHelper.getInstance().getOwnerProfile().username = "username";
        PreferenceHelper.getInstance().setAccountUsername("username");
    }

    @Test
    public void viewCorrectlyLaidout() throws Exception {
        // menus
        onView(ViewMatchers.withId(R.id.action_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.action_profile)).check(matches(isDisplayed()));

        onView(withId(R.id.container)).check(matches(isDisplayed()));
    }

    @Test
    public void menuClickProfileGoesToProfile() throws Exception {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation()
                .addMonitor(ProfileActivity.class.getName(), null, false);

        onView(withId(R.id.action_profile)).perform(click());
        ProfileActivity targetActivity = (ProfileActivity) activityMonitor.waitForActivity();
        assertNotNull("Target Activity is not launched", targetActivity);
    }

    @Test
    public void menuClickFriendsGoesToFriends() throws Exception {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation()
                .addMonitor(FriendsActivity.class.getName(), null, false);

        onView(withId(R.id.action_friends)).perform(click());
        FriendsActivity targetActivity = (FriendsActivity) activityMonitor.waitForActivity();
        assertNotNull("Target Activity is not launched", targetActivity);
    }

    @Test
    public void rotate() throws Exception {
        mMainActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Thread.sleep(500);
        mMainActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Thread.sleep(500);
    }

    // @Test
    public void myGoalsTab() throws Exception {
        // feed fragment is shown
        onView(withId(R.id.goal_list)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_new_goal)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_menu1)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fab_menu2)).check(matches(not(isDisplayed())));

        FABCollapsesOnBackButton();
    }

    private void FABCollapsesOnBackButton() throws Exception {
        onView(withId(R.id.fab_new_goal)).perform(click());
        onView(withId(R.id.fab_menu1)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_menu2)).check(matches(isDisplayed()));

        mMainActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMainActivityTestRule.getActivity().onBackPressed();
            }
        });
        Thread.sleep(500); // wait for animation to finish
        onView(withId(R.id.fab_menu1)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fab_menu2)).check(matches(not(isDisplayed())));
    }
}
