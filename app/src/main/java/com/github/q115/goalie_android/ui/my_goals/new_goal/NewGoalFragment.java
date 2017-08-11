package com.github.q115.goalie_android.ui.my_goals.new_goal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;

/**
 * Created by Qi on 8/11/2017.
 */

public class NewGoalFragment extends Fragment implements NewGoalView {
    private NewGoalPresenter mNewGoalPresenter;

    public NewGoalFragment() {
    }

    public static NewGoalFragment newInstance() {
        return new NewGoalFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_goal, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mNewGoalPresenter.start();
    }

    @Override
    public void setPresenter(NewGoalPresenter presenter) {
        mNewGoalPresenter = presenter;
    }
}
