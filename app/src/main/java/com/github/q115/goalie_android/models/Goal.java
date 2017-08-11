package com.github.q115.goalie_android.models;

import com.github.q115.goalie_android.MainDB;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = MainDB.class)
public class Goal extends BaseModel {
    public enum GoalCompleteResult {
        None, Ongoing, Success, Failed, Cancelled
    }

    @PrimaryKey
    @Column
    public String guid;

    @Column
    public String title;

    @Column
    public long startDate;

    @Column
    public long endDate; //millisecond epoch since 1970

    @Column
    public long wager;

    @Column
    public String encouragement;

    @Column
    public GoalCompleteResult goalCompleteResult;

    @Column
    public String referee;

    public Goal() {
        guid = "";
        title = "";
        startDate = 0;
        endDate = 0;
        wager = 0;
        encouragement = "";
        goalCompleteResult = GoalCompleteResult.None;
        referee = "";
    }

    public Goal(String guid, String title, long startDate, long endDate, long wager, String encouragement, GoalCompleteResult goalCompleteResult, String referee) {
        this();
        this.guid = guid;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.wager = wager;
        this.encouragement = encouragement;
        this.goalCompleteResult = goalCompleteResult;
        this.referee = referee;
    }
}
