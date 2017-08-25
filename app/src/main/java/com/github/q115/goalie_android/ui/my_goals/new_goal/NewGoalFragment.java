package com.github.q115.goalie_android.ui.my_goals.new_goal;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.github.q115.goalie_android.R;

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

public class NewGoalFragment extends Fragment implements NewGoalView {
    private ProgressDialog mProgressDialog;
    private NewGoalPresenter mNewGoalPresenter;
    private String mTitle;

    private EditText mGoalTitle;
    private TextView mGoalStart;
    private TextView mGoalEnd;
    private TextView mGoalWager;
    private TextView mGoalWagerPercentage;
    private EditText mGoalEncouragement;
    private AppCompatSpinner mGoalRefereeSpinner;
    private EditText mGoalRefereeText;

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
        View.OnClickListener showPicker = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(view.getId());
            }
        };
        rootView.findViewById(R.id.goal_start_btn).setOnClickListener(showPicker);
        rootView.findViewById(R.id.goal_end_btn).setOnClickListener(showPicker);

        // wager
        mGoalWager = rootView.findViewById(R.id.goal_wager);
        mGoalWagerPercentage = rootView.findViewById(R.id.goal_wager_middle);
        View.OnClickListener wagerClicked = mNewGoalPresenter.onWagerClicked();
        rootView.findViewById(R.id.goal_wager_minus).setOnClickListener(wagerClicked);
        rootView.findViewById(R.id.goal_wager_plus).setOnClickListener(wagerClicked);

        // encouragement
        mGoalEncouragement = rootView.findViewById(R.id.goal_encouragement);

        // Referee
        mGoalRefereeSpinner = rootView.findViewById(R.id.goal_referee_spinner);
        mGoalRefereeSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mNewGoalPresenter.refereeArray()));
        mGoalRefereeSpinner.setOnItemSelectedListener(mNewGoalPresenter.getSelectionChangedListener());
        mGoalRefereeText = rootView.findViewById(R.id.goal_referee);
        mGoalRefereeText.addTextChangedListener(mNewGoalPresenter.getTextChangedListener());

        // set goal
        rootView.findViewById(R.id.set_goal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewGoalPresenter.setGoal(getActivity(), mGoalTitle.getText().toString().trim(), mGoalEncouragement.getText().toString().trim(),
                        mGoalRefereeSpinner.getSelectedItemPosition() == 0 ? mGoalRefereeText.getText().toString().trim() : (String) mGoalRefereeSpinner.getSelectedItem());
            }
        });

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.connecting));
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
    public void setPresenter(NewGoalPresenter presenter) {
        mNewGoalPresenter = presenter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Edittext values are saved by default, so no need to save title & others, just save timer etc.
        outState.putSerializable("presenter", mNewGoalPresenter.save());
        super.onSaveInstanceState(outState);
    }

    private void showTimePicker(int viewID) {
        // DialogFragment to host SublimePicker
        SublimePickerDialog pickerFrag = new SublimePickerDialog();
        pickerFrag.setCallback(mNewGoalPresenter.onTimePicked());

        // Options
        Pair<Boolean, SublimeOptions> optionsPair = mNewGoalPresenter.getOptions();

        if (!optionsPair.first) { // If options are not valid
            Toast.makeText(getActivity(), "No pickers activated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Valid options
        Bundle bundle = new Bundle();
        bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
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
    public void resetReferee(boolean isFromSpinner) {
        if (isFromSpinner) {
            mGoalRefereeText.clearFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && getActivity().getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
            mGoalRefereeText.removeTextChangedListener(mNewGoalPresenter.getTextChangedListener());
            mGoalRefereeText.setText("");
            mGoalRefereeText.addTextChangedListener(mNewGoalPresenter.getTextChangedListener());
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
            mProgressDialog.show();
        } else if (mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @Override
    public void updateReferee(boolean isFromSpinner, int position) {
        if (isFromSpinner) {
            mGoalRefereeSpinner.setSelection(position);
        }
    }
}
