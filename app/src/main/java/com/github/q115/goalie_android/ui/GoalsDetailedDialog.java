package com.github.q115.goalie_android.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.https.RESTRemind;
import com.github.q115.goalie_android.https.RESTUpdateGoal;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.utils.UserHelper;

/**
 * Created by Qi on 8/13/2017.
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

    /// <summary>
    /// default constructor. Needed so rotation doesn't crash
    /// </summary>
    public GoalsDetailedDialog() {
        super();
    }

    @NonNull
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

    /// <summary>
    /// Set click events and set max length of editview.
    /// </summary>
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
            getDialog().findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    remindClicked(isMyGoal);
                }
            });

            getDialog().findViewById(R.id.btn_3).setVisibility(View.GONE);

            getDialog().findViewById(R.id.goal_from).setVisibility(View.GONE);
            ((TextView) getDialog().findViewById(R.id.goal_referee)).setText(mReferee);
            ((TextView) getDialog().findViewById(R.id.goal_referee)).setCompoundDrawablesWithIntrinsicBounds(
                    null, new BitmapDrawable(getActivity().getResources(), mProfileImage), null, null);
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
                getDialog().findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionPicked(Goal.GoalCompleteResult.Success);
                    }
                });

                getDialog().findViewById(R.id.btn_3).setVisibility(View.VISIBLE);
                ((Button) getDialog().findViewById(R.id.btn_3)).setText(getString(R.string.remind_friend));
                getDialog().findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        remindClicked(!isMyGoal);
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
                    null, new BitmapDrawable(getResources(), mProfileImage), null, null);
        }
    }

    public void actionPicked(Goal.GoalCompleteResult goalCompleteResult) {
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setMessage(getString(R.string.connecting));
        progress.show();

        final String goalCompleteResultInt = String.valueOf(goalCompleteResult.ordinal());
        RESTUpdateGoal sm = new RESTUpdateGoal(UserHelper.getInstance().getOwnerProfile().username, goalCompleteResult);
        sm.setListener(new RESTUpdateGoal.Listener() {
            @Override
            public void onSuccess() {
                progress.cancel();
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent(goalCompleteResultInt));
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

    public void remindClicked(boolean isToReferee) {
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setMessage(getString(R.string.connecting));
        progress.show();

        RESTRemind sm = new RESTRemind(UserHelper.getInstance().getOwnerProfile().username, mReferee, isToReferee, mGuid);
        sm.setListener(new RESTRemind.Listener() {
            @Override
            public void onSuccess() {
                progress.cancel();
                Toast.makeText(getDialog().getContext(), "Successfully Sent a reminder to " + mReferee, Toast.LENGTH_LONG).show();
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

    public void delete(String guid) {
        UserHelper.getInstance().deleteGoal(guid);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent("deleted"));
        dismiss();
    }
}