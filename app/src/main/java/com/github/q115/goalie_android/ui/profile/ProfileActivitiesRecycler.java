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

/**
 * Created by Qi on 8/4/2017.
 */

public class ProfileActivitiesRecycler extends BaseGoalRecyler {
    public ProfileActivitiesRecycler(FragmentActivity context) {
        super(context);
        setupDataSet();
    }

    private void setupDataSet() {
        this.mGoalList = UserHelper.getInstance().getOwnerProfile().finishedGoals;
        if (this.mGoalList == null)
            this.mGoalList = new ArrayList<>();
        Collections.sort(mGoalList, new Comparator<Goal>() {
            @Override
            public int compare(Goal a1, Goal a2) {
                return (int) (a2.endDate - a1.endDate);
            }
        });
    }

    //Must override, this inflates our Layout and instantiates and assigns
    //it to the ViewHolder.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mContext.getLayoutInflater().inflate(R.layout.list_item_my_goal, parent, false);
        return new BaseGoalsHolder(itemView);
    }

    public void notifyDataSetChanged(ArrayList<Goal> goalList) {
        mGoalList = goalList;
        this.notifyDataSetChanged();
    }
}