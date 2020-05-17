package com.example.yourdesires;

import java.util.ArrayList;

public class Desires {
    private String name,tag1,tag2,data,op;
    private int status;
    private boolean light,search;
    ArrayList<Integer> position;
    public Desires (String name, int status , String tag1, String tag2, String data, String op, boolean light, boolean search, ArrayList<Integer> position){
        this.name = name;
        this.status = status;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.data = data;
        this.op = op;
        this.light = light;
        this.search = search;
        this.position = position;
    }
    public boolean getLight (){
        return light;
    }

    public void setLight(boolean light) {
        this.light = light;
    }

    public ArrayList<Integer> getPosition() {
        return position;
    }

    public void setPosition(ArrayList<Integer> position) {
        this.position = position;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }

    public boolean getSearch (){
        return this.search;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public String getTag1() {
        return tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public String getData() {
        return data;
    }

    public String getOp() {
        return op;
    }
}
