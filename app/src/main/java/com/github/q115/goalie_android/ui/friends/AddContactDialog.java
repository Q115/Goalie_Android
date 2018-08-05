package com.github.q115.goalie_android.ui.friends;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.https.RESTGetUserInfo;
import com.github.q115.goalie_android.ui.DelayedProgressDialog;
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

public class AddContactDialog extends DialogFragment {
    private String mUsername;
    private TextView mUpdateStatus;
    private EditText mUsernameText;
    private AddContactOnAddedListener mListener;

    public interface AddContactOnAddedListener {
        void onAdded(String username);
    }

    public void setOnAdded(AddContactOnAddedListener listener) {
        mListener = listener;
    }

    // default constructor. Needed so rotation doesn't crash
    public AddContactDialog() {
        super();
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        if (savedInstanceState != null) {
            mUsername = savedInstanceState.getString("username", mUsername);
        } else
            mUsername = "";

        builder.setView(inflater.inflate(R.layout.dialog_add_contact, null))
                .setTitle(R.string.add_title)
                .setPositiveButton(R.string.add, null)
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
        mUsername = mUsernameText.getText().toString().trim();
        outState.putString("username", mUsername);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        mUpdateStatus = getDialog().findViewById(R.id.add_friend_status);
        mUsernameText = getDialog().findViewById(R.id.add_username);

        mUsernameText.setOnEditorActionListener(handleEditorAction());
        mUsernameText.setText(mUsername);
        mUsernameText.setSelection(mUsername.length());
        mUpdateStatus.setVisibility(View.INVISIBLE);

        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addCheck();
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
                    addCheck();
                    return true;
                }
                return false;
            }
        };
    }

    private void addCheck() {
        mUsername = mUsernameText.getText().toString().trim();
        mUpdateStatus.setVisibility(View.INVISIBLE);

        if (!UserHelper.isUsernameValid(mUsername)) {
            mUpdateStatus.setVisibility(View.VISIBLE);
            mUpdateStatus.setText(getString(R.string.username_error));
        } else if (UserHelper.getInstance().getAllContacts().containsKey(mUsername)) {
            mUpdateStatus.setVisibility(View.VISIBLE);
            mUpdateStatus.setText(getString(R.string.already_friends));
        } else if (mUsername.equals(UserHelper.getInstance().getOwnerProfile().username)) {
            mUpdateStatus.setVisibility(View.VISIBLE);
            mUpdateStatus.setText(getString(R.string.no_self));
        } else {
            add();
        }
    }

    private void add() {
        ViewHelper.hideKeyboard(getActivity(), getDialog());

        final DelayedProgressDialog progressDialog = new DelayedProgressDialog();
        progressDialog.show(getActivity().getSupportFragmentManager(), "DelayedProgressDialog");

        RESTGetUserInfo sm = new RESTGetUserInfo(mUsername);
        sm.setListener(new RESTGetUserInfo.Listener() {
            @Override
            public void onSuccess() {
                progressDialog.cancel();
                mListener.onAdded(mUsername);
                dismissAllowingStateLoss();
            }

            @Override
            public void onFailure(String errMsg) {
                progressDialog.cancel();
                mUpdateStatus.setVisibility(View.VISIBLE);
                mUpdateStatus.setText(errMsg);
            }
        });
        sm.execute();
    }
}