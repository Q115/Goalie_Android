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
