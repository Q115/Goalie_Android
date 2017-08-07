package com.github.q115.goalie_android.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.github.q115.goalie_android.R;

/**
 * Created by Qi on 8/6/2017.
 */

public class LoginActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentManager fm = getSupportFragmentManager();
        LoginFragment loginFragment = (LoginFragment) fm.findFragmentByTag("loginFragment");
        if (loginFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            loginFragment = LoginFragment.newInstance();
            ft.add(android.R.id.content, loginFragment, "loginFragment");
            ft.commit();
        }

        // Create the presenter
        new LoginPresenter(loginFragment);
    }
}
