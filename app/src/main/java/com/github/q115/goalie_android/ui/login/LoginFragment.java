package com.github.q115.goalie_android.ui.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.q115.goalie_android.R;

/**
 * Created by Qi on 8/6/2017.
 */

public class LoginFragment extends Fragment implements LoginView {
    private LoginPresenter mPresenter;
    private TextView serverMsg;
    private ProgressDialog mProgressDialog;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        rootView.findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverMsg.setVisibility(View.GONE);
                mPresenter.register(getActivity(), ((TextView) rootView.findViewById(R.id.username)).getText().toString());
            }
        });

        EditText username = rootView.findViewById(R.id.username);
        username.setOnEditorActionListener(handleEditorAction());
        serverMsg = rootView.findViewById(R.id.register_server_response);
        serverMsg.setVisibility(View.GONE);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.connecting));
        mProgressDialog.setCancelable(false);

        return rootView;
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
                    serverMsg.setVisibility(View.GONE);
                    mPresenter.register(getActivity(), ((TextView) getView().findViewById(R.id.username)).getText().toString());
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void showRegisterError(String msg) {
        serverMsg.setVisibility(View.VISIBLE);
        serverMsg.setText(msg);
    }

    @Override
    public void registerSuccess(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        getActivity().finish();
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
