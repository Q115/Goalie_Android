package com.github.q115.goalie_android.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

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
public class ProfileActivity extends AppCompatActivity {
    private String mUsername;

    public static Intent newIntent(Context context, String username) {
        Intent newIntent = new Intent(context, ProfileActivity.class);
        newIntent.putExtra("username", username);
        return newIntent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("username", mUsername);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mUsername = getIntent().getStringExtra("username");
        if (savedInstanceState != null) {
            mUsername = savedInstanceState.getString("username");
        }

        FragmentManager fm = getSupportFragmentManager();
        ProfileFragment profileFragment = (ProfileFragment) fm.findFragmentByTag("profileFragment");
        if (profileFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            profileFragment = ProfileFragment.newInstance();
            ft.add(android.R.id.content, profileFragment, "profileFragment");
            ft.commit();
        }

        // Create the presenter
        new ProfilePresenter(mUsername, profileFragment);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
