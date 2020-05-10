package com.example.yourdesires;

public class Desires {
    private String name,tag1,tag2,data,op;
    private int status;
    public Desires (String name, int status ,String tag1, String tag2,String data,String op){
        this.name = name;
        this.status = status;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.data = data;
        this.op = op;
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
