package com.github.q115.goalie_android.ui.my_goals;

import android.animation.Animator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsRecycler;

public class MyGoalsFragment extends Fragment {
    private boolean isFABOpen;
    private LinearLayout mFABMenu1;
    private LinearLayout mFABMenu2;
    private Animator.AnimatorListener mAnimatorListener;

    public MyGoalsFragment() {
    }

    public static MyGoalsFragment newInstance() {
        return new MyGoalsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_my_goals, container, false);

        RecyclerView goalList = rootView.findViewById(R.id.goal_list);
        goalList.setLayoutManager(new LinearLayoutManager(getContext()));
        goalList.setAdapter(new MyGoalsRecycler(getActivity()));

        View.OnClickListener toggleFABClickListener = toggleFABClickListener();
        mFABMenu1 = rootView.findViewById(R.id.fab_menu1);
        mFABMenu1.setOnClickListener(toggleFABClickListener);
        mFABMenu1.setVisibility(View.GONE);

        mFABMenu2 = rootView.findViewById(R.id.fab_menu2);
        mFABMenu2.setOnClickListener(toggleFABClickListener);
        mFABMenu2.setVisibility(View.GONE);

        FloatingActionButton fab = rootView.findViewById(R.id.fab_new_goal);
        fab.setOnClickListener(toggleFABClickListener);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        closeFABMenu();
    }

    private View.OnClickListener toggleFABClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        };
    }

    private void showFABMenu() {
        isFABOpen = true;
        mFABMenu1.setVisibility(View.VISIBLE);
        mFABMenu2.setVisibility(View.VISIBLE);
        mFABMenu1.animate().translationY(-getResources().getDimension(R.dimen.fab_goal1_translate));
        mFABMenu2.animate().translationY(-getResources().getDimension(R.dimen.fab_goal2_translate));
    }

    private void closeFABMenu() {
        if (mAnimatorListener == null) {
            mAnimatorListener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    // intentionally left blank
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (!isFABOpen) {
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

        isFABOpen = false;
        mFABMenu1.animate().translationY(0);
        mFABMenu2.animate().translationY(0).setListener(mAnimatorListener);
    }

    /*
    @Override
    public void onBackPressed() {
        if(!isFABOpen){
            super.onBackPressed();
        }else{
            closeFABMenu();
        }
    }*/
}
