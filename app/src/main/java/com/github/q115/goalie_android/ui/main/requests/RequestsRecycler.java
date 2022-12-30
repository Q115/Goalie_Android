package com.github.q115.goalie_android.ui.main.requests;

import android.graphics.drawable.Drawable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.ui.BaseGoalRecyler;
import com.github.q115.goalie_android.utils.GoalHelper;

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

public class RequestsRecycler extends BaseGoalRecyler {
    private RequestsPresenter mRequestsPresenter;

    public RequestsRecycler(FragmentActivity context, RequestsPresenter requestsPresenter) {
        super(context, true);
        mRequestsPresenter = requestsPresenter;
        setupDataSet();
    }

    public void notifyDataSetHasChanged() {
        setupDataSet();
        super.notifyDataSetChanged();
    }

    public void setPresenter(RequestsPresenter requestsPresenter) {
        this.mRequestsPresenter = requestsPresenter;
    }

    private void setupDataSet() {
        this.mGoalList = GoalHelper.getInstance().getRequests();

        if (this.mGoalList == null)
            this.mGoalList = new ArrayList<>();
        Collections.sort(mGoalList, new Comparator<Goal>() {
            @Override
            public int compare(Goal a1, Goal a2) {
                return (int) (a2.startDate - a1.startDate);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mContext.getLayoutInflater().inflate(R.layout.list_item_request, parent, false);
        return new BaseGoalsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        final int pos = position;
        final BaseGoalRecyler.BaseGoalsHolder viewHolder = (BaseGoalRecyler.BaseGoalsHolder) holder;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Goal goal = mGoalList.get(pos);

                if (mRequestsPresenter != null) {
                    Drawable profileImage = mImages.containsKey(goal.createdByUsername) ?
                            mImages.get(goal.createdByUsername)
                            : viewHolder.mRefereeTxt.getCompoundDrawables()[1];

                    mRequestsPresenter.showDialog(viewHolder.mTitleTxt.getText().toString(),
                            viewHolder.mEndDateTxt.getText().toString(),
                            viewHolder.mStartDateTxt.getText().toString(),
                            viewHolder.mWagerTxt.getText().toString(),
                            viewHolder.mEncouragementTxt.getText().toString(),
                            viewHolder.mRefereeTxt.getText().toString(),
                            profileImage,
                            goal.goalCompleteResult, goal.guid);
                }
            }
        });
    }
}