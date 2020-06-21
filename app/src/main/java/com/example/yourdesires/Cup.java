package com.example.yourdesires;

public class Cup {
    private boolean cup_img;
    private boolean cup_audio;
    private boolean cup_bell;
    public Cup (boolean cup_img,boolean cup_audio,boolean cup_bell){

    }

    public boolean getCup_img() {
        return cup_img;
    }

    public boolean getCup_audio() {
        return cup_audio;
    }

    public boolean getCup_bell() {
        return cup_bell;
    }

    public void setCup_audio(boolean cup_audio) {
        this.cup_audio = cup_audio;
    }

    public void setCup_bell(boolean cup_bell) {
        this.cup_bell = cup_bell;
    }

    public void setCup_img(boolean cup_img) {
        this.cup_img = cup_img;
    }
}
