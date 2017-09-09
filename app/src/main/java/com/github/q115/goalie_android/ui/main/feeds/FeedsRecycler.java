package com.github.q115.goalie_android.ui.main.feeds;

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
import com.github.q115.goalie_android.ui.DelayedProgressDialog;
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

    private static HashSet<String> mHasVoted; // TODO: doesn't persist over restart.
    private static HashMap<String, Drawable> mCachedImages;

    private FragmentActivity mContext;
    private ArrayList<GoalFeed> mGoalFeedList;
    private DelayedProgressDialog mProgressDialog;

    public FeedsRecycler(FragmentActivity context) {
        this.mContext = context;
        this.mGoalFeedList = GoalHelper.getInstance().getFeeds();
        this.mProgressDialog = new DelayedProgressDialog();

        if (mHasVoted == null)
            mHasVoted = new HashSet<>();
        if (mCachedImages == null)
            mCachedImages = new HashMap<>();
    }

    @Override
    public int getItemCount() {
        return mGoalFeedList.size();
    }

    public void notifyDataSetHasChanged() {
        mGoalFeedList = GoalHelper.getInstance().getFeeds();
        super.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mContext.getLayoutInflater().inflate(R.layout.list_item_feed, parent, false);
        return new FeedsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FeedsHolder viewHolder = (FeedsHolder) holder;
        GoalFeed feed = mGoalFeedList.get(position);

        // set profile image
        viewHolder.mGoalPerson.setText(feed.createdUsername);
        setupProfileImage(feed, viewHolder.mGoalPerson);

        // setup vote count
        viewHolder.mUpvoteCount.setText(String.valueOf(feed.upvoteCount));
        viewHolder.mUpvoteCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up, 0, 0, 0);

        // set up button
        setupButtonText(viewHolder.mGoalResult, viewHolder.mGoalFeedAction, feed);
        setupButtonAction(viewHolder.mGoalFeedAction, feed, position);
    }

    private void setupProfileImage(GoalFeed feed, TextView textView) {
        User user = UserHelper.getInstance().getAllContacts().get(feed.createdUsername);
        if (user == null) {
            setNonExistingUserImage(feed.createdUsername, textView);
        } else {
            setExistingUserImage(user, textView);
        }
    }

    private void setNonExistingUserImage(final String createdUsername, final TextView textView) {
        Drawable profileRoundedDrawableImage = mCachedImages.get(createdUsername);

        if (profileRoundedDrawableImage == null) {
            ImageLoader imageLoader = VolleyRequestQueue.getInstance().getImageLoader();
            imageLoader.get(RESTGetPhoto.getURL(createdUsername), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        Drawable profileDrawableImage = convertProfileImageToRoundedDrawable(response.getBitmap());
                        textView.setCompoundDrawablesWithIntrinsicBounds(null, profileDrawableImage, null, null);
                        mCachedImages.put(createdUsername, profileDrawableImage);
                    } else {
                        textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile_default_small, 0, 0);
                        mCachedImages.put(createdUsername, textView.getCompoundDrawables()[1]);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile_default_small, 0, 0);
                    mCachedImages.put(createdUsername, textView.getCompoundDrawables()[1]);
                }
            });
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(null, profileRoundedDrawableImage, null, null);
        }
    }

    private void setExistingUserImage(User user, TextView textView) {
        if (user.profileBitmapImage == null)
            textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile_default_small, 0, 0);
        else {
            Drawable profileRoundedDrawableImage = mCachedImages.get(user.username);

            if (profileRoundedDrawableImage == null) {
                profileRoundedDrawableImage = convertProfileImageToRoundedDrawable(user.profileBitmapImage);
                mCachedImages.put(user.username, profileRoundedDrawableImage);
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(null, profileRoundedDrawableImage, null, null);
        }
    }

    private Drawable convertProfileImageToRoundedDrawable(Bitmap profileImage) {
        int size = ImageHelper.dpToPx(mContext.getResources(), Constants.PROFILE_ROW_SIZE);
        Bitmap image = Bitmap.createScaledBitmap(profileImage, size, size, false);
        return ImageHelper.getRoundedCornerDrawable(mContext.getResources(), image, Constants.CIRCLE_PROFILE);
    }

    private void setupButtonText(TextView goalResult, TextView goalFeedAction, GoalFeed feed) {
        if (feed.goalCompleteResult == Goal.GoalCompleteResult.Ongoing
                || feed.goalCompleteResult == Goal.GoalCompleteResult.Pending) {
            goalResult.setText(String.format(mContext.getString(R.string.feed_title),
                    mContext.getString(R.string.started), feed.wager));
            goalFeedAction.setText(mContext.getString(R.string.goodluck));
        } else {
            goalResult.setText(String.format(mContext.getString(R.string.feed_title),
                    mContext.getString(R.string.completed), feed.wager));
            goalFeedAction.setText(mContext.getString(R.string.congrats));
        }
    }

    private void setupButtonAction(Button goalFeedAction, final GoalFeed feed, final int position) {
        goalFeedAction.setEnabled(!feed.hasVoted && !mHasVoted.contains(feed.guid));
        if (goalFeedAction.isEnabled()) {
            goalFeedAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mProgressDialog.show(mContext.getSupportFragmentManager(), "DelayedProgressDialog");
                    RESTUpvote sm = new RESTUpvote(UserHelper.getInstance().getOwnerProfile().username, feed.guid);
                    sm.setListener(new RESTUpvote.Listener() {
                        @Override
                        public void onSuccess() {
                            mProgressDialog.cancel();
                            feed.upvoteCount++;
                            feed.hasVoted = true;
                            mHasVoted.add(feed.guid);
                            notifyItemChanged(position);
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
}