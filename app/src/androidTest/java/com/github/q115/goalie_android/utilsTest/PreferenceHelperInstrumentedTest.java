package com.github.q115.goalie_android.utilsTest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.utils.PreferenceHelper;

import org.junit.AfterClass;
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

    @AfterClass
    public static void teardown() throws Exception {
        SharedPreferences sp = InstrumentationRegistry.getTargetContext().getSharedPreferences(Constants.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }

    @Test
    public void initalization() throws Exception {
        assertNotNull(PreferenceHelper.getInstance().getPushID());
        assertNotNull(PreferenceHelper.getInstance().getAccountUsername());
    }

    @Test
    public void setPushID() throws Exception {
        PreferenceHelper.getInstance().setPushID("new pushID");
        assertEquals("new pushID", PreferenceHelper.getInstance().getPushID());
    }

    @Test
    public void setAccountUsername() throws Exception {
        PreferenceHelper.getInstance().setAccountUsername("new AccountUsername");
        assertEquals("new AccountUsername", PreferenceHelper.getInstance().getAccountUsername());
    }

    @Test
    public void storedOnDisk() throws Exception {
        setAccountUsername();
        setPushID();
        PreferenceHelper.getInstance().initialize(InstrumentationRegistry.getTargetContext());

        assertEquals("new pushID", PreferenceHelper.getInstance().getPushID());
        assertEquals("new AccountUsername", PreferenceHelper.getInstance().getAccountUsername());
    }
}