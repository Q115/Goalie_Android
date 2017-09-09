package com.github.q115.goalie_android.ui.main.my_goals;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.ui.main.BaseRefresherFragment;
import com.github.q115.goalie_android.ui.main.GoalsDetailedDialog;
import com.github.q115.goalie_android.ui.new_goal.NewGoalActivity;
import com.github.q115.goalie_android.ui.popular_goal.PopularGoalActivity;

import static android.app.Activity.RESULT_OK;
import static com.github.q115.goalie_android.Constants.RESULT_GOAL_SET;
import static com.github.q115.goalie_android.Constants.RESULT_MY_GOAL_DIALOG;

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
public class MyGoalsFragment extends BaseRefresherFragment implements MyGoalsView {
    private FloatingActionButton mFAB;
    private LinearLayout mFABMenu1;
    private LinearLayout mFABMenu2;
    private Animator.AnimatorListener mAnimatorListener;
    private MyGoalsPresenter mMyGoalsPresenter;

    public MyGoalsFragment() {
    }

    public static MyGoalsFragment newInstance() {
        return new MyGoalsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_my_goals, container, false);

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(onRefresherRefreshListener());
        mSwipeRefreshLayout.setEnabled(true);

        mRecyclerView = rootView.findViewById(R.id.goal_list);
        mRecyclerView.addOnScrollListener(onScrollListener());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new MyGoalsRecycler(getActivity(), mMyGoalsPresenter));
        mRecyclerView.setOnTouchListener(getCloseFABMenuTouchListener());

        mEmptyMsg = rootView.findViewById(R.id.empty);
        mEmptyMsg.setOnTouchListener(getCloseFABMenuTouchListener());
        showEmptyWhenNecessary();

        mFABMenu1 = rootView.findViewById(R.id.fab_menu1);
        mFABMenu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMyGoalsPresenter.closeFABMenu();
                startActivityForResult(NewGoalActivity.newIntent(getActivity()), RESULT_GOAL_SET);
            }
        });
        mFABMenu1.setVisibility(View.GONE);

        mFABMenu2 = rootView.findViewById(R.id.fab_menu2);
        mFABMenu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMyGoalsPresenter.closeFABMenu();
                startActivityForResult(PopularGoalActivity.newIntent(getActivity()), RESULT_GOAL_SET);
            }
        });
        mFABMenu2.setVisibility(View.GONE);

        mFAB = rootView.findViewById(R.id.fab_new_goal);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMyGoalsPresenter.toggleFAB();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMyGoalsPresenter != null)
            mMyGoalsPresenter.start();
    }

    @Override
    public void setPresenter(MyGoalsPresenter presenter) {
        mMyGoalsPresenter = presenter;

        // reconnect presenter if needed.
        if (mRecyclerView != null && mRecyclerView.getAdapter() != null)
            ((MyGoalsRecycler) mRecyclerView.getAdapter()).setPresenter(mMyGoalsPresenter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_GOAL_SET && resultCode == RESULT_OK) {
            reload();
        } else if (requestCode == RESULT_MY_GOAL_DIALOG && resultCode == RESULT_OK && data != null) {
            int goalInt = Integer.parseInt(data.getStringExtra("goalCompleteResultInt"));
            if (goalInt < Goal.GoalCompleteResult.values().length) {
                switch (Goal.GoalCompleteResult.values()[goalInt]) {
                    case Cancelled:
                        Toast.makeText(getActivity(), getString(R.string.delete_toast), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            reload();
        }
    }

    private View.OnTouchListener getCloseFABMenuTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        if (mMyGoalsPresenter.isFABOpen()) {
                            mMyGoalsPresenter.closeFABMenu();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        };
    }

    @Override
    public void showFABMenu() {
        if (mRecyclerView != null)
            mRecyclerView.animate().alpha(0.3f);
        mFABMenu1.setVisibility(View.VISIBLE);
        mFABMenu2.setVisibility(View.VISIBLE);
        mFABMenu1.animate().translationY(-getResources().getDimension(R.dimen.fab_goal1_translate));
        mFABMenu2.animate().translationY(-getResources().getDimension(R.dimen.fab_goal2_translate));

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
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

        if (mRecyclerView != null)
            mRecyclerView.animate().alpha(1f);
        mFABMenu1.animate().translationY(0);
        mFABMenu2.animate().translationY(0).setListener(mAnimatorListener);

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
            mFAB.animate().rotation(0);
    }

    public void showDialog(String title, String end, String start, String reputation, String encouragment, String referee,
                           Drawable profileImage, Goal.GoalCompleteResult goalCompleteResult, String guid) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isMyGoal", true);
        bundle.putString("title", title);
        bundle.putString("end", end);
        bundle.putString("start", start);
        bundle.putString("reputation", reputation);
        bundle.putString("referee", referee);
        bundle.putString("encouragement", encouragment);
        if (profileImage instanceof RoundedBitmapDrawable)
            bundle.putParcelable("profile", ((RoundedBitmapDrawable) profileImage).getBitmap());
        else if (profileImage instanceof BitmapDrawable)
            bundle.putParcelable("profile", ((BitmapDrawable) profileImage).getBitmap());
        bundle.putSerializable("goalCompleteResult", goalCompleteResult);
        bundle.putString("guid", guid);

        GoalsDetailedDialog detailedDialog = new GoalsDetailedDialog();
        detailedDialog.setArguments(bundle);
        detailedDialog.setTargetFragment(this, Constants.RESULT_MY_GOAL_DIALOG);
        detailedDialog.show(getActivity().getSupportFragmentManager(), "GoalsDetailedDialog");
    }

    @Override
    public void reload() {
        ((MyGoalsRecycler) mRecyclerView.getAdapter()).notifyDataSetHasChanged();
        showEmptyWhenNecessary();
    }
}
