package com.github.q115.goalie_android.ui.my_goals.popular_goal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;

/**
 * Created by Qi on 8/11/2017.
 */

public class PopularGoalFragment extends Fragment implements PopularGoalView {
    private PopularGoalPresenter mPopularGoalPresenter;

    public PopularGoalFragment() {
    }

    public static PopularGoalFragment newInstance() {
        return new PopularGoalFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_popular_goal, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPopularGoalPresenter.start();
    }

    @Override
    public void setPresenter(PopularGoalPresenter presenter) {
        mPopularGoalPresenter = presenter;
    }
}
