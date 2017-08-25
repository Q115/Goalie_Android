package com.github.q115.goalie_android.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

public abstract class BaseGoalRecyler extends RecyclerView.Adapter {
    public class BaseGoalsHolder extends RecyclerView.ViewHolder {
        public final TextView mTitleTxt;
        public final TextView mEndDateTxt;
        public final TextView mStartDateTxt;
        public final TextView mWagerTxt;
        public final TextView mEncouragementTxt;
        public final TextView mStatusTxt;
        public final TextView mRefereeTxt;

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
    protected static HashMap<String, Drawable> mImages;
    private DateFormat mDF;
    private boolean isRequest;

    protected BaseGoalRecyler(FragmentActivity context, boolean isRequest) {
        this.mContext = context;
        this.isRequest = isRequest;
        this.mGoalList = new ArrayList<>();
        this.mDF = new SimpleDateFormat("MMMM dd, yyyy HH:mm", Locale.getDefault());

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
            Drawable profileDrawableImage = mImages.get(user.username);

            if (profileDrawableImage == null) {
                int size = ImageHelper.dpToPx(mContext.getResources(), Constants.PROFILE_ROW_SIZE);
                Bitmap temp = Bitmap.createScaledBitmap(user.profileBitmapImage, size, size, false);
                profileDrawableImage = ImageHelper.getRoundedCornerDrawable(mContext.getResources(),
                        temp, Constants.CIRCLE_PROFILE);
                mImages.put(user.username, profileDrawableImage);
            }
            viewHolder.mRefereeTxt.setCompoundDrawablesWithIntrinsicBounds(null, profileDrawableImage, null, null);
        }
    }
}