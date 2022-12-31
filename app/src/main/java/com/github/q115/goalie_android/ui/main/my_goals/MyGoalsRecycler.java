package com.github.q115.goalie_android.ui.main.my_goals;

import android.graphics.drawable.Drawable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.ui.BaseGoalRecyler;
import com.github.q115.goalie_android.utils.UserHelper;

import java.util.ArrayList;

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
public class MyGoalsRecycler extends BaseGoalRecyler {
    private MyGoalsPresenter mMyGoalsPresenter;

    public MyGoalsRecycler(FragmentActivity context, MyGoalsPresenter myGoalsPresenter) {
        super(context, false);
        mMyGoalsPresenter = myGoalsPresenter;
        setupDataSet();
    }

    public void notifyDataSetHasChanged() {
        setupDataSet();
        super.notifyDataSetChanged();
    }

    public void setPresenter(MyGoalsPresenter myGoalsPresenter) {
        this.mMyGoalsPresenter = myGoalsPresenter;
    }

    private void setupDataSet() {
        this.mGoalList = new ArrayList<>(UserHelper.getInstance().getOwnerProfile().activeGoals.values());
        mGoalList.sort((a1, a2) -> (int) (a2.startDate - a1.startDate));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mContext.getLayoutInflater().inflate(R.layout.list_item_my_goal, parent, false);
        return new BaseGoalsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        final BaseGoalRecyler.BaseGoalsHolder viewHolder = (BaseGoalRecyler.BaseGoalsHolder) holder;
        final int pos = position;
        viewHolder.itemView.setOnClickListener(view -> {
            if (mMyGoalsPresenter.isFABOpen()) {
                mMyGoalsPresenter.closeFABMenu();
            } else {
                Goal goal = mGoalList.get(pos);
                if (mMyGoalsPresenter != null) {
                    Drawable profileImage = mImages.containsKey(goal.referee) ?
                            mImages.get(goal.referee)
                            : viewHolder.mRefereeTxt.getCompoundDrawables()[1];

                    mMyGoalsPresenter.showDialog(viewHolder.mTitleTxt.getText().toString(),
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

