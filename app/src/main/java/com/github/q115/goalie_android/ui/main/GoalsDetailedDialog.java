package com.github.q115.goalie_android.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
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
import com.github.q115.goalie_android.ui.DelayedProgressDialog;
import com.github.q115.goalie_android.utils.GoalHelper;
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

    private DelayedProgressDialog delayedProgressDialog;
    private final View.OnClickListener deleteAction;
    private final View.OnClickListener remindAction;
    private final View.OnClickListener updateAction;

    public GoalsDetailedDialog() {
        super();

        deleteAction = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        };
        remindAction = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remind();
            }
        };
        updateAction = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGoal((Goal.GoalCompleteResult) view.getTag());
            }
        };
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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

        LayoutInflater inflater = getActivity().getLayoutInflater();
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

        delayedProgressDialog = new DelayedProgressDialog();

        ((TextView) getDialog().findViewById(R.id.goal_title)).setText(mTitle);
        ((TextView) getDialog().findViewById(R.id.goal_start)).setText(mStart);
        ((TextView) getDialog().findViewById(R.id.goal_end)).setText(mEnd);
        ((TextView) getDialog().findViewById(R.id.goal_wager)).setText(mReputation);
        ((TextView) getDialog().findViewById(R.id.goal_encouragement)).setText(mEncouragement);

        int goal_referee_invisible;
        int goal_referee_visible;

        if (isMyGoal) {
            setupMyGoals();
            goal_referee_invisible = R.id.goal_from;
            goal_referee_visible = R.id.goal_referee;
        } else {
            setupMyRequests();
            goal_referee_invisible = R.id.goal_referee;
            goal_referee_visible = R.id.goal_from;
        }

        getDialog().findViewById(goal_referee_invisible).setVisibility(View.GONE);
        ((TextView) getDialog().findViewById(goal_referee_visible)).setText(mReferee);
        ((TextView) getDialog().findViewById(goal_referee_visible)).setCompoundDrawablesWithIntrinsicBounds(
                null, ImageHelper.getRoundedCornerDrawable(
                        getActivity().getResources(), mProfileImage, Constants.CIRCLE_PROFILE), null, null);
    }

    private void setupMyGoals() {
        Button btn1 = getDialog().findViewById(R.id.btn_1);
        Button btn2 = getDialog().findViewById(R.id.btn_2);
        Button btn3 = getDialog().findViewById(R.id.btn_3);

        btn1.setText(getString(R.string.delete));
        btn1.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        btn1.setOnClickListener(deleteAction);

        btn2.setVisibility(View.VISIBLE);
        btn2.setText(getString(R.string.remind_referee));
        btn2.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        btn2.setOnClickListener(remindAction);

        btn3.setVisibility(View.GONE);
    }

    private void setupMyRequests() {
        Button btn1 = getDialog().findViewById(R.id.btn_1);
        Button btn2 = getDialog().findViewById(R.id.btn_2);
        Button btn3 = getDialog().findViewById(R.id.btn_3);

        if (mGoalCompleteResult != Goal.GoalCompleteResult.Pending) {
            btn1.setText(getString(R.string.fail));
            btn1.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            btn1.setTag(Goal.GoalCompleteResult.Failed);
            btn1.setOnClickListener(updateAction);

            btn2.setVisibility(View.VISIBLE);
            btn2.setText(getString(R.string.pass));
            btn2.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
            btn2.setTag(Goal.GoalCompleteResult.Success);
            btn2.setOnClickListener(updateAction);

            btn3.setVisibility(View.VISIBLE);
            btn3.setText(getString(R.string.remind_friend));
            btn3.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            btn3.setOnClickListener(remindAction);
        } else {
            btn1.setText(getString(R.string.accept));
            btn1.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
            btn1.setTag(Goal.GoalCompleteResult.Ongoing);
            btn1.setOnClickListener(updateAction);

            btn2.setVisibility(View.GONE); //No decline for now until server adds implemention
            btn3.setVisibility(View.GONE);
        }
    }

    private void updateGoal(Goal.GoalCompleteResult goalCompleteResult) {
        delayedProgressDialog.show(getActivity().getSupportFragmentManager(), "DelayedProgressDialog");

        String fromUsername = UserHelper.getInstance().getOwnerProfile().username;
        final String goalCompleteResultString = String.valueOf(goalCompleteResult.ordinal());
        RESTUpdateGoal sm = new RESTUpdateGoal(fromUsername, mGuid, goalCompleteResult);
        sm.setListener(new RESTUpdateGoal.Listener() {
            @Override
            public void onSuccess() {
                delayedProgressDialog.cancel();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("goalCompleteResultInt", goalCompleteResultString);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, returnIntent);
                dismissAllowingStateLoss();
            }

            @Override
            public void onFailure(String errMsg) {
                delayedProgressDialog.cancel();
                Toast.makeText(getDialog().getContext(), "failed: " + errMsg, Toast.LENGTH_LONG).show();
            }
        });
        sm.execute();
    }

    private void remind() {
        delayedProgressDialog.show(getActivity().getSupportFragmentManager(), "DelayedProgressDialog");

        String fromUsername = UserHelper.getInstance().getOwnerProfile().username;
        RESTRemind sm = new RESTRemind(fromUsername, mReferee, mGuid, isMyGoal);
        sm.setListener(new RESTRemind.Listener() {
            @Override
            public void onSuccess() {
                delayedProgressDialog.cancel();
                Toast.makeText(getDialog().getContext(),
                        String.format(getString(R.string.remind_sent), mReferee), Toast.LENGTH_LONG).show();
                dismissAllowingStateLoss();
            }

            @Override
            public void onFailure(String errMsg) {
                delayedProgressDialog.cancel();
                Toast.makeText(getDialog().getContext(), "failed: " + errMsg, Toast.LENGTH_LONG).show();
            }
        });
        sm.execute();
    }

    private void delete() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.are_you_sure));
        alertDialog.setMessage(getString(R.string.no_refund));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GoalHelper.getInstance().deleteGoal(mGuid);
                Intent returnIntent = new Intent();
                String goalCompleteResultString = String.valueOf(Goal.GoalCompleteResult.Cancelled.ordinal());
                returnIntent.putExtra("goalCompleteResultInt", goalCompleteResultString);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, returnIntent);
                dismiss();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (DialogInterface.OnClickListener) null);
        alertDialog.show();
    }
}