package com.github.q115.goalie_android.ui.feeds;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.GoalFeed;

import java.util.ArrayList;

/**
 * Created by Qi on 8/4/2017.
 */

public class FeedsRecycler extends RecyclerView.Adapter {
    public class FeedsHolder extends RecyclerView.ViewHolder {
        private TextView mGoalPerson;
        private TextView mGoalResult;
        private TextView mUpvoteCount;
        private Button mGoalFeedAction;

        public FeedsHolder(View itemView) {
            super(itemView);
            mGoalPerson = itemView.findViewById(R.id.goal_person);
            mGoalResult = itemView.findViewById(R.id.goal_result);
            mUpvoteCount = itemView.findViewById(R.id.upvote_count);
            mGoalFeedAction = itemView.findViewById(R.id.goal_feed_action);
        }
    }

    private FragmentActivity mContext;
    private ArrayList<GoalFeed> mGoalFeedList;

    public FeedsRecycler(FragmentActivity context) {
        this.mContext = context;
        mGoalFeedList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mGoalFeedList.size();
    }

    public void notifyDataSetChanged(ArrayList<GoalFeed> goalFeedList) {
        mGoalFeedList = goalFeedList;
        super.notifyDataSetChanged();
    }

    //Must override, this inflates our Layout and instantiates and assigns
    //it to the ViewHolder.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mContext.getLayoutInflater().inflate(R.layout.list_item_feed, parent, false);
        return new FeedsHolder(itemView);
    }

    //Bind our current data to your view holder.  Think of this as the equivalent
    //of GetView for regular Adapters.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FeedsHolder viewHolder = (FeedsHolder) holder;

        GoalFeed feed = mGoalFeedList.get(position);

        //Bind our data from our data source to our View References
        viewHolder.mGoalPerson.setText(feed.createdUsername);
        viewHolder.mGoalPerson.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile_default_small, 0, 0);

        viewHolder.mUpvoteCount.setText(String.valueOf(feed.upvoteCount));
        viewHolder.mUpvoteCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up, 0, 0, 0);

        if (feed.goalCompleteResult == Goal.GoalCompleteResult.Ongoing) {
            viewHolder.mGoalResult.setText(String.format(mContext.getString(R.string.feed_title),
                    mContext.getString(R.string.created), feed.wager));
            viewHolder.mGoalFeedAction.setText(mContext.getString(R.string.goodluck));
        } else {
            viewHolder.mGoalResult.setText(String.format(mContext.getString(R.string.feed_title),
                    mContext.getString(R.string.completed), feed.wager));
            viewHolder.mGoalFeedAction.setText(mContext.getString(R.string.congrats));
        }
    }
}