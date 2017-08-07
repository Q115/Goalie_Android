package com.github.q115.goalie_android.uiTest;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.login.LoginActivity;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by Qi on 8/6/2017.
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityInstrumentedTest {
    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    @BeforeClass
    public static void init() throws Exception {

    }

    @Test
    public void viewCorrectlyLaidout() throws Exception {
        // status is hidden
        onView(withId(R.id.username)).check(matches(isDisplayed()));
        onView(withId(R.id.register_server_response)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
    }

    @Test
    public void invalidClientUsername() throws Exception {
        // too short
        onView(withId(R.id.username)).perform(clearText(), typeText("123"));
        onView(withId(R.id.btn_register)).perform(click());
        onView(withId(R.id.register_server_response)).check(matches(isDisplayed()));
        onView(withId(R.id.register_server_response)).check(matches(withText(mLoginActivityRule.getActivity().getString(R.string.username_error))));

        // invalid character
        onView(withId(R.id.username)).perform(clearText(), typeText("1234 "));
        onView(withId(R.id.btn_register)).perform(click());
        onView(withId(R.id.register_server_response)).check(matches(withText(mLoginActivityRule.getActivity().getString(R.string.username_error))));

        // invalid character
        onView(withId(R.id.username)).perform(clearText(), typeText("1234:"));
        onView(withId(R.id.btn_register)).perform(click());
        onView(withId(R.id.register_server_response)).check(matches(withText(mLoginActivityRule.getActivity().getString(R.string.username_error))));

        // invalid character
        onView(withId(R.id.username)).perform(clearText(), typeText("1234/"));
        onView(withId(R.id.btn_register)).perform(click());
        onView(withId(R.id.register_server_response)).check(matches(withText(mLoginActivityRule.getActivity().getString(R.string.username_error))));

        // invalid character
        onView(withId(R.id.username)).perform(clearText(), typeText("1234\\"));
        onView(withId(R.id.btn_register)).perform(click());
        onView(withId(R.id.register_server_response)).check(matches(withText(mLoginActivityRule.getActivity().getString(R.string.username_error))));
    }

    @Test
    public void validUsername() throws Exception {
        // pings server
        onView(withId(R.id.username)).perform(clearText(), typeText(UUID.randomUUID().toString()));
        onView(withId(R.id.btn_register)).perform(click());
    }

    @Test
    public void invalidServerUsername() throws Exception {
        String username = UUID.randomUUID().toString();
        onView(withId(R.id.username)).perform(clearText(), typeText(username));
        onView(withId(R.id.btn_register)).perform(click());

        onView(withId(R.id.username)).perform(clearText(), typeText(username));
        onView(withId(R.id.btn_register)).perform(click());
        onView(withId(R.id.register_server_response)).check(matches(withText("taken")));
    }
}
