package com.github.q115.goalie_android.ui.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.https.RESTUpdateUserInfo;
import com.github.q115.goalie_android.utils.PreferenceHelper;
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

public class UpdateProfileDialog extends DialogFragment {
    private String mBio;

    // default constructor. Needed so rotation doesn't crash
    public UpdateProfileDialog() {
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
            mBio = savedInstanceState.getString("bio", mBio);
        }

        builder.setView(inflater.inflate(R.layout.dialog_update_profile_info, null))
                .setTitle(R.string.update_title)
                .setPositiveButton(R.string.update, null)
                .setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    // show keyboard
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getDialog().getWindow() != null)
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mBio = ((EditText) getDialog().findViewById(R.id.profile_bio)).getText().toString().trim();

        outState.putString("bio", mBio);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mBio == null)
            mBio = getArguments().getString("bio");

        EditText profileBioInput = getDialog().findViewById(R.id.profile_bio);
        profileBioInput.setOnEditorActionListener(handleEditorAction());
        profileBioInput.setSingleLine(false);
        profileBioInput.setText(mBio);

        (getDialog().findViewById(R.id.update_profile_status)).setVisibility(View.INVISIBLE);

        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    update();
                }
            });
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDialog().dismiss();
                }
            });
        }
    }

    private EditText.OnEditorActionListener handleEditorAction() {
        return new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    update();
                    return true;
                }
                return false;
            }
        };
    }

    private void update() {
        final String newBio = ((EditText) getDialog().findViewById(R.id.profile_bio)).getText().toString().trim();

        //check if anything changed before pinging the server
        if (newBio.equals(UserHelper.getInstance().getOwnerProfile().bio)) {
            this.dismiss();
        } else {
            //hide keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            // verify if the soft keyboard is open
            if (imm != null && getDialog().getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getDialog().getCurrentFocus().getWindowToken(), 0);
            }

            final ProgressDialog progress = new ProgressDialog(getActivity());
            progress.setMessage(getString(R.string.updating));
            progress.show();

            RESTUpdateUserInfo sm = new RESTUpdateUserInfo(UserHelper.getInstance().getOwnerProfile().username,
                    newBio, PreferenceHelper.getInstance().getPushID());
            sm.setListener(new RESTUpdateUserInfo.Listener() {
                @Override
                public void onSuccess() {
                    progress.cancel();
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent(newBio));
                    dismiss();
                }

                @Override
                public void onFailure(String errMsg) {
                    progress.cancel();
                    TextView updatestatus = getDialog().findViewById(R.id.update_profile_status);
                    updatestatus.setVisibility(View.VISIBLE);
                    updatestatus.setText(String.format(getString(R.string.error_updating), errMsg));
                }
            });
            sm.execute();
        }
    }
}