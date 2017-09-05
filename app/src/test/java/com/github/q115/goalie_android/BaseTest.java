package com.github.q115.goalie_android;

import android.content.Context;

import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.ResponseDelivery;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.github.q115.goalie_android.https.VolleyRequestQueue;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.File;
import java.util.concurrent.Executors;

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

@RunWith(RobolectricTestRunner.class)
public abstract class BaseTest {
    @Before
    public void init() throws Exception {
        assertTrue(BuildConfig.DEBUG); // unit test should only be run on debug at this time

        FlowManager.init(new FlowConfig.Builder(RuntimeEnvironment.application).build());
        VolleyRequestQueue.getInstance().initialize(RuntimeEnvironment.application);
        Whitebox.setInternalState(VolleyRequestQueue.getInstance(), "mRequestQueue",
                newVolleyRequestQueueForTest(RuntimeEnvironment.application));

        ImageHelper.getInstance().initialize(RuntimeEnvironment.application);
        UserHelper.getInstance().initialize();
        PreferenceHelper.getInstance().initialize(RuntimeEnvironment.application);
        UserHelper.getInstance().LoadContacts();
    }

    private RequestQueue newVolleyRequestQueueForTest(final Context context) {
        File cacheDir = new File(context.getCacheDir(), "cache/volley");
        Network network = new BasicNetwork(new HurlStack());
        ResponseDelivery responseDelivery = new ExecutorDelivery(Executors.newSingleThreadExecutor());
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network, 4, responseDelivery);
        queue.start();

        return queue;
    }
}
