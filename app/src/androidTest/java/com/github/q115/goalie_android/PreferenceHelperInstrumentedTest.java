package com.github.q115.goalie_android;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.github.q115.goalie_android.utils.PreferenceHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Qi on 8/3/2017.
 */

@RunWith(AndroidJUnit4.class)
public class PreferenceHelperInstrumentedTest {
    @Before
    public void init() throws Exception {
        PreferenceHelper.getInstance().initialize(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void initalization_isCorrect() throws Exception {
        assertNotNull(PreferenceHelper.getInstance().getPushID());
        assertNotNull(PreferenceHelper.getInstance().getAccountUsername());
    }

    @Test
    public void setPushID_isCorrect() throws Exception {
        PreferenceHelper.getInstance().setPushID("new pushID");
        assertEquals("new pushID", PreferenceHelper.getInstance().getPushID());
    }

    @Test
    public void setAccountUsername_isCorrect() throws Exception {
        PreferenceHelper.getInstance().setAccountUsername("new AccountUsername");
        assertEquals("new AccountUsername", PreferenceHelper.getInstance().getAccountUsername());
    }

    @Test
    public void storedOnDisk_isCorrect() throws Exception {
        setAccountUsername_isCorrect();
        setPushID_isCorrect();
        PreferenceHelper.getInstance().initialize(InstrumentationRegistry.getTargetContext());

        assertEquals("new pushID", PreferenceHelper.getInstance().getPushID());
        assertEquals("new AccountUsername", PreferenceHelper.getInstance().getAccountUsername());
    }
}