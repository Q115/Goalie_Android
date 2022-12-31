package com.github.q115.goalie_android.uiTest;

import android.app.Instrumentation;
import android.content.pm.ActivityInfo;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.friends.FriendsActivity;
import com.github.q115.goalie_android.ui.main.MainActivity;
import com.github.q115.goalie_android.ui.profile.ProfileActivity;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
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

public class MainActivityInstrumentedTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void init() {
        UserHelper.getInstance().getOwnerProfile().username = "username";
        PreferenceHelper.getInstance().setAccountUsername("username");
    }

    @Test
    public void viewCorrectlyLaidout() throws Exception {
        // menus
        onView(withId(R.id.action_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.action_friends)).check(matches(isDisplayed()));

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
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Thread.sleep(750);
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Thread.sleep(750);
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

        mActivityRule.getActivity().runOnUiThread(() -> mActivityRule.getActivity().onBackPressed());
        Thread.sleep(750); // wait for animation to finish
        onView(withId(R.id.fab_menu1)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fab_menu2)).check(matches(not(isDisplayed())));
    }
}
