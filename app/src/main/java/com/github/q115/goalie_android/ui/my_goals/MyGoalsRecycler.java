package com.github.q115.goalie_android.ui.my_goals;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.Goal;

import java.util.ArrayList;

public class MyGoalsRecycler extends RecyclerView.Adapter {
    private class MyGoalsHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTxt;
        private TextView mDeadlineTxt;
        private TextView mWagerTxt;
        private TextView mEncouragementTxt;
        private TextView mRefereeTxt;

        private MyGoalsHolder(View itemView) {
            super(itemView);
            mTitleTxt = itemView.findViewById(R.id.goal_title);
            mDeadlineTxt = itemView.findViewById(R.id.goal_deadline);
            mWagerTxt = itemView.findViewById(R.id.goal_wager);
            mEncouragementTxt = itemView.findViewById(R.id.goal_encouragement);
            mRefereeTxt = itemView.findViewById(R.id.goal_referee);
        }
    }

    private FragmentActivity mContext;
    private ArrayList<Goal> mGoalList;

    public MyGoalsRecycler(FragmentActivity context) {
        this.mContext = context;
        mGoalList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return 10;
        // TODO
        //return mGoalList.size();
    }

    //Must override, this inflates our Layout and instantiates and assigns
    //it to the ViewHolder.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mContext.getLayoutInflater().inflate(R.layout.list_item_my_goal, parent, false);
        return new MyGoalsHolder(itemView);
    }

    //Bind our current data to your view holder.  Think of this as the equivalent
    //of GetView for regular Adapters.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyGoalsHolder viewHolder = (MyGoalsHolder) holder;

        //Bind our data from our data source to our View References
        viewHolder.mTitleTxt.setText("hello");
/*
        final int index = position;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
*/
        viewHolder.mRefereeTxt.setText("chris");
        viewHolder.mRefereeTxt.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_profile, 0, 0);
    }
}

