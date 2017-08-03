package com.github.q115.goalie_android.models;

import com.github.q115.goalie_android.MainDB;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = MainDB.class)
public class Goal extends BaseModel {
    @PrimaryKey
    @Column
    public String guid;

    @Column
    public String title;

    @Column
    public long deadline; //millisecond epoch since 1970

    @Column
    public long wager;

    public Goal() {
        guid = "";
        title = "";
        deadline = 0;
        wager = 0;
    }

    public Goal(String guid, String title, long deadline, long wager) {
        this();
        this.guid = guid;
        this.title = title;
        this.deadline = deadline;
        this.wager = wager;
    }
}
