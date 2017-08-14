package com.github.q115.goalie_android.ui.requests;

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

/**
 * Created by Qi on 8/4/2017.
 */

public class RequestsRecycler extends BaseGoalRecyler {
    private RequestsPresenter mRequestsPresenter;

    public RequestsRecycler(FragmentActivity context, RequestsPresenter requestsPresenter) {
        super(context);
        setupDataSet();
        mRequestsPresenter = requestsPresenter;
    }

    public void notifyDataSetHasChanged() {
        setupDataSet();
        super.notifyDataSetChanged();
    }

    private void setupDataSet() {
        this.mGoalList = UserHelper.getInstance().getRequests();

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
        View itemView = mContext.getLayoutInflater().inflate(R.layout.list_item_request, parent, false);
        return new BaseGoalsHolder(itemView);
    }

    //Bind our current data to your view holder.  Think of this as the equivalent
    //of GetView for regular Adapters.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        final BaseGoalRecyler.BaseGoalsHolder viewHolder = (BaseGoalRecyler.BaseGoalsHolder) holder;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRequestsPresenter.showDialog(viewHolder.mTitleTxt.getText().toString(), viewHolder.mEndDateTxt.getText().toString(),
                        viewHolder.mStartDateTxt.getText().toString(), viewHolder.mWagerTxt.getText().toString(), viewHolder.mEncouragementTxt.getText().toString(),
                        viewHolder.mRefereeTxt.getText().toString(), ((BitmapDrawable) viewHolder.mRefereeTxt.getCompoundDrawables()[1]).getBitmap());
            }
        });
    }
}