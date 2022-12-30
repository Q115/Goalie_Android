package com.github.q115.goalie_android.ui.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.https.RESTUpdateUserInfo;
import com.github.q115.goalie_android.ui.DelayedProgressDialog;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;
import com.github.q115.goalie_android.utils.ViewHelper;

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewHelper.showKeyboard(getDialog());
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
        profileBioInput.setSelection(mBio.length());

        (getDialog().findViewById(R.id.update_profile_status)).setVisibility(View.INVISIBLE);

        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCheck();
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
                    updateCheck();
                    return true;
                }
                return false;
            }
        };
    }

    private void updateCheck() {
        mBio = ((EditText) getDialog().findViewById(R.id.profile_bio)).getText().toString().trim();

        if (mBio.equals(UserHelper.getInstance().getOwnerProfile().bio)) {
            this.dismiss();
        } else {
            update();
        }
    }

    private void update() {
        ViewHelper.hideKeyboard(getActivity(), getDialog());

        final DelayedProgressDialog progress = new DelayedProgressDialog();
        progress.show(getActivity().getSupportFragmentManager(), "DelayedProgressDialog");

        RESTUpdateUserInfo sm = new RESTUpdateUserInfo(UserHelper.getInstance().getOwnerProfile().username,
                mBio, PreferenceHelper.getInstance().getPushID());
        sm.setListener(new RESTUpdateUserInfo.Listener() {
            @Override
            public void onSuccess() {
                progress.cancel();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("bio", mBio);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, returnIntent);
                dismissAllowingStateLoss();
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