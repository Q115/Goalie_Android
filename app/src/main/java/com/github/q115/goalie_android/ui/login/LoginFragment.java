package com.github.q115.goalie_android.ui.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.q115.goalie_android.R;

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

public class LoginFragment extends Fragment implements LoginView {
    private LoginPresenter mPresenter;
    private EditText mUsername;
    private TextView mServerMsg;
    private ProgressDialog mProgressDialog;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mUsername = rootView.findViewById(R.id.username);
        mUsername.setOnEditorActionListener(handleEditorAction());
        mServerMsg = rootView.findViewById(R.id.register_server_response);
        mServerMsg.setVisibility(View.GONE);

        rootView.findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mServerMsg.setVisibility(View.GONE);
                mPresenter.register(getActivity(), mUsername.getText().toString());
            }
        });

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.connecting));
        mProgressDialog.setCancelable(false);

        return rootView;
    }

    // show keyboard
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity().getWindow() != null)
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(LoginPresenter presenter) {
        mPresenter = presenter;
    }

    private EditText.OnEditorActionListener handleEditorAction() {
        return new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE && getView() != null) {
                    mServerMsg.setVisibility(View.GONE);
                    mPresenter.register(getActivity(), mUsername.getText().toString());
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void registerComplete(boolean isSuccessful, String msg) {
        if (isSuccessful) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            mServerMsg.setVisibility(View.VISIBLE);
            mServerMsg.setText(msg);
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
}
