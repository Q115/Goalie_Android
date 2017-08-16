package com.github.q115.goalie_android.ui.my_goals.popular_goal;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.my_goals.new_goal.NewGoalActivity;

import static com.github.q115.goalie_android.Constants.RESULT_GOAL_SET;

/**
 * Created by Qi on 8/12/2017.
 */

public class PopularGoalRecycler extends RecyclerView.Adapter {
    public class PopularGoalHolder extends RecyclerView.ViewHolder {
        private TextView mGoalName;

        public PopularGoalHolder(View itemView) {
            super(itemView);
            mGoalName = itemView.findViewById(R.id.popular_goal_name);
        }
    }

    private FragmentActivity mContext;
    private String[] popularGoalArray;

    public PopularGoalRecycler(FragmentActivity context) {
        this.mContext = context;

        // remove self and display all others
        popularGoalArray = context.getResources().getStringArray(R.array.popular_goals);
    }

    @Override
    public int getItemCount() {
        return popularGoalArray.length;
    }

    //Must override, this inflates our Layout and instantiates and assigns
    //it to the ViewHolder.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mContext.getLayoutInflater().inflate(R.layout.list_item_popular_goals, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = NewGoalActivity.newIntent(mContext);
                intent.putExtra("title", popularGoalArray[(int) itemView.getTag()] + ": ");
                mContext.startActivityForResult(intent, RESULT_GOAL_SET);
            }
        });
        return new PopularGoalHolder(itemView);
    }

    //Bind our current data to your view holder.  Think of this as the equivalent
    //of GetView for regular Adapters.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PopularGoalHolder viewHolder = (PopularGoalHolder) holder;
        viewHolder.itemView.setTag(position);
        //Bind our data from our data source to our View References
        switch (position) {
            case 0:
                viewHolder.itemView.setBackgroundResource(R.drawable.popular1);
                break;
            case 1:
                viewHolder.itemView.setBackgroundResource(R.drawable.popular2);
                break;
            case 2:
                viewHolder.itemView.setBackgroundResource(R.drawable.popular3);
                break;
            case 3:
                viewHolder.itemView.setBackgroundResource(R.drawable.popular4);
                break;
        }
        viewHolder.mGoalName.setText(popularGoalArray[position]);
    }
}
