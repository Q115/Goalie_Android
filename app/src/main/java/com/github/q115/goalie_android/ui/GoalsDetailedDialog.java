package com.github.q115.goalie_android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.https.RESTRemind;
import com.github.q115.goalie_android.https.RESTUpdateGoal;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;

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

public class GoalsDetailedDialog extends DialogFragment {
    private boolean isMyGoal;
    private String mTitle;
    private String mEnd;
    private String mStart;
    private String mReputation;
    private String mEncouragement;
    private String mReferee;
    private Goal.GoalCompleteResult mGoalCompleteResult;
    private String mGuid;
    private Bitmap mProfileImage;

    // default constructor. Needed so rotation doesn't crash
    public GoalsDetailedDialog() {
        super();
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        if (savedInstanceState != null) {
            isMyGoal = savedInstanceState.getBoolean("isMyGoal", true);
            mTitle = savedInstanceState.getString("title", "");
            mEnd = savedInstanceState.getString("end", "");
            mStart = savedInstanceState.getString("start", "");
            mReputation = savedInstanceState.getString("reputation", "");
            mEncouragement = savedInstanceState.getString("encouragement", "");
            mReferee = savedInstanceState.getString("referee", "");
            mProfileImage = savedInstanceState.getParcelable("profile");
            mGoalCompleteResult = (Goal.GoalCompleteResult) savedInstanceState.getSerializable("goalCompleteResult");
            mGuid = savedInstanceState.getString("guid", "");
        } else {
            isMyGoal = getArguments().getBoolean("isMyGoal");
            mTitle = getArguments().getString("title");
            mEnd = getArguments().getString("end");
            mStart = getArguments().getString("start");
            mReputation = getArguments().getString("reputation");
            mEncouragement = getArguments().getString("encouragement");
            mReferee = getArguments().getString("referee");
            mProfileImage = getArguments().getParcelable("profile");
            mGoalCompleteResult = (Goal.GoalCompleteResult) getArguments().getSerializable("goalCompleteResult");
            mGuid = getArguments().getString("guid", "");
        }

        builder.setView(inflater.inflate(R.layout.dialog_my_goals_detail, null));
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isMyGoal", isMyGoal);
        outState.putString("title", mTitle);
        outState.putString("end", mEnd);
        outState.putString("start", mStart);
        outState.putString("reputation", mReputation);
        outState.putString("encouragement", mEncouragement);
        outState.putString("referee", mReferee);
        outState.putParcelable("profile", mProfileImage);
        outState.putSerializable("goalCompleteResult", mGoalCompleteResult);
        outState.putString("guid", mGuid);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        ((TextView) getDialog().findViewById(R.id.goal_title)).setText(mTitle);
        ((TextView) getDialog().findViewById(R.id.goal_start)).setText(mStart);
        ((TextView) getDialog().findViewById(R.id.goal_end)).setText(mEnd);
        ((TextView) getDialog().findViewById(R.id.goal_wager)).setText(mReputation);
        ((TextView) getDialog().findViewById(R.id.goal_encouragement)).setText(mEncouragement);

        if (isMyGoal) {
            ((Button) getDialog().findViewById(R.id.btn_1)).setText(getString(R.string.delete));
            getDialog().findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete(mGuid);
                }
            });

            getDialog().findViewById(R.id.btn_2).setVisibility(View.VISIBLE);
            ((Button) getDialog().findViewById(R.id.btn_2)).setText(getString(R.string.remind_referee));
            getDialog().findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    remindClicked();
                }
            });

            getDialog().findViewById(R.id.btn_3).setVisibility(View.GONE);

            getDialog().findViewById(R.id.goal_from).setVisibility(View.GONE);
            ((TextView) getDialog().findViewById(R.id.goal_referee)).setText(mReferee);
            ((TextView) getDialog().findViewById(R.id.goal_referee)).setCompoundDrawablesWithIntrinsicBounds(
                    null, ImageHelper.getRoundedCornerBitmap(getActivity().getResources(), mProfileImage, Constants.CIRCLE_PROFILE), null, null);
        } else {
            if (mGoalCompleteResult != Goal.GoalCompleteResult.Pending) {
                ((Button) getDialog().findViewById(R.id.btn_1)).setText(getString(R.string.failed));
                getDialog().findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionPicked(Goal.GoalCompleteResult.Failed);
                    }
                });

                getDialog().findViewById(R.id.btn_2).setVisibility(View.VISIBLE);
                ((Button) getDialog().findViewById(R.id.btn_2)).setText(getString(R.string.completed));
                getDialog().findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionPicked(Goal.GoalCompleteResult.Success);
                    }
                });

                getDialog().findViewById(R.id.btn_3).setVisibility(View.VISIBLE);
                ((Button) getDialog().findViewById(R.id.btn_3)).setText(getString(R.string.remind_friend));
                getDialog().findViewById(R.id.btn_3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        remindClicked();
                    }
                });
            } else {
                ((Button) getDialog().findViewById(R.id.btn_1)).setText(getString(R.string.accept));
                getDialog().findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionPicked(Goal.GoalCompleteResult.Ongoing);
                    }
                });

                getDialog().findViewById(R.id.btn_2).setVisibility(View.GONE);
                getDialog().findViewById(R.id.btn_3).setVisibility(View.GONE);
            }

            getDialog().findViewById(R.id.goal_referee).setVisibility(View.GONE);
            ((TextView) getDialog().findViewById(R.id.goal_from)).setText(mReferee);
            ((TextView) getDialog().findViewById(R.id.goal_from)).setCompoundDrawablesWithIntrinsicBounds(
                    null, ImageHelper.getRoundedCornerBitmap(getActivity().getResources(), mProfileImage, Constants.CIRCLE_PROFILE), null, null);
        }
    }

    private void actionPicked(Goal.GoalCompleteResult goalCompleteResult) {
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setMessage(getString(R.string.connecting));
        progress.show();

        final String goalCompleteResultInt = String.valueOf(goalCompleteResult.ordinal());
        RESTUpdateGoal sm = new RESTUpdateGoal(UserHelper.getInstance().getOwnerProfile().username, mGuid, goalCompleteResult);
        sm.setListener(new RESTUpdateGoal.Listener() {
            @Override
            public void onSuccess() {
                progress.cancel();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("goalCompleteResultInt", goalCompleteResultInt);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, returnIntent);
                dismiss();
            }

            @Override
            public void onFailure(String errMsg) {
                progress.cancel();
                Toast.makeText(getDialog().getContext(), "failed: " + errMsg, Toast.LENGTH_LONG).show();
            }
        });
        sm.execute();
    }

    private void remindClicked() {
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setMessage(getString(R.string.connecting));
        progress.show();

        RESTRemind sm = new RESTRemind(UserHelper.getInstance().getOwnerProfile().username, mReferee, mGuid);
        sm.setListener(new RESTRemind.Listener() {
            @Override
            public void onSuccess() {
                progress.cancel();
                Toast.makeText(getDialog().getContext(), String.format(getString(R.string.remind_sent), mReferee), Toast.LENGTH_LONG).show();
                dismiss();
            }

            @Override
            public void onFailure(String errMsg) {
                progress.cancel();
                Toast.makeText(getDialog().getContext(), "failed: " + errMsg, Toast.LENGTH_LONG).show();
            }
        });
        sm.execute();
    }

    private void delete(final String guid) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.are_you_sure));
        alertDialog.setMessage(getString(R.string.no_refund));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UserHelper.getInstance().deleteGoal(guid);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("goalCompleteResultInt", String.valueOf(Goal.GoalCompleteResult.Cancelled.ordinal()));
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, returnIntent);
                dismiss();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (DialogInterface.OnClickListener) null);
        alertDialog.show();
    }
}