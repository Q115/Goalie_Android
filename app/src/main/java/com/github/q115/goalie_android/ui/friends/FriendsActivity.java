package com.github.q115.goalie_android.ui.friends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;

/**
 * Created by Qi on 8/5/2017.
 */

public class FriendsActivity extends AppCompatActivity implements FriendsView {
    private FriendsPresenter mPresenter;
    private FriendsListPresenter mFriendsListPresenter;

    public static Intent newIntent(Context context, String username) {
        Intent newIntent = new Intent(context, FriendsActivity.class);
        newIntent.putExtra("username", username);
        return newIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        FragmentManager fm = getSupportFragmentManager();
        FriendsListFragment friendsListFragment = (FriendsListFragment) fm.findFragmentByTag("friendsListFragment");
        if (friendsListFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            friendsListFragment = FriendsListFragment.newInstance();
            ft.add(android.R.id.content, friendsListFragment, "friendsListFragment");
            ft.commit();
        }

        // Create the presenters
        mPresenter = new FriendsPresenter(this, this);
        mFriendsListPresenter = new FriendsListPresenter(friendsListFragment);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(FriendsPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_invite_friends:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, Constants.REQUEST_PERMISSIONS_CONTACT);
                return true;
            case R.id.action_add_friends:
                AddContactDialog addContactDialog = new AddContactDialog();
                addContactDialog.show(getSupportFragmentManager(), "AddContactDialog");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_PERMISSIONS_CONTACT && resultCode == RESULT_OK && data != null) {
            mPresenter.result(data.getData());
        } else if (requestCode == Constants.RESULT_FRIENDS_ADD && resultCode == RESULT_OK && data != null) {
            mFriendsListPresenter.onAddContactDialog(data.getAction());
        }
    }

    @Override
    public void sendSMSInvite(String phoneNum) {
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, android.net.Uri.fromParts("smsto", phoneNum, null));
        sendIntent.putExtra("sms_body", getString(R.string.join_me));
        startActivity(sendIntent);
    }
}
