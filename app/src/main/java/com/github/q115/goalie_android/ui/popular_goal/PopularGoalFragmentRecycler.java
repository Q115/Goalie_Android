package com.github.q115.goalie_android.ui.popular_goal;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.new_goal.NewGoalActivity;

import static com.github.q115.goalie_android.Constants.RESULT_GOAL_SET;

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

public class PopularGoalFragmentRecycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static class PopularGoalHolder extends RecyclerView.ViewHolder {
        private final TextView mGoalName;

        public PopularGoalHolder(View itemView) {
            super(itemView);
            mGoalName = itemView.findViewById(R.id.popular_goal_name);
        }
    }

    private final FragmentActivity mContext;
    private final String[] mPopularGoalArray;

    public PopularGoalFragmentRecycler(FragmentActivity context) {
        this.mContext = context;
        this.mPopularGoalArray = context.getResources().getStringArray(R.array.popular_goals);
    }

    @Override
    public int getItemCount() {
        return mPopularGoalArray.length;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mContext.getLayoutInflater().inflate(R.layout.list_item_popular_goals, parent, false);
        itemView.setOnClickListener(view -> {
            Intent intent = NewGoalActivity.newIntent(mContext);
            int position = (int) itemView.getTag();
            if (position < mPopularGoalArray.length) {
                intent.putExtra("title", mPopularGoalArray[position] + ": ");
            }
            mContext.startActivityForResult(intent, RESULT_GOAL_SET);
        });

        return new PopularGoalHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PopularGoalHolder viewHolder = (PopularGoalHolder) holder;
        viewHolder.itemView.setTag(position);

        switch (position) {
            case 0:
                viewHolder.itemView.setBackgroundResource(R.drawable.popular1);
                break;
            case 1:
                viewHolder.itemView.setBackgroundResource(R.drawable.popular2);
                break;
            case 2:
                viewHolder.itemView.setBackgroundResource(R.drawable.popular3);
                break;
            case 3:
                viewHolder.itemView.setBackgroundResource(R.drawable.popular4);
                break;
        }
        viewHolder.mGoalName.setText(mPopularGoalArray[position]);
    }
}
