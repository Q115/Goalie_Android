package com.github.q115.goalie_android.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.DelayedProgressDialog;
import com.github.q115.goalie_android.utils.ViewHelper;

import androidx.fragment.app.Fragment;

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

public class LoginFragment extends Fragment implements LoginFragmentView {
    private LoginFragmentPresenter mPresenter;
    private EditText mUsername;
    private TextView mServerMsg;
    private TextView mDisclaimer;
    private DelayedProgressDialog mProgressDialog;

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
        mDisclaimer = rootView.findViewById(R.id.disclaimer);

        rootView.findViewById(R.id.btn_register).setOnClickListener(view -> {
            mServerMsg.setVisibility(View.GONE);
            mPresenter.register(getActivity(), mUsername.getText().toString());
        });

        mProgressDialog = new DelayedProgressDialog();
        mProgressDialog.setCancelable(false);

        setupDisclaimer();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewHelper.showKeyboard(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(LoginFragmentPresenter presenter) {
        mPresenter = presenter;
    }

    private EditText.OnEditorActionListener handleEditorAction() {
        return (v, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && getView() != null) {
                mServerMsg.setVisibility(View.GONE);
                mPresenter.register(getActivity(), mUsername.getText().toString());
                return true;
            }
            return false;
        };
    }

    private void setupDisclaimer() {
        String terms = Constants.URL + "/termsofservice.htm";
        String privacy = Constants.URL + "/privacypolicy.htm";

        String disclaimer = "By registering, you agree to the<br>" +
                "<a href=\"" + terms + "\">Terms of Service</a>" +
                " and <a href=\"" + privacy + "\">Privacy Policy.</a><br>";

        mDisclaimer.setText(Html.fromHtml(disclaimer, Html.FROM_HTML_MODE_LEGACY));
        mDisclaimer.setMovementMethod(LinkMovementMethod.getInstance());
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
            mProgressDialog.show(getActivity().getSupportFragmentManager(), "DelayedProgressDialog");
        } else {
            mProgressDialog.cancel();
        }
    }
}
