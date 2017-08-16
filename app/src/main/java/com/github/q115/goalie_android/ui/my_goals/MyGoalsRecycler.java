package com.github.q115.goalie_android.ui.my_goals;

import android.graphics.drawable.BitmapDrawable;
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

public class MyGoalsRecycler extends BaseGoalRecyler {
    private MyGoalsPresenter mMyGoalsPresenter;

    public MyGoalsRecycler(FragmentActivity context, MyGoalsPresenter myGoalsPresenter) {
        super(context);
        mMyGoalsPresenter = myGoalsPresenter;
        setupDataSet();
    }

    public void notifyDataSetHasChanged() {
        setupDataSet();
        super.notifyDataSetChanged();
    }

    private void setupDataSet() {
        this.mGoalList = UserHelper.getInstance().getOwnerProfile().activieGoals;

        if (this.mGoalList == null)
            this.mGoalList = new ArrayList<>();
        Collections.sort(mGoalList, new Comparator<Goal>() {
            @Override
            public int compare(Goal a1, Goal a2) {
                return (int) (a1.endDate - a2.endDate);
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

    //Bind our current data to your view holder.  Think of this as the equivalent
    //of GetView for regular Adapters.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        final BaseGoalRecyler.BaseGoalsHolder viewHolder = (BaseGoalRecyler.BaseGoalsHolder) holder;
        final int pos = position;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMyGoalsPresenter.isFABOpen()) {
                    mMyGoalsPresenter.closeFABMenu();
                } else {
                    Goal goal = mGoalList.get(pos);
                    mMyGoalsPresenter.showDialog(viewHolder.mTitleTxt.getText().toString(), viewHolder.mEndDateTxt.getText().toString(),
                            viewHolder.mStartDateTxt.getText().toString(), viewHolder.mWagerTxt.getText().toString(), viewHolder.mEncouragementTxt.getText().toString(),
                            viewHolder.mRefereeTxt.getText().toString(), ((BitmapDrawable) viewHolder.mRefereeTxt.getCompoundDrawables()[1]).getBitmap(), goal.goalCompleteResult, goal.guid);
                }
            }
        });
    }
}

