package com.github.q115.goalie_android;

import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;

import com.github.q115.goalie_android.ui.MainActivity;
import com.github.q115.goalie_android.ui.friends.FriendActivity;
import com.github.q115.goalie_android.ui.profile.ProfileActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Qi on 8/5/2017.
 */

public class MainActivityInstrumentedTest {
    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void viewCorrectlyLaidout() throws Exception {
        // menus
        onView(withId(R.id.action_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.action_profile)).check(matches(isDisplayed()));

        // my goals fragment is shown
        onView(withId(R.id.container)).check(matches(isDisplayed()));
        onView(withId(R.id.goal_list)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_new_goal)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_menu1)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fab_menu2)).check(matches(not(isDisplayed())));
    }

    @Test
    public void FABCollapsesOnBackButton() throws Exception {
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
                .addMonitor(FriendActivity.class.getName(), null, false);

        onView(withId(R.id.action_friends)).perform(click());
        FriendActivity targetActivity = (FriendActivity) activityMonitor.waitForActivity();
        assertNotNull("Target Activity is not launched", targetActivity);
    }
}
