package com.github.q115.goalie_android.ui.main.feeds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
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
import com.github.q115.goalie_android.models.GoalFeed;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.ui.DelayedProgressDialog;
import com.github.q115.goalie_android.utils.GoalHelper;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

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

public class FeedsRecycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static class FeedsHolder extends RecyclerView.ViewHolder {
        private final TextView mGoalPerson;
        private final TextView mGoalResult;
        private final TextView mUpvoteCount;
        private final Button mGoalFeedAction;

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

    private ArrayList<GoalFeed> mGoalFeedList;
    private final DelayedProgressDialog mProgressDialog;

    public FeedsRecycler() {
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_feed, parent, false);
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
            textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile_default_small, 0, 0);
            textView.setTag(createdUsername);
            ImageLoader imageLoader = VolleyRequestQueue.getInstance().getImageLoader();
            imageLoader.get(RESTGetPhoto.getURL(createdUsername), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        Drawable profileDrawableImage = convertProfileImageToRoundedDrawable(textView.getContext().getResources(), response.getBitmap());
                        mCachedImages.put(createdUsername, profileDrawableImage);
                    } else { // not yet loaded
                        return;
                    }

                    if (textView.getTag() != null && !((String) textView.getTag()).equals(createdUsername))
                        return;
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, mCachedImages.get(createdUsername), null, null);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    mCachedImages.put(createdUsername, ContextCompat.getDrawable(textView.getContext(), R.drawable.ic_profile_default_small));

                    if (textView.getTag() != null && !((String) textView.getTag()).equals(createdUsername))
                        return;
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, mCachedImages.get(createdUsername), null, null);
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
                profileRoundedDrawableImage = convertProfileImageToRoundedDrawable(textView.getContext().getResources(), user.profileBitmapImage);
                mCachedImages.put(user.username, profileRoundedDrawableImage);
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(null, profileRoundedDrawableImage, null, null);
        }
    }

    private Drawable convertProfileImageToRoundedDrawable(Resources res, Bitmap profileImage) {
        int size = ImageHelper.dpToPx(res, Constants.PROFILE_ROW_SIZE);
        Bitmap image = Bitmap.createScaledBitmap(profileImage, size, size, false);
        return ImageHelper.getRoundedCornerDrawable(res, image, Constants.CIRCLE_PROFILE);
    }

    private void setupButtonText(TextView goalResult, TextView goalFeedAction, GoalFeed feed) {
        if (feed.goalCompleteResult.isActive()) {
            goalResult.setText(String.format(goalResult.getContext().getString(R.string.feed_title),
                    goalResult.getContext().getString(R.string.started), feed.wager));
            goalFeedAction.setText(goalResult.getContext().getString(R.string.goodluck));
        } else {
            goalResult.setText(String.format(goalResult.getContext().getString(R.string.feed_title),
                    goalResult.getContext().getString(R.string.completed), feed.wager));
            goalFeedAction.setText(goalResult.getContext().getString(R.string.congrats));
        }
    }

    private void setupButtonAction(Button goalFeedAction, final GoalFeed feed, final int position) {
        goalFeedAction.setEnabled(!feed.hasVoted && !mHasVoted.contains(feed.guid));
        if (goalFeedAction.isEnabled()) {
            final FragmentActivity context = (FragmentActivity) goalFeedAction.getContext();
            goalFeedAction.setTag(feed.guid);
            goalFeedAction.setOnClickListener(view -> {
                mProgressDialog.show(context.getSupportFragmentManager(), "DelayedProgressDialog");
                RESTUpvote sm = new RESTUpvote(UserHelper.getInstance().getOwnerProfile().username, feed.guid);
                sm.setListener(new RESTUpvote.Listener() {
                    @Override
                    public void onSuccess() {
                        if (goalFeedAction.getTag() != null && !((String) goalFeedAction.getTag()).equals(feed.guid))
                            return;

                        mProgressDialog.cancel();
                        feed.upvoteCount++;
                        feed.hasVoted = true;
                        mHasVoted.add(feed.guid);
                        notifyItemChanged(position);
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        mProgressDialog.cancel();
                        Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
                sm.execute();
            });
        }
    }
}