package test_util;

import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.GoalFeed;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.UserHelper;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class TestUtil {
    public static String getValidUsername() {
        return "device";
    }

    public static boolean isGoalEqual(Goal goalA, Goal goalB) {
        boolean isEqual;
        isEqual = goalA.guid.equals(goalB.guid);
        isEqual &= goalA.createdByUsername.equals(goalB.createdByUsername);
        isEqual &= goalA.title.equals(goalB.title);
        isEqual &= goalA.startDate == goalB.startDate;
        isEqual &= goalA.endDate == goalB.endDate;
        isEqual &= goalA.encouragement.equals(goalB.encouragement);
        isEqual &= goalA.goalCompleteResult == goalB.goalCompleteResult;
        isEqual &= goalA.wager == goalB.wager;
        isEqual &= goalA.referee.equals(goalB.referee);
        return isEqual;
    }

    public static boolean isUserEqual(User userA, User userB) {
        boolean isEqual;
        isEqual = userA.username.equals(userB.username);
        isEqual &= userA.bio.equals(userB.bio);
        isEqual &= userA.lastPhotoModifiedTime == userB.lastPhotoModifiedTime;
        isEqual &= userA.reputation == userB.reputation;
        isEqual &= ((userA.profileBitmapImage != null && userB.profileBitmapImage != null) || userA.profileBitmapImage == userB.profileBitmapImage);
        isEqual &= userA.activieGoals == userB.activieGoals || userA.activieGoals.size() == userB.activieGoals.size();
        isEqual &= userA.finishedGoals == userB.finishedGoals || userA.finishedGoals.size() == userB.finishedGoals.size();

        if (userA.activieGoals != null && userB.activieGoals != null) {
            for (int i = 0; i < userA.activieGoals.size(); i++) {
                isEqual &= isGoalEqual(userA.activieGoals.get(i), userB.activieGoals.get(i));
            }
        }
        if (userA.finishedGoals != null && userB.finishedGoals != null) {
            for (int i = 0; i < userA.finishedGoals.size(); i++) {
                isEqual &= isGoalEqual(userA.finishedGoals.get(i), userB.finishedGoals.get(i));
            }
        }
        return isEqual;
    }

    public static boolean isGoalFeedEqual(GoalFeed feedA, GoalFeed feedB) {
        boolean isEqual;
        isEqual = feedA.guid.equals(feedB.guid);
        isEqual &= feedA.createdUsername.equals(feedB.createdUsername);
        isEqual &= feedA.goalCompleteResult == feedB.goalCompleteResult;
        isEqual &= feedA.wager == feedB.wager;
        isEqual &= feedA.hasVoted == feedB.hasVoted;
        isEqual &= feedA.upvoteCount == feedB.upvoteCount;

        return isEqual;
    }

    public static void ReadDatabase() {
        try {
            List<User> users = SQLite.select().from(User.class).queryList();
            //populate contacts
            for (User user : users) {
                UserHelper.getInstance().getAllContacts().put(user.username, user);
            }

            //populate goals
            List<Goal> goals = SQLite.select().from(Goal.class).queryList();
            for (Goal goal : goals) {
                if (!goal.createdByUsername.equals(UserHelper.getInstance().getOwnerProfile().username)) {
                    UserHelper.getInstance().getRequests().add(goal);
                } else if (UserHelper.getInstance().getAllContacts().get(goal.createdByUsername) != null) {
                    if (goal.goalCompleteResult == Goal.GoalCompleteResult.Ongoing || goal.goalCompleteResult == Goal.GoalCompleteResult.Pending)
                        UserHelper.getInstance().getAllContacts().get(goal.createdByUsername).addActivitGoal(goal);
                    else
                        UserHelper.getInstance().getAllContacts().get(goal.createdByUsername).addCompleteGoal(goal);
                }
            }
        } catch (OutOfMemoryError outOfMemoryException) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.MainApplication, "Too many entries: " + outOfMemoryException.toString());
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.MainApplication, "Error loading database: " + ex.toString());
        }
    }
}