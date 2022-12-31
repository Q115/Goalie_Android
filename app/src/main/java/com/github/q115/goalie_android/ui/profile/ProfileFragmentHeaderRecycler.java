package com.github.q115.goalie_android.ui.profile;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.BaseGoalRecyler;
import com.github.q115.goalie_android.utils.UserHelper;

import java.util.ArrayList;

/*
 * Copyright 2018 Qi Li
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

public class ProfileFragmentHeaderRecycler extends BaseGoalRecyler {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private ProfileBioViewHolder mProfileBioViewHolder;

    public ProfileFragmentHeaderRecycler(FragmentActivity context, ProfileBioViewHolder mProfileBioViewHolder) {
        super(context, false);
        this.mProfileBioViewHolder = mProfileBioViewHolder;
        setupDataSet();
    }

    private void setupDataSet() {
        this.mGoalList = new ArrayList<>(UserHelper.getInstance().getOwnerProfile().finishedGoals.values());
        mGoalList.sort((a1, a2) -> (int) (a2.startDate - a1.startDate));
    }

    public void notifyDataSetHasChanged() {
        setupDataSet();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mGoalList != null)
            return mGoalList.size() + 1;
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = mContext.getLayoutInflater().inflate(R.layout.list_item_my_goal, parent, false);
            return new BaseGoalsHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            return mProfileBioViewHolder;
        }

        throw new RuntimeException("there is no type that matches the type " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BaseGoalsHolder) {
            super.onBindViewHolder(holder, position - 1);
        } else if (holder instanceof ProfileBioViewHolder) {
            // intentionally left blank
        }
    }
}
