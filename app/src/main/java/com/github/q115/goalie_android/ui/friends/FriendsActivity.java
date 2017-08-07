package com.github.q115.goalie_android.ui.friends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.q115.goalie_android.R;

/**
 * Created by Qi on 8/5/2017.
 */

public class FriendsActivity extends AppCompatActivity {
    public static Intent newIntent(Context context, String username) {
        Intent newIntent = new Intent(context, FriendsActivity.class);
        newIntent.putExtra("username", username);
        return newIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        FragmentManager fm = getSupportFragmentManager();
        FriendsListFragment friendsListFragment = (FriendsListFragment)fm.findFragmentByTag("friendsListFragment");
        if (friendsListFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            friendsListFragment = FriendsListFragment.newInstance();
            ft.add(android.R.id.content, friendsListFragment, "friendsListFragment");
            ft.commit();
        }

        // Create the presenter
        new FriendsListPresenter(friendsListFragment);

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
