package com.github.q115.goalie_android.ui.popular_goal;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;

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

public class PopularGoalFragment extends Fragment implements PopularGoalFragmentView {
    private PopularGoalFragmentPresenter mPopularGoalPresenter;

    public PopularGoalFragment() {
    }

    public static PopularGoalFragment newInstance() {
        return new PopularGoalFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_popular_goal, container, false);

        RecyclerView popularlist = rootView.findViewById(R.id.popular_goal_list);
        popularlist.setLayoutManager(new LinearLayoutManager(getContext()));
        popularlist.setHasFixedSize(true);
        popularlist.setAdapter(new PopularGoalFragmentRecycler(getResources().getStringArray(R.array.popular_goals)));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPopularGoalPresenter.start();
    }

    @Override
    public void setPresenter(PopularGoalFragmentPresenter presenter) {
        mPopularGoalPresenter = presenter;
    }
}
