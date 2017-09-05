package com.github.q115.goalie_android.models;

import com.github.q115.goalie_android.MainDB;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

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
@Table(database = MainDB.class)
public class Goal extends BaseModel {
    public enum GoalCompleteResult {
        None, Pending, Ongoing, Success, Failed, Cancelled
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

    @Column
    public String createdByUsername;

    @Column
    public long activityDate;

    public Goal() {
        guid = "";
        title = "";
        startDate = 0;
        endDate = 0;
        wager = 0;
        encouragement = "";
        goalCompleteResult = GoalCompleteResult.None;
        referee = "";
        createdByUsername = "";
        activityDate = System.currentTimeMillis();
    }

    public Goal(String guid, GoalCompleteResult goalCompleteResult) {
        this();
        this.guid = guid;
        this.goalCompleteResult = goalCompleteResult;
    }

    public Goal(String guid, String createdByUsername, String title, long startDate, long endDate, long wager,
                String encouragement, GoalCompleteResult goalCompleteResult, String referee, long activityDate) {
        this();
        this.guid = guid;
        this.createdByUsername = createdByUsername;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.wager = wager;
        this.encouragement = encouragement;
        this.goalCompleteResult = goalCompleteResult;
        this.referee = referee;
        this.activityDate = activityDate;
    }
}
