package com.github.q115.goalie_android.ui.feeds;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.MainActivity;

public class FeedsFragment extends Fragment implements View.OnTouchListener, FeedsView {
    private FeedsPresenter mFeedsPresenter;
    private ProgressDialog mProgressDialog;
    private RecyclerView mFeedsList;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isRefresherEnabled;

    public FeedsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FeedsFragment newInstance() {
        return new FeedsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_feeds, container, false);
        mFeedsList = rootView.findViewById(R.id.feed_list);
        mFeedsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFeedsList.setAdapter(new FeedsRecycler(getActivity()));

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.connecting));
        mProgressDialog.setCancelable(false);

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mFeedsPresenter.onRefresherRefresh();
            }
        });
        mSwipeRefreshLayout.setEnabled(true);

        if (mFeedsPresenter != null)
            ((MainActivity) getActivity()).attachFeedsPresenter(mFeedsPresenter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFeedsPresenter.start();
    }

    @Override
    public void setPresenter(FeedsPresenter presenter) {
        mFeedsPresenter = presenter;
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if (v.getId() == mFeedsList.getId()) {
            switch (e.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    isRefresherEnabled = true;
                    break;
                default:
                    break;
            }

            if (isRefresherEnabled) {
                RecyclerView DailyPondersList = (RecyclerView) v;
                LinearLayoutManager layoutManager = ((LinearLayoutManager) DailyPondersList.getLayoutManager());
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                int topRowVerticalPosition = (DailyPondersList.getChildCount() == 0) ? 0 : DailyPondersList.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(firstVisiblePosition <= 0 && topRowVerticalPosition >= 0);
            }
        }
        return false;
    }

    @Override
    public void reload() {
        ((FeedsRecycler) mFeedsList.getAdapter()).notifyDataSetHasChanged();
    }

    @Override
    public void syncComplete(boolean isSuccessful, String errMsg) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (isSuccessful) {
            ((MainActivity) getActivity()).reloadAll();
        } else {
            Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
        }
    }
}