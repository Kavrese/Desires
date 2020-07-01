package com.example.yourdesires.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
@Table(database = MyDataBase.class)
public class LogData extends BaseModel {
    @PrimaryKey
    @Column
    int id_log;
    @Column
    String message;
    @Column
    String name_desires;
    @Column
    int id_desires_sum;
    @Column
    int id_desires;

    public String getName_desires() {
        return name_desires;
    }

    public void setName_desires(String name_desires) {
        this.name_desires = name_desires;
    }

    public int getId_desires() {
        return id_desires;
    }

    public int getId_desires_sum() {
        return id_desires_sum;
    }

    public int getId_log() {
        return id_log;
    }

    public String getMessage() {
        return message;
    }

    public void setId_desires(int id_desires) {
        this.id_desires = id_desires;
    }

    public void setId_desires_sum(int id_desires_sum) {
        this.id_desires_sum = id_desires_sum;
    }

    public void setId_log(int id_log) {
        this.id_log = id_log;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
