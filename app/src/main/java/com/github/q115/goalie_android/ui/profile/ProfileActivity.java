package com.github.q115.goalie_android.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Qi on 8/5/2017.
 */

public class ProfileActivity extends AppCompatActivity {
    public static Intent newIntent(Context context, String username) {
        Intent newIntent = new Intent(context, ProfileActivity.class);
        newIntent.putExtra("username", username);
        return newIntent;
    }
}
