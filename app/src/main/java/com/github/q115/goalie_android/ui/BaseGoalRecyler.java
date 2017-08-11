package com.github.q115.goalie_android.ui;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
        this.mGoalList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        if (mGoalList != null)
            return mGoalList.size();
        return 0;
    }

    //Bind our current data to your view holder.  Think of this as the equivalent
    //of GetView for regular Adapters.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseGoalRecyler.BaseGoalsHolder viewHolder = (BaseGoalRecyler.BaseGoalsHolder) holder;

        Goal goal = mGoalList.get(position);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());

        //Bind our data from our data source to our View References
        viewHolder.mTitleTxt.setText(goal.title);
        viewHolder.mStartDateTxt.setText(df.format(new Date(goal.startDate)));
        viewHolder.mEndDateTxt.setText(df.format(new Date(goal.endDate)));
        viewHolder.mWagerTxt.setText(String.valueOf(goal.wager));
        viewHolder.mEncouragementTxt.setText(goal.encouragement);

        viewHolder.mRefereeTxt.setText(goal.referee);

        User user = UserHelper.getInstance().getAllContacts().get(goal.referee);
        if (user.profileBitmapImage == null)
            viewHolder.mRefereeTxt.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile_default_small, 0, 0);
        else
            viewHolder.mRefereeTxt.setCompoundDrawables(null, ImageHelper.getRoundedCornerBitmap(mContext.getResources(),
                    user.profileBitmapImage, Constants.CIRCLE_PROFILE), null, null);
    }
}