package com.github.q115.goalie_android.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.github.q115.goalie_android.MainBaseActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.services.MessagingServiceUtil;
import com.github.q115.goalie_android.ui.DelayedProgressDialog;
import com.github.q115.goalie_android.ui.friends.FriendsActivity;
import com.github.q115.goalie_android.ui.login.LoginActivity;
import com.github.q115.goalie_android.ui.main.feeds.FeedsPresenter;
import com.github.q115.goalie_android.ui.main.my_goals.MyGoalsPresenter;
import com.github.q115.goalie_android.ui.main.requests.RequestsPresenter;
import com.github.q115.goalie_android.ui.profile.ProfileActivity;
import com.github.q115.goalie_android.utils.UserHelper;

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
public class MainActivity extends MainBaseActivity implements MainActivityView,
        MessagingServiceUtil.MessagingServiceListener, ViewPager.OnPageChangeListener {
    private MainActivityPresenter mPresenter;
    private ViewPager mViewPager;
    private MainActivityPagerAdapter mViewPagerAdapter;
    private DelayedProgressDialog progressDialog;

    public static Intent newIntent(Context context, int tab) {
        Intent newIntent = new Intent(context, MainActivity.class);
        newIntent.putExtra("tab", tab);
        return newIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the presenter
        mPresenter = new MainActivityPresenter(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the ViewPager with the sections adapter.
        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.container);

        mViewPagerAdapter = new MainActivityPagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(mViewPager);

        progressDialog = new DelayedProgressDialog();

        if (getIntent() != null)
            onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.hasExtra("tab")) {
            final int tab = intent.getIntExtra("tab", 0);
            mViewPager.post(() -> mViewPager.setCurrentItem(tab));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessagingServiceUtil.setMessagingServiceListener("Main", this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.syncIfNeeded();
    }

    @Override
    protected void onDestroy() {
        MessagingServiceUtil.setMessagingServiceListener("Main", null);
        super.onDestroy();
    }

    @Override
    public void setPresenter(MainActivityPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_friends:
                startActivity(FriendsActivity.newIntent(this, UserHelper.getInstance().getOwnerProfile().username));
                return true;
            case R.id.action_profile:
                startActivity(ProfileActivity.newIntent(this, UserHelper.getInstance().getOwnerProfile().username));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // intentionally left blank
    }

    @Override
    public void onPageSelected(int position) {
        MyGoalsPresenter myGoalsPresenter = mViewPagerAdapter.getMyGoalsPresenter();
        if (myGoalsPresenter != null)
            myGoalsPresenter.closeFABMenu();
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true, true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // intentionally left blank
    }

    @Override
    public void onBackPressed() {
        MyGoalsPresenter myGoalsPresenter = mViewPagerAdapter.getMyGoalsPresenter();
        if (myGoalsPresenter == null || !myGoalsPresenter.isFABOpen()) {
            super.onBackPressed();
        } else {
            myGoalsPresenter.closeFABMenu();
        }
    }

    @Override
    public void showLogin() {
        startActivity(LoginActivity.newIntent(this));
    }

    @Override
    public void reloadAll() {
        MyGoalsPresenter myGoalsPresenter = mViewPagerAdapter.getMyGoalsPresenter();
        RequestsPresenter requestsPresenter = mViewPagerAdapter.getRequestsPresenter();
        FeedsPresenter feedsPresenter = mViewPagerAdapter.getFeedsPresenter();

        if (myGoalsPresenter != null)
            myGoalsPresenter.reload();

        if (requestsPresenter != null)
            requestsPresenter.reload();

        if (feedsPresenter != null)
            feedsPresenter.reload();
    }

    @Override
    public void onNotification() {
        runOnUiThread(this::reloadAll);
    }

    @Override
    public void updateProgress(boolean shouldShow) {
        if (shouldShow) {
            progressDialog.show(getSupportFragmentManager(), "DelayedProgressDialog");
        } else {
            progressDialog.cancel();
        }
    }
}
