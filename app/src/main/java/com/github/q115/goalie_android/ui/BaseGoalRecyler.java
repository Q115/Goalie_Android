package com.github.q115.goalie_android.ui;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsRecycler;

import java.util.ArrayList;

/**
 * Created by Qi on 8/4/2017.
 */

public abstract class BaseGoalRecyler extends RecyclerView.Adapter {
    public class BaseGoalsHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTxt;
        private TextView mEndDateTxt;
        private TextView mStartDateTxt;
        private TextView mWagerTxt;
        private TextView mEncouragementTxt;
        private TextView mRefereeTxt;

        public BaseGoalsHolder(View itemView) {
            super(itemView);
            mTitleTxt = itemView.findViewById(R.id.goal_title);
            mStartDateTxt = itemView.findViewById(R.id.goal_start);
            mEndDateTxt = itemView.findViewById(R.id.goal_end);
            mWagerTxt = itemView.findViewById(R.id.goal_wager);
            mEncouragementTxt = itemView.findViewById(R.id.goal_encouragement);
            mRefereeTxt = itemView.findViewById(R.id.goal_referee);
        }
    }

    protected FragmentActivity mContext;
    protected ArrayList<Goal> mGoalList;

    public BaseGoalRecyler(FragmentActivity context) {
        this.mContext = context;
        mGoalList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return 10;
        // TODO
        //return mGoalList.size();
    }

    //Bind our current data to your view holder.  Think of this as the equivalent
    //of GetView for regular Adapters.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseGoalRecyler.BaseGoalsHolder viewHolder = (BaseGoalRecyler.BaseGoalsHolder) holder;

        //Bind our data from our data source to our View References
        viewHolder.mTitleTxt.setText("lose 5 lbs");
        viewHolder.mStartDateTxt.setText("august 31, 2017");
        viewHolder.mEndDateTxt.setText("august 31, 2017");
        viewHolder.mWagerTxt.setText("100");
        viewHolder.mEncouragementTxt.setText("you can do it");
/*
        final int index = position;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
*/
        viewHolder.mRefereeTxt.setText("chris");
        viewHolder.mRefereeTxt.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile_default_small, 0, 0);
    }
}