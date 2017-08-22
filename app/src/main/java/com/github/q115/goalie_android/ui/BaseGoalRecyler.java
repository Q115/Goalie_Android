package com.github.q115.goalie_android.ui;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
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
import java.util.HashMap;
import java.util.Locale;

import static android.R.color.tab_indicator_text;

/**
 * Created by Qi on 8/4/2017.
 */

public abstract class BaseGoalRecyler extends RecyclerView.Adapter {
    public class BaseGoalsHolder extends RecyclerView.ViewHolder {
        public TextView mTitleTxt;
        public TextView mEndDateTxt;
        public TextView mStartDateTxt;
        public TextView mWagerTxt;
        public TextView mEncouragementTxt;
        public TextView mStatusTxt;
        public TextView mRefereeTxt;

        public BaseGoalsHolder(View itemView) {
            super(itemView);
            mTitleTxt = itemView.findViewById(R.id.goal_title);
            mStartDateTxt = itemView.findViewById(R.id.goal_start);
            mEndDateTxt = itemView.findViewById(R.id.goal_end);
            mWagerTxt = itemView.findViewById(R.id.goal_wager);
            mEncouragementTxt = itemView.findViewById(R.id.goal_encouragement);
            mRefereeTxt = itemView.findViewById(R.id.goal_referee);
            mStatusTxt = itemView.findViewById(R.id.goal_status);
        }
    }

    protected FragmentActivity mContext;
    protected ArrayList<Goal> mGoalList;
    protected static HashMap<String, Bitmap> mImages;
    private DateFormat mDF;
    private boolean isRequest;

    public BaseGoalRecyler(FragmentActivity context, boolean isRequest) {
        this.mContext = context;
        this.isRequest = isRequest;
        this.mGoalList = new ArrayList<>();
        mDF = new SimpleDateFormat("MMMM dd, yyyy HH:mm", Locale.getDefault());

        if (mImages == null)
            mImages = new HashMap<>();
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

        //Bind our data from our data source to our View References
        viewHolder.mTitleTxt.setText(goal.title);
        viewHolder.mStartDateTxt.setText(String.format(mContext.getString(R.string.start_), mDF.format(new Date(goal.startDate))));
        viewHolder.mEndDateTxt.setText(String.format(mContext.getString(R.string.end_), mDF.format(new Date(goal.endDate))));
        viewHolder.mWagerTxt.setText(String.format(mContext.getString(R.string.reputation_), goal.wager));
        viewHolder.mEncouragementTxt.setText(goal.encouragement);

        switch (goal.goalCompleteResult) {
            case Pending:
                viewHolder.mStatusTxt.setText(R.string.status_pending);
                viewHolder.mStatusTxt.setTextColor(ContextCompat.getColor(mContext, tab_indicator_text));
                break;
            case Ongoing:
                viewHolder.mStatusTxt.setText(R.string.status_ongoing);
                viewHolder.mStatusTxt.setTextColor(ContextCompat.getColor(mContext, tab_indicator_text));
                break;
            case Failed:
                viewHolder.mStatusTxt.setText(R.string.status_failed);
                viewHolder.mStatusTxt.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                break;
            case Success:
                viewHolder.mStatusTxt.setText(R.string.status_successful);
                viewHolder.mStatusTxt.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                break;
            default:
                viewHolder.mStatusTxt.setText("");
                break;
        }

        User user;
        if (isRequest) {
            viewHolder.mRefereeTxt.setText(goal.createdByUsername);
            user = UserHelper.getInstance().getAllContacts().get(goal.createdByUsername);
        } else {
            viewHolder.mRefereeTxt.setText(goal.referee);
            user = UserHelper.getInstance().getAllContacts().get(goal.referee);
        }

        if (user == null)
            return;

        if (user.profileBitmapImage == null)
            viewHolder.mRefereeTxt.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile_default_small, 0, 0);
        else {
            Bitmap profileBitmapImage = mImages.get(user.username);

            if (profileBitmapImage == null) {
                int size = ImageHelper.dpToPx(mContext.getResources(), 75);
                profileBitmapImage = Bitmap.createScaledBitmap(user.profileBitmapImage, size, size, false);
                mImages.put(user.username, profileBitmapImage);
            }
            viewHolder.mRefereeTxt.setCompoundDrawablesWithIntrinsicBounds(null, ImageHelper.getRoundedCornerBitmap(mContext.getResources(),
                    profileBitmapImage, Constants.CIRCLE_PROFILE), null, null);
        }
    }
}