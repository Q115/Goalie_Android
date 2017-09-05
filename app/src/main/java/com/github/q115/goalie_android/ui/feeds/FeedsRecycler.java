package com.github.q115.goalie_android.ui.feeds;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.https.RESTGetPhoto;
import com.github.q115.goalie_android.https.RESTUpvote;
import com.github.q115.goalie_android.https.VolleyRequestQueue;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.GoalFeed;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.GoalHelper;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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

public class FeedsRecycler extends RecyclerView.Adapter {
    private class FeedsHolder extends RecyclerView.ViewHolder {
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
    private static HashSet<String> mHasVoted; // doesn't persist over restart.
    private static HashMap<String, Drawable> mImages;
    private ProgressDialog mProgressDialog;

    public FeedsRecycler(FragmentActivity context) {
        this.mContext = context;
        this.mGoalFeedList = GoalHelper.getInstance().getFeeds();

        this.mProgressDialog = new ProgressDialog(this.mContext);
        this.mProgressDialog.setMessage(this.mContext.getString(R.string.connecting));
        this.mProgressDialog.setCancelable(false);

        if (mHasVoted == null)
            mHasVoted = new HashSet<>();
        if (mImages == null)
            mImages = new HashMap<>();
    }

    @Override
    public int getItemCount() {
        return mGoalFeedList.size();
    }

    public void notifyDataSetHasChanged() {
        mGoalFeedList = GoalHelper.getInstance().getFeeds();
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
        final FeedsHolder viewHolder = (FeedsHolder) holder;
        final GoalFeed feed = mGoalFeedList.get(position);

        // set profile image
        viewHolder.mGoalPerson.setText(feed.createdUsername);
        setupImage(feed, viewHolder.mGoalPerson);

        // setup vote count
        viewHolder.mUpvoteCount.setText(String.valueOf(feed.upvoteCount));
        viewHolder.mUpvoteCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up, 0, 0, 0);

        // set up button text
        if (feed.goalCompleteResult == Goal.GoalCompleteResult.Ongoing || feed.goalCompleteResult == Goal.GoalCompleteResult.Pending) {
            viewHolder.mGoalResult.setText(String.format(mContext.getString(R.string.feed_title),
                    mContext.getString(R.string.started), feed.wager));
            viewHolder.mGoalFeedAction.setText(mContext.getString(R.string.goodluck));
        } else {
            viewHolder.mGoalResult.setText(String.format(mContext.getString(R.string.feed_title),
                    mContext.getString(R.string.completed), feed.wager));
            viewHolder.mGoalFeedAction.setText(mContext.getString(R.string.congrats));
        }

        // set up button action if necessary
        final int pos = position;
        viewHolder.mGoalFeedAction.setEnabled(!feed.hasVoted && !mHasVoted.contains(feed.guid));
        if (viewHolder.mGoalFeedAction.isEnabled()) {
            viewHolder.mGoalFeedAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mProgressDialog.show();
                    RESTUpvote sm = new RESTUpvote(UserHelper.getInstance().getOwnerProfile().username, feed.guid);
                    sm.setListener(new RESTUpvote.Listener() {
                        @Override
                        public void onSuccess() {
                            mProgressDialog.cancel();
                            feed.upvoteCount++;
                            feed.hasVoted = true;
                            mHasVoted.add(feed.guid);
                            notifyItemChanged(pos);
                        }

                        @Override
                        public void onFailure(String errMsg) {
                            mProgressDialog.cancel();
                            Toast.makeText(mContext, errMsg, Toast.LENGTH_SHORT).show();
                        }
                    });
                    sm.execute();
                }
            });
        }
    }

    private void setupImage(final GoalFeed feed, final TextView textView) {
        User user = UserHelper.getInstance().getAllContacts().get(feed.createdUsername);
        if (user != null) { //existing user
            if (user.profileBitmapImage == null)
                textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile_default_small, 0, 0);
            else {
                Drawable profileDrawableImage = mImages.get(user.username);

                if (profileDrawableImage == null) {
                    int size = ImageHelper.dpToPx(mContext.getResources(), Constants.PROFILE_ROW_SIZE);
                    Bitmap image = Bitmap.createScaledBitmap(user.profileBitmapImage, size, size, false);
                    profileDrawableImage = ImageHelper.getRoundedCornerDrawable(mContext.getResources(),
                            image, Constants.CIRCLE_PROFILE);
                    mImages.put(user.username, profileDrawableImage);
                }
                textView.setCompoundDrawablesWithIntrinsicBounds(null, profileDrawableImage, null, null);
            }
        } else { // new user
            Drawable profileDrawableImage = mImages.get(feed.createdUsername);

            if (profileDrawableImage == null) {
                VolleyRequestQueue.getInstance().getImageLoader().get(RESTGetPhoto.getURL(feed.createdUsername), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            int size = ImageHelper.dpToPx(mContext.getResources(), Constants.PROFILE_ROW_SIZE);
                            Bitmap temp = Bitmap.createScaledBitmap(response.getBitmap(), size, size, false);
                            Drawable profileDrawableImage = ImageHelper.getRoundedCornerDrawable(mContext.getResources(),
                                    temp, Constants.CIRCLE_PROFILE);
                            mImages.put(feed.createdUsername, profileDrawableImage);
                            textView.setCompoundDrawablesWithIntrinsicBounds(null, profileDrawableImage, null, null);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile_default_small, 0, 0);
                        mImages.put(feed.createdUsername, textView.getCompoundDrawables()[1]);
                    }
                });
            } else {
                textView.setCompoundDrawablesWithIntrinsicBounds(null, profileDrawableImage, null, null);
            }
        }
    }
}