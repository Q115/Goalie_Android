package com.github.q115.goalie_android.ui.my_goals.new_goal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;

/**
 * Created by Qi on 8/11/2017.
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
        new NewGoalPresenter(newGoalFragment);

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
