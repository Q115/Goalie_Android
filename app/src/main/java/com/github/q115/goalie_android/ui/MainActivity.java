package com.github.q115.goalie_android.ui;

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

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.GoalFeed;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityView {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private MainActivityPagerAdapter mSectionsPagerAdapter;
    private MainActivityPresenter mPresenter;
    private MyGoalsPresenter mMyGoalsPresenter;
    private RequestsPresenter mRequestsPresenter;
    private FeedsPresenter mFeedsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the presenter
        mPresenter = new MainActivityPresenter(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new MainActivityPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mMyGoalsPresenter != null)
                    mMyGoalsPresenter.closeFABMenu();
                AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
                appBarLayout.setExpanded(true, true);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.start();
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
        if (!mMyGoalsPresenter.isFABOpen()) {
            super.onBackPressed();
        } else {
            mMyGoalsPresenter.closeFABMenu();
            reloadAll();
        }
    }

    @Override
    public void showLogin() {
        startActivity(LoginActivity.newIntent(this));
    }

    public void reloadAll() {
        if (mMyGoalsPresenter != null)
            mMyGoalsPresenter.reload();

        if (mRequestsPresenter != null)
            mRequestsPresenter.reload();

        if (mFeedsPresenter != null)
            mFeedsPresenter.reload();
    }

    public void attachMyGoalsPresenter(MyGoalsPresenter myGoalsPresenter) {
        mMyGoalsPresenter = myGoalsPresenter;
    }

    public void attachRequestsPresenter(RequestsPresenter requestsPresenter) {
        mRequestsPresenter = requestsPresenter;
    }

    public void attachFeedsPresenter(FeedsPresenter feedsPresenter) {
        mFeedsPresenter = feedsPresenter;
    }

    // Create 3 fragments
    private class MainActivityPagerAdapter extends FragmentPagerAdapter {
        public MainActivityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    FeedsFragment fm3 = FeedsFragment.newInstance();
                    mFeedsPresenter = new FeedsPresenter(fm3);
                    return fm3;
                case 1:
                    MyGoalsFragment fm1 = MyGoalsFragment.newInstance();
                    mMyGoalsPresenter = new MyGoalsPresenter(fm1);
                    return fm1;
                case 2:
                    RequestsFragment fm2 = RequestsFragment.newInstance();
                    mRequestsPresenter = new RequestsPresenter(fm2);
                    return fm2;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_feeds);
                case 1:
                    return getString(R.string.tab_my_goal);
                case 2:
                    return getString(R.string.tab_requests);
            }
            return null;
        }
    }
}
