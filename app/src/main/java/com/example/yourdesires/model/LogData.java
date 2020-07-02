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
    int num_edit;

    public int getNum_edit() {
        return num_edit;
    }

    public void setNum_edit(int num_edit) {
        this.num_edit = num_edit;
    }

    public String getName_desires() {
        return name_desires;
    }

    public void setName_desires(String name_desires) {
        this.name_desires = name_desires;
    }

    public int getId_log() {
        return id_log;
    }

    public String getMessage() {
        return message;
    }

    public void setId_log(int id_log) {
        this.id_log = id_log;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
