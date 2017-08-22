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
    @PrimaryKey
    @Column
    public String guid;

    @Column
    public long wager;

    @Column
    public Goal.GoalCompleteResult goalCompleteResult;

    @Column
    public String createdUsername;

    @Column
    public long upvoteCount;

    @Column
    public boolean hasVoted;

    public GoalFeed() {
        guid = "";
        wager = 0;
        goalCompleteResult = Goal.GoalCompleteResult.None;
        createdUsername = "";
        upvoteCount = 1;
        hasVoted = false;
    }

    public GoalFeed(String guid, long wager, String createdUsername, long upvoteCount, Goal.GoalCompleteResult goalCompleteResult) {
        this();
        this.guid = guid;
        this.wager = wager;
        this.createdUsername = createdUsername;
        this.upvoteCount = upvoteCount;
        this.goalCompleteResult = goalCompleteResult;
    }
}
