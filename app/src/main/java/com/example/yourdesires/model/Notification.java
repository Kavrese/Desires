package com.example.yourdesires.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
@Table(database = MyDataBase.class)
public class Notification extends BaseModel {
    @Column
    int id_desires;
    @PrimaryKey
    @Column
    int id_notification;
    @Column
    String date_work;
    @Column
    String date_created;
    @Column
    String type;
    @Column
    int status_edit;
    @Column
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus_edit() {
        return status_edit;
    }

    public void setStatus_edit(int status_edit) {
        this.status_edit = status_edit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId_desires() {
        return id_desires;
    }

    public int getId_notification() {
        return id_notification;
    }

    public String getDate_created() {
        return date_created;
    }

    public String getDate_work() {
        return date_work;
    }

    public void setId_desires(int id_desires) {
        this.id_desires = id_desires;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public void setDate_work(String date_work) {
        this.date_work = date_work;
    }

    public void setId_notification(int id_notification) {
        this.id_notification = id_notification;
    }
}
