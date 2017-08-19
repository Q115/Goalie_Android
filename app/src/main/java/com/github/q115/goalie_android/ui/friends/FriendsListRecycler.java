package com.github.q115.goalie_android.ui.friends;

import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by Qi on 8/4/2017.
 */

public class FriendsListRecycler extends RecyclerView.Adapter {
    public class FriendsHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private ImageView mFriendImage;
        private TextView mFriendName;
        private TextView mFriendReputation;
        private FragmentActivity mContext;

        public FriendsHolder(View itemView, FragmentActivity context) {
            super(itemView);
            mContext = context;
            mFriendImage = itemView.findViewById(R.id.friend_image);
            mFriendName = itemView.findViewById(R.id.friend_name);
            mFriendReputation = itemView.findViewById(R.id.friend_reputation);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(mFriendName.getText());
            menu.add(0, R.string.refresh, getAdapterPosition(), mContext.getString(R.string.refresh));
            menu.add(0, R.string.delete, getAdapterPosition(), mContext.getString(R.string.delete));
        }
    }

    protected FragmentActivity mContext;
    protected ArrayList<User> mUserList;

    public FriendsListRecycler(FragmentActivity context) {
        this.mContext = context;

        // remove self and display all others
        TreeMap<String, User> tempHashMap = new TreeMap<>(UserHelper.getInstance().getAllContacts());
        tempHashMap.remove(UserHelper.getInstance().getOwnerProfile().username);
        mUserList = new ArrayList<>(tempHashMap.values());
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public User getItem(int position) {
        if (position < mUserList.size())
            return mUserList.get(position);
        else
            return null;
    }

    public void addUserToList(User user) {
        if (user == null)
            return;

        mUserList.add(user);
        Collections.sort(mUserList, new Comparator<User>() {
            @Override
            public int compare(User a1, User a2) {
                return a1.username.compareTo(a2.username);
            }
        });

        super.notifyDataSetChanged();
    }

    public void notifyDataSetHasChanged() {
        TreeMap<String, User> tempHashMap = new TreeMap<>(UserHelper.getInstance().getAllContacts());
        tempHashMap.remove(UserHelper.getInstance().getOwnerProfile().username);
        mUserList = new ArrayList<>(tempHashMap.values());

        super.notifyDataSetChanged();
    }

    //Must override, this inflates our Layout and instantiates and assigns
    //it to the ViewHolder.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mContext.getLayoutInflater().inflate(R.layout.list_item_friend, parent, false);
        return new FriendsHolder(itemView, mContext);
    }

    //Bind our current data to your view holder.  Think of this as the equivalent
    //of GetView for regular Adapters.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FriendsHolder viewHolder = (FriendsHolder) holder;

        //Bind our data from our data source to our View References
        User user = mUserList.get(position);
        viewHolder.mFriendName.setText(user.username);
        viewHolder.mFriendReputation.setText(String.valueOf(user.reputation));

        if (user.profileBitmapImage == null)
            viewHolder.mFriendImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_profile_default_small));
        else
            viewHolder.mFriendImage.setImageDrawable(ImageHelper.getRoundedCornerBitmap(mContext.getResources(),
                    user.profileBitmapImage, Constants.CIRCLE_PROFILE));
    }
}
