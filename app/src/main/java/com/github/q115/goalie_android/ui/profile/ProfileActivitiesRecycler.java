package com.github.q115.goalie_android.ui.profile;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.ui.BaseGoalRecyler;
import com.github.q115.goalie_android.utils.UserHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

public class ProfileActivitiesRecycler extends BaseGoalRecyler {
    public ProfileActivitiesRecycler(FragmentActivity context) {
        super(context, false);
        setupDataSet();
    }

    private void setupDataSet() {
        this.mGoalList =  new ArrayList<>(UserHelper.getInstance().getOwnerProfile().finishedGoals.values());
        Collections.sort(mGoalList, new Comparator<Goal>() {
            @Override
            public int compare(Goal a1, Goal a2) {
                return (int) (a2.activityDate - a1.activityDate);
            }
        });
    }

    public void notifyDataSetHasChanged() {
        setupDataSet();
        this.notifyDataSetChanged();
    }

    //Must override, this inflates our Layout and instantiates and assigns
    //it to the ViewHolder.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mContext.getLayoutInflater().inflate(R.layout.list_item_my_goal, parent, false);
        return new BaseGoalsHolder(itemView);
    }
}