package com.github.q115.goalie_android.ui.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.main.feeds.FeedsFragment;
import com.github.q115.goalie_android.ui.main.feeds.FeedsPresenter;
import com.github.q115.goalie_android.ui.main.my_goals.MyGoalsFragment;
import com.github.q115.goalie_android.ui.main.my_goals.MyGoalsPresenter;
import com.github.q115.goalie_android.ui.main.requests.RequestsFragment;
import com.github.q115.goalie_android.ui.main.requests.RequestsPresenter;

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

public class MainActivityPagerAdapter extends FragmentPagerAdapter {
    private MyGoalsPresenter mMyGoalsPresenter;
    private RequestsPresenter mRequestsPresenter;
    private FeedsPresenter mFeedsPresenter;

    private final String[] mTitles;

    public MainActivityPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mTitles = new String[]{context.getString(R.string.tab_feeds),
                context.getString(R.string.tab_my_goal),
                context.getString(R.string.tab_requests)};
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

    public MyGoalsPresenter getMyGoalsPresenter() {
        return mMyGoalsPresenter;
    }

    public RequestsPresenter getRequestsPresenter() {
        return mRequestsPresenter;
    }

    public FeedsPresenter getFeedsPresenter() {
        return mFeedsPresenter;
    }
}
