package com.github.q115.goalie_android.ui.friends;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.ui.DelayedProgressDialog;

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

public class FriendsListFragment extends Fragment implements FriendsListView {
    private FriendsListPresenter mPresenter;
    private RecyclerView mFriendsList;
    private DelayedProgressDialog mProgressDialog;

    public FriendsListFragment() {
    }

    public static FriendsListFragment newInstance() {
        return new FriendsListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);

        mFriendsList = rootView.findViewById(R.id.friends_list);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setAdapter(new FriendsListRecycler(getActivity()));

        mProgressDialog = new DelayedProgressDialog();
        mProgressDialog.setCancelable(false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        showHideEmptyMessage();
    }

    @Override
    public void setPresenter(FriendsListPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        User user = ((FriendsListRecycler) mFriendsList.getAdapter()).getItem(item.getOrder());
        if (user == null)
            return super.onContextItemSelected(item);

        switch (item.getItemId()) {
            case R.string.refresh:
                mPresenter.refresh(user.username);
                return true;
            case R.string.delete:
                mPresenter.delete(user.username);
                return true;
            default:
                return super.onContextItemSelected(item);
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

    @Override
    public void reload() {
        if (getView() != null) {

            RecyclerView friendsList = getView().findViewById(R.id.friends_list);
            ((FriendsListRecycler) friendsList.getAdapter()).notifyDataSetHasChanged();

            showHideEmptyMessage();
        }
    }

    private void showHideEmptyMessage() {
        if (getView() != null) {
            RecyclerView friendsList = getView().findViewById(R.id.friends_list);
            if (friendsList.getAdapter().getItemCount() == 0) {
                friendsList.setVisibility(View.GONE);
                getView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
            } else {
                friendsList.setVisibility(View.VISIBLE);
                getView().findViewById(R.id.empty).setVisibility(View.GONE);
            }
        }
    }
}
