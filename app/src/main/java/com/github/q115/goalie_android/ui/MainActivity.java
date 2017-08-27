package com.github.q115.goalie_android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.services.MessagingService;
import com.github.q115.goalie_android.ui.feeds.FeedsFragment;
import com.github.q115.goalie_android.ui.feeds.FeedsPresenter;
import com.github.q115.goalie_android.ui.friends.FriendsActivity;
import com.github.q115.goalie_android.ui.login.LoginActivity;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsFragment;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsPresenter;
import com.github.q115.goalie_android.ui.profile.ProfileActivity;
import com.github.q115.goalie_android.ui.requests.RequestsFragment;
import com.github.q115.goalie_android.ui.requests.RequestsPresenter;
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
public class MainActivity extends AppCompatActivity implements MainActivityView, MessagingService.MessagingServiceListener {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private MainActivityPresenter mPresenter;
    private MyGoalsPresenter mMyGoalsPresenter;
    private RequestsPresenter mRequestsPresenter;
    private FeedsPresenter mFeedsPresenter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the presenter
        mPresenter = new MainActivityPresenter(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        MainActivityPagerAdapter mSectionsPagerAdapter = new MainActivityPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // intentionally left blank
            }

            @Override
            public void onPageSelected(int position) {
                if (mMyGoalsPresenter != null)
                    mMyGoalsPresenter.closeFABMenu();
                AppBarLayout appBarLayout = findViewById(R.id.appbar);
                appBarLayout.setExpanded(true, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // intentionally left blank
            }
        });

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if (getIntent() != null)
            onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.hasExtra("tab")) {
            final int tab = intent.getIntExtra("tab", 0);
            mViewPager.post(new Runnable() {
                @Override
                public void run() {
                    mViewPager.setCurrentItem(tab);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessagingService.setMessagingServiceListener("Main", this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        MessagingService.setMessagingServiceListener("Main", null);
        super.onDestroy();
    }

    @Override
    public void setPresenter(MainActivityPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
    public void onBackPressed() {
        if (mMyGoalsPresenter == null || !mMyGoalsPresenter.isFABOpen()) {
            super.onBackPressed();
        } else {
            mMyGoalsPresenter.closeFABMenu();
        }
    }

    @Override
    public void showLogin() {
        startActivity(LoginActivity.newIntent(this));
    }

    @Override
    public void reloadAll() {
        if (mMyGoalsPresenter != null)
            mMyGoalsPresenter.reload();

        if (mRequestsPresenter != null)
            mRequestsPresenter.reload();

        if (mFeedsPresenter != null)
            mFeedsPresenter.reload();
    }

    @Override
    public void onNotification() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reloadAll();
            }
        });
    }

    // Create 3 fragments
    private class MainActivityPagerAdapter extends FragmentPagerAdapter {
        private final String[] mTitles = {getString(R.string.tab_feeds),
                getString(R.string.tab_my_goal),
                getString(R.string.tab_requests)};

        public MainActivityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    mFeedsPresenter = new FeedsPresenter((FeedsFragment) createdFragment);
                    break;
                case 1:
                    mMyGoalsPresenter = new MyGoalsPresenter((MyGoalsFragment) createdFragment);
                    break;
                case 2:
                    mRequestsPresenter = new RequestsPresenter((RequestsFragment) createdFragment);
                    break;
            }
            return createdFragment;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FeedsFragment.newInstance();
                case 1:
                    return MyGoalsFragment.newInstance();
                case 2:
                    return RequestsFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position < mTitles.length)
                return mTitles[position];
            return null;
        }
    }
}
