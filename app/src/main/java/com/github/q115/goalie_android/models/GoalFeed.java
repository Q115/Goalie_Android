package com.github.q115.goalie_android.models;

import com.github.q115.goalie_android.MainDB;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Qi on 8/11/2017.
 */

@Table(database = MainDB.class)
public class GoalFeed extends BaseModel {
    public enum GoalFeedCompleteResult {
        None, Success, Failed
    }

    @PrimaryKey
    @Column
    public String guid;

    @Column
    public String title;

    @Column
    public long endDate; //millisecond epoch since 1970

    @Column
    public long wager;

    @Column
    public String encouragement;

    @Column
    public GoalFeedCompleteResult goalCompleteResult;

    @Column
    public String fromUsername;

    @Column
    public long upvoteCount;

    public GoalFeed() {
        guid = "";
        title = "";
        endDate = 0;
        wager = 0;
        encouragement = "";
        goalCompleteResult = GoalFeedCompleteResult.None;
        fromUsername = "";
        upvoteCount = 0;
    }

    public GoalFeed(String guid, String title, long endDate, long wager, String encouragement, long upvoteCount, GoalFeedCompleteResult goalCompleteResult, String referee) {
        this();
        this.guid = guid;
        this.title = title;
        this.endDate = endDate;
        this.wager = wager;
        this.encouragement = encouragement;
        this.upvoteCount = upvoteCount;
        this.goalCompleteResult = goalCompleteResult;
        this.fromUsername = referee;
    }
}
