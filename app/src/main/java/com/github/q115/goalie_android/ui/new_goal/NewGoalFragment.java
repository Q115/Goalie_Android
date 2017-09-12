package com.github.q115.goalie_android.ui.new_goal;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.DelayedProgressDialog;
import com.github.q115.goalie_android.utils.ViewHelper;

import java.util.HashMap;

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

public class NewGoalFragment extends Fragment implements NewGoalFragmentView {
    private DelayedProgressDialog mProgressDialog;
    private NewGoalFragmentPresenter mNewGoalPresenter;
    private String mTitle;

    private EditText mGoalTitle;
    private TextView mGoalStart;
    private TextView mGoalEnd;
    private TextView mGoalWager;
    private TextView mGoalWagerPercentage;
    private EditText mGoalEncouragement;
    private AppCompatSpinner mGoalRefereeSpinner;
    private EditText mGoalRefereeText;
    private Switch isGoalPublicFeed;

    private TextWatcher textWatcher;

    public NewGoalFragment() {
    }

    public static NewGoalFragment newInstance(String title) {
        NewGoalFragment fm = new NewGoalFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fm.setArguments(bundle);
        return fm;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitle = getArguments().getString("title");
    }

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_goal, container, false);

        // title
        mGoalTitle = rootView.findViewById(R.id.goal_title);
        if (mTitle != null && mGoalTitle.getText().length() == 0)
            mGoalTitle.setText(mTitle);

        // start & end
        mGoalStart = rootView.findViewById(R.id.goal_start_text);
        mGoalEnd = rootView.findViewById(R.id.goal_end_text);
        View.OnClickListener showPicker = mNewGoalPresenter.getTimePickerClickedListener();
        rootView.findViewById(R.id.goal_start_btn).setOnClickListener(showPicker);
        rootView.findViewById(R.id.goal_end_btn).setOnClickListener(showPicker);

        // wager
        mGoalWager = rootView.findViewById(R.id.goal_wager);
        mGoalWagerPercentage = rootView.findViewById(R.id.goal_wager_middle);
        View.OnClickListener wagerClicked = mNewGoalPresenter.getWagerClickedListener();
        rootView.findViewById(R.id.goal_wager_minus).setOnClickListener(wagerClicked);
        rootView.findViewById(R.id.goal_wager_plus).setOnClickListener(wagerClicked);

        // Referee
        mGoalRefereeSpinner = rootView.findViewById(R.id.goal_referee_spinner);
        mGoalRefereeSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, mNewGoalPresenter.getRefereeArray()));
        mGoalRefereeSpinner.setOnItemSelectedListener(mNewGoalPresenter.getSelectionChangedListener());

        textWatcher = mNewGoalPresenter.getTextChangedListener();
        mGoalRefereeText = rootView.findViewById(R.id.goal_referee);
        mGoalRefereeText.addTextChangedListener(textWatcher);

        // is public
        isGoalPublicFeed = rootView.findViewById(R.id.is_public_goal);

        // encouragement
        mGoalEncouragement = rootView.findViewById(R.id.goal_encouragement);

        // set goal
        rootView.findViewById(R.id.set_goal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String referee = mGoalRefereeSpinner.getSelectedItemPosition() == 0 ?
                        mGoalRefereeText.getText().toString().trim() : (String) mGoalRefereeSpinner.getSelectedItem();
                mNewGoalPresenter.setGoal(getActivity(), mGoalTitle.getText().toString().trim(),
                        mGoalEncouragement.getText().toString().trim(), referee, isGoalPublicFeed.isChecked());
            }
        });

        mProgressDialog = new DelayedProgressDialog();
        mProgressDialog.setCancelable(false);

        if (savedInstanceState != null) {
            mNewGoalPresenter.restore((HashMap<String, String>) savedInstanceState.getSerializable("presenter"));
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mNewGoalPresenter.start();
    }

    @Override
    public void setPresenter(NewGoalFragmentPresenter presenter) {
        mNewGoalPresenter = presenter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Edittext values are saved by default, so no need to save title & others, just timer etc.
        outState.putSerializable("presenter", mNewGoalPresenter.getSaveHash());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void showTimePicker(int viewID) {
        SublimePickerDialog pickerFrag = new SublimePickerDialog();
        SublimePickerDialog.Callback onTimePickedCallback = mNewGoalPresenter.getTimePickerCallbackListener();
        pickerFrag.setCallback(onTimePickedCallback);

        Bundle bundle = new Bundle();
        bundle.putParcelable("SUBLIME_OPTIONS", mNewGoalPresenter.getSublimePickerOptions(viewID));
        bundle.putInt("viewID", viewID);

        pickerFrag.setArguments(bundle);
        pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        pickerFrag.show(getActivity().getSupportFragmentManager(), "SublimePickerDialog");
    }

    @Override
    public void updateTime(boolean isStart, String date) {
        if (isStart)
            mGoalStart.setText(date);
        else
            mGoalEnd.setText(date);
    }

    @Override
    public void updateWager(long wagering, long total, int percent) {
        mGoalWager.setText(String.format(getString(R.string.wagered), wagering, total));
        mGoalWagerPercentage.setText(String.format(getString(R.string.percent), percent));
    }

    @Override
    public void updateRefereeOnSpinner(int position) {
        mGoalRefereeSpinner.setSelection(position);
    }

    @Override
    public void resetReferee(boolean isFromSpinner) {
        if (isFromSpinner) {
            mGoalRefereeText.clearFocus();
            ViewHelper.hideKeyboard(getActivity());

            mGoalRefereeText.removeTextChangedListener(textWatcher);
            mGoalRefereeText.setText("");
            mGoalRefereeText.addTextChangedListener(textWatcher);
        } else {
            mGoalRefereeSpinner.setSelection(0);
        }
    }

    @Override
    public void onSetGoal(boolean isSuccessful, String errMsg) {
        if (isSuccessful) {
            Toast.makeText(getActivity(), getString(R.string.ok_goal), Toast.LENGTH_SHORT).show();
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle(getString(R.string.error_goal));
            alertDialog.setMessage(errMsg);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (DialogInterface.OnClickListener) null);
            alertDialog.show();
        }
    }

    @Override
    public void updateProgress(boolean shouldShow) {
        if (shouldShow) {
            mProgressDialog.show(getActivity().getSupportFragmentManager(), "DelayedProgressDialog");
        } else {
            mProgressDialog.cancel();
        }
    }
}
