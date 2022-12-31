package com.github.q115.goalie_android.ui;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.github.q115.goalie_android.MainBaseActivity;
import com.github.q115.goalie_android.R;

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

public class ManageSpaceActivity extends MainBaseActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // do nothing to prevent Preference file from being deleted
        new Handler().post(() -> {
            Toast.makeText(ManageSpaceActivity.this, getString(R.string.cleaned), Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
