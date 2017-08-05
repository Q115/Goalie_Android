package com.github.q115.goalie_android.ui.friends;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Qi on 8/5/2017.
 */

public class FriendActivity extends AppCompatActivity {
    public static Intent newIntent(Context context, String username) {
        Intent newIntent = new Intent(context, FriendActivity.class);
        newIntent.putExtra("username", username);
        return newIntent;
    }
}
