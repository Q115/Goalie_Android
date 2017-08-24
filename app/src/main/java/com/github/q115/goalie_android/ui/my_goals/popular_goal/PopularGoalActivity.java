package com.github.q115.goalie_android.ui.my_goals.popular_goal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.q115.goalie_android.R;

import static com.github.q115.goalie_android.Constants.RESULT_GOAL_SET;

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

public class PopularGoalActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, PopularGoalActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_goal);

        FragmentManager fm = getSupportFragmentManager();
        PopularGoalFragment popularGoalFragment = (PopularGoalFragment) fm.findFragmentByTag("popularGoalFragment");
        if (popularGoalFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            popularGoalFragment = PopularGoalFragment.newInstance();
            ft.add(android.R.id.content, popularGoalFragment, "popularGoalFragment");
            ft.commit();
        }

        // Create the presenters
        new PopularGoalPresenter(popularGoalFragment);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_GOAL_SET && resultCode == RESULT_GOAL_SET) {
            setResult(RESULT_GOAL_SET);
            finish();
        }
    }
}