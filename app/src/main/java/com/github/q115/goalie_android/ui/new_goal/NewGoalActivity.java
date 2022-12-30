package com.github.q115.goalie_android.ui.new_goal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
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

public class NewGoalActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, NewGoalActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_goal);

        String title = getIntent().getStringExtra("title");

        FragmentManager fm = getSupportFragmentManager();
        NewGoalFragment newGoalFragment = (NewGoalFragment) fm.findFragmentByTag("newGoalFragment");
        if (newGoalFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            newGoalFragment = NewGoalFragment.newInstance(title);
            ft.add(android.R.id.content, newGoalFragment, "newGoalFragment");
            ft.commit();
        }

        // Create the presenters
        new NewGoalFragmentPresenter(newGoalFragment);

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
