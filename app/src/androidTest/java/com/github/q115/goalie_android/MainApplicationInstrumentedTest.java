package com.github.q115.goalie_android;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.github.q115.goalie_android.utilsTest.ImageHelperInstrumentedTest;
import com.github.q115.goalie_android.utilsTest.PreferenceHelperInstrumentedTest;
import com.github.q115.goalie_android.utilsTest.UserHelperInstrumentedTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Qi on 8/4/2017.
 */

@RunWith(AndroidJUnit4.class)
public class MainApplicationInstrumentedTest {
    @Test
    public void initalization() throws Exception {
        MainApplication ma = new MainApplication();
        ma.initialize(InstrumentationRegistry.getTargetContext());

        ImageHelperInstrumentedTest imageHelperInstrumentedTest = new ImageHelperInstrumentedTest();
        imageHelperInstrumentedTest.initalization();

        PreferenceHelperInstrumentedTest preferenceHelperInstrumentedTest = new PreferenceHelperInstrumentedTest();
        preferenceHelperInstrumentedTest.initalization();

        UserHelperInstrumentedTest userHelperInstrumentedTest = new UserHelperInstrumentedTest();
        userHelperInstrumentedTest.initalization();
    }
}
