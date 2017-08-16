package com.github.q115.goalie_android.utils;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.GoalFeed;
import com.github.q115.goalie_android.models.Goal_Table;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.ImageHelper.ImageType;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;


public class UserHelper {
    /// <summary>
    /// username -> User
    /// </summary>
    private SortedMap<String, User> mAllContacts;

    public SortedMap<String, User> getAllContacts() {
        return mAllContacts;
    }

    private ArrayList<Goal> mRequests;

    public ArrayList<Goal> getRequests() {
        return mRequests;
    }

    private ArrayList<GoalFeed> mFeeds;

    public ArrayList<GoalFeed> getFeeds() {
        return mFeeds;
    }

    public void setFeeds(ArrayList<GoalFeed> feeds) {
        mFeeds = feeds;
    }

    /// <summary>
    /// User information of device owner
    /// </summary>
    private User mOwnerProfile;

    public User getOwnerProfile() {
        return mOwnerProfile;
    }

    public boolean setOwnerProfile(User user) {
        if (addUser(user)) {
            PreferenceHelper.getInstance().setAccountUsername(user.username);
            mOwnerProfile = user;
            return true;
        }
        return false;
    }

    private static UserHelper mInstance;

    private UserHelper() {
    }

    public static synchronized UserHelper getInstance() {
        if (mInstance == null) {
            mInstance = new UserHelper();
        }

        return mInstance;
    }

    public void initialize() {
        mAllContacts = new TreeMap<>();
        mOwnerProfile = new User();
        mRequests = new ArrayList<>();
        mFeeds = new ArrayList<>();
    }

    /// <summary>
    /// Populate Friends and PendingFriends list from mAllContacts, and also download image if needed
    /// </summary>
    public void LoadContacts() {
        for (User value : mAllContacts.values()) {
            // TODO: load on demand vs all at once & load async
            if (ImageHelper.getInstance().isImageOnPrivateStorage(value.username, ImageType.PNG)) {
                value.profileBitmapImage = ImageHelper.getInstance().loadImageFromPrivateSorageSync(value.username, ImageType.PNG);
            }

            if (value.username.equals(mOwnerProfile.username))
                mOwnerProfile = value;
        }
    }

    /// <summary>
    /// Insert/replace user into database
    /// </summary>
    public boolean addUser(User user) {
        try {
            if (mAllContacts.containsKey(user.username)) {
                User oldUser = mAllContacts.get(user.username);
                oldUser.reputation = user.reputation;
                oldUser.bio = user.bio.length() > 0 ? user.bio : oldUser.bio;
                oldUser.profileBitmapImage = user.profileBitmapImage != null ? user.profileBitmapImage : oldUser.profileBitmapImage;
                oldUser.lastPhotoModifiedTime = user.lastPhotoModifiedTime > 0 ? user.lastPhotoModifiedTime : oldUser.lastPhotoModifiedTime;
                oldUser.save();
            } else {
                user.save();
                mAllContacts.put(user.username, user);
            }
            return true;
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.UserHelper, "Error adding user: " + ex.toString());
            return false;
        }
    }

    public boolean addGoal(Goal goal) {
        try {
            goal.save();
            if (!goal.createdByUsername.equals(mOwnerProfile.username)) {
                mRequests.add(goal);
            } else {
                if (goal.goalCompleteResult == Goal.GoalCompleteResult.Ongoing)
                    mOwnerProfile.addActivitGoal(goal);
                else
                    mOwnerProfile.addCompleteGoal(goal);
            }

            return true;
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.UserHelper, "Error adding goal: " + ex.toString());
            return false;
        }
    }

    public boolean deleteGoal(String guid) {
        try {
            SQLite.delete().from(Goal.class).where(Goal_Table.guid.eq(guid)).execute();

            for (int i = 0; i < mOwnerProfile.activieGoals.size(); i++) {
                Goal goal = mOwnerProfile.activieGoals.get(i);
                if (goal.guid.equals(guid)) {
                    mOwnerProfile.activieGoals.remove(i);
                    break;
                }
            }

            return true;
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.UserHelper, "Error adding goal: " + ex.toString());
            return false;
        }
    }

    public boolean modifyGoal(Goal newGoal) {
        try {
            newGoal.update();
            return true;
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.UserHelper, "Error adding goal: " + ex.toString());
            return false;
        }
    }

    public static boolean isUsernameValid(String username) {
        boolean isValid = (username != null && username.length() >= 4 && username.length() <= Constants.MAX_USERNAME_LENGTH) && !username.equals("admin");
        return isValid && !username.contains(":") && !username.contains(" ") && !username.contains("/") && !username.contains("\\");
    }
}
