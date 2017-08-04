package com.github.q115.goalie_android;

import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

import test_util.TestUtil;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Qi on 8/4/2017.
 */

@RunWith(AndroidJUnit4.class)
public class MainApplicationInstrumentedTest {
    @Test
    public void initalization_isCorrect() throws Exception {
        MainApplication ma = new MainApplication();
        ma.initialize(InstrumentationRegistry.getTargetContext());

        ImageHelperInstrumentedTest imageHelperInstrumentedTest = new ImageHelperInstrumentedTest();
        imageHelperInstrumentedTest.initalization_isCorrect();

        PreferenceHelperInstrumentedTest preferenceHelperInstrumentedTest = new PreferenceHelperInstrumentedTest();
        preferenceHelperInstrumentedTest.initalization_isCorrect();

        UserHelperInstrumentedTest userHelperInstrumentedTest = new UserHelperInstrumentedTest();
        userHelperInstrumentedTest.initalization_isCorrect();
    }
}
