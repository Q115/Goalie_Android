package com.github.q115.goalie_android.ui;

import android.app.Dialog;
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

import com.github.q115.goalie_android.R;

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
        } else {
            isMyGoal = getArguments().getBoolean("isMyGoal");
            mTitle = getArguments().getString("title");
            mEnd = getArguments().getString("end");
            mStart = getArguments().getString("start");
            mReputation = getArguments().getString("reputation");
            mEncouragement = getArguments().getString("encouragement");
            mReferee = getArguments().getString("referee");
            mProfileImage = getArguments().getParcelable("profile");
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
            getDialog().findViewById(R.id.btn_delete).setVisibility(View.VISIBLE);
            getDialog().findViewById(R.id.btn_failed).setVisibility(View.VISIBLE);
            ((Button) getDialog().findViewById(R.id.btn_accept)).setText(getString(R.string.complete));

            getDialog().findViewById(R.id.goal_from).setVisibility(View.GONE);
            ((TextView) getDialog().findViewById(R.id.goal_referee)).setText(mReferee);
            ((TextView) getDialog().findViewById(R.id.goal_referee)).setCompoundDrawablesWithIntrinsicBounds(
                    null, new BitmapDrawable(getActivity().getResources(), mProfileImage), null, null);
        } else {
            getDialog().findViewById(R.id.btn_delete).setVisibility(View.GONE);
            getDialog().findViewById(R.id.btn_failed).setVisibility(View.GONE);
            ((Button) getDialog().findViewById(R.id.btn_accept)).setText(getString(R.string.accept));

            getDialog().findViewById(R.id.goal_referee).setVisibility(View.GONE);
            ((TextView) getDialog().findViewById(R.id.goal_from)).setText(mReferee);
            ((TextView) getDialog().findViewById(R.id.goal_from)).setCompoundDrawablesWithIntrinsicBounds(
                    null, new BitmapDrawable(getResources(), mProfileImage), null, null);
        }
    }

    public void acceptClicked() {

    }

    public void deleteClicked() {

    }

    public void failedClicked() {

    }

    public void completeClicked() {

    }
}