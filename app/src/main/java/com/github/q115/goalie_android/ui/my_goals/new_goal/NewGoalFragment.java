package com.github.q115.goalie_android.ui.my_goals.new_goal;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
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

/**
 * Created by Qi on 8/11/2017.
 */

public class NewGoalFragment extends Fragment implements NewGoalView {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_goal, container, false);

        // title
        mGoalTitle = rootView.findViewById(R.id.goal_title);
        if (mTitle != null)
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
        mGoalRefereeSpinner.setOnItemSelectedListener(mNewGoalPresenter.mSelectionChangedListener);
        mGoalRefereeText = rootView.findViewById(R.id.goal_referee);
        mGoalRefereeText.addTextChangedListener(mNewGoalPresenter.mTextChangedListener);

        // set goal
        rootView.findViewById(R.id.set_goal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewGoalPresenter.setGoal();
            }
        });
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

    public void showTimePicker(int viewID) {
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
            mGoalRefereeText.removeTextChangedListener(mNewGoalPresenter.mTextChangedListener);
            mGoalRefereeText.setText("");
            mGoalRefereeText.addTextChangedListener(mNewGoalPresenter.mTextChangedListener);
        } else {
            // TODO
            mGoalRefereeSpinner.setOnItemSelectedListener(null);
            mGoalRefereeSpinner.setSelection(0);
            mGoalRefereeSpinner.setOnItemSelectedListener(mNewGoalPresenter.mSelectionChangedListener);
        }
    }
}
