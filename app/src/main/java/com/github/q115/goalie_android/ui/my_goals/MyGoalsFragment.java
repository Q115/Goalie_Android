package com.github.q115.goalie_android.ui.my_goals;

import android.animation.Animator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.MainActivity;
import com.github.q115.goalie_android.ui.my_goals.new_goal.NewGoalActivity;
import com.github.q115.goalie_android.ui.my_goals.popular_goal.PopularGoalActivity;

public class MyGoalsFragment extends Fragment implements View.OnTouchListener, MyGoalsView {
    private FloatingActionButton mFAB;
    private LinearLayout mFABMenu1;
    private LinearLayout mFABMenu2;
    private Animator.AnimatorListener mAnimatorListener;
    private RecyclerView mGoalList;
    private MyGoalsPresenter mMyGoalsPresenter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean isRefresherEnabled;

    public MyGoalsFragment() {
    }

    public static MyGoalsFragment newInstance() {
        return new MyGoalsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_my_goals, container, false);
        View.OnClickListener toggleFABClickListener = toggleFABClickListener();

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mMyGoalsPresenter.onRefresherRefresh();
            }
        });

        mSwipeRefreshLayout.setEnabled(true);

        mGoalList = rootView.findViewById(R.id.goal_list);
        mGoalList.setOnTouchListener(this);
        mGoalList.setLayoutManager(new LinearLayoutManager(getContext()));
        mGoalList.setAdapter(new MyGoalsRecycler(getActivity()));
        mGoalList.setHasFixedSize(true);

        mFABMenu1 = rootView.findViewById(R.id.fab_menu1);
        mFABMenu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMyGoalsPresenter.toggleFAB();
                startActivity(NewGoalActivity.newIntent(getActivity()));
            }
        });
        mFABMenu1.setVisibility(View.GONE);

        mFABMenu2 = rootView.findViewById(R.id.fab_menu2);
        mFABMenu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMyGoalsPresenter.toggleFAB();
                startActivity(PopularGoalActivity.newIntent(getActivity()));
            }
        });
        mFABMenu2.setVisibility(View.GONE);

        mFAB = rootView.findViewById(R.id.fab_new_goal);
        mFAB.setOnClickListener(toggleFABClickListener);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMyGoalsPresenter.start();
    }

    @Override
    public void setPresenter(MyGoalsPresenter presenter) {
        mMyGoalsPresenter = presenter;
    }

    private View.OnClickListener toggleFABClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMyGoalsPresenter.toggleFAB();
            }
        };
    }

    @Override
    public void showFABMenu() {
        if (mGoalList != null)
            mGoalList.animate().alpha(0.3f);
        mFABMenu1.setVisibility(View.VISIBLE);
        mFABMenu2.setVisibility(View.VISIBLE);
        mFABMenu1.animate().translationY(-getResources().getDimension(R.dimen.fab_goal1_translate));
        mFABMenu2.animate().translationY(-getResources().getDimension(R.dimen.fab_goal2_translate));
        mFAB.animate().rotation(45f);
    }

    @Override
    public void closeFABMenu() {
        if (mAnimatorListener == null) {
            mAnimatorListener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    // intentionally left blank
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (!mMyGoalsPresenter.isFABOpen()) {
                        mFABMenu1.setVisibility(View.GONE);
                        mFABMenu2.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    // intentionally left blank
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                    // intentionally left blank
                }
            };
        }

        if (mGoalList != null)
            mGoalList.animate().alpha(1f);
        mFABMenu1.animate().translationY(0);
        mFABMenu2.animate().translationY(0).setListener(mAnimatorListener);
        mFAB.animate().rotation(0);
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if (v.getId() == mGoalList.getId()) {
            switch (e.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    isRefresherEnabled = true;
                    break;
                case MotionEvent.ACTION_DOWN:
                    if (mMyGoalsPresenter.isFABOpen()) {
                        mMyGoalsPresenter.closeFABMenu();
                        return true;
                    }
                    break;
                default:
                    break;
            }

            if (isRefresherEnabled) {
                RecyclerView DailyPondersList = (RecyclerView) v;
                LinearLayoutManager layoutManager = ((LinearLayoutManager) DailyPondersList.getLayoutManager());
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                int topRowVerticalPosition = (DailyPondersList.getChildCount() == 0) ? 0 : DailyPondersList.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(firstVisiblePosition == 0 && topRowVerticalPosition >= 0);
            }
        }
        return false;
    }

    @Override
    public void showRefresher(boolean shouldShow) {
        mSwipeRefreshLayout.setRefreshing(shouldShow);
    }

    @Override
    public void syncError(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void syncSuccess() {
        //TODO

        ((MainActivity) getActivity()).reloadAll();
    }

    @Override
    public void reload() {
        //TODO
    }
}
