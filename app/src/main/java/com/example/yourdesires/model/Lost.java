package com.example.yourdesires.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.sql.Blob;

@Table(database = MyDataBase.class)
public class Lost extends BaseModel {

    @Column
    @PrimaryKey
    int id;
    @Column
    String desires;

    public int getId(){return id;}

    public void setId(int id) {this.id = id;}

    public String getDesires() {
        return desires;}

    public void setDesires(String desires) { this.desires = desires;}
}
