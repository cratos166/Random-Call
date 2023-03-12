package com.nbird.call_random.UNIVERSAL.MODEL;

public class UpdateInfo {

    int CODE;
    String DIS,TITLE,LINKDATA;

    public UpdateInfo(int CODE, String DIS, String TITLE, String LINKDATA) {
        this.CODE = CODE;
        this.DIS = DIS;
        this.TITLE = TITLE;
        this.LINKDATA = LINKDATA;
    }

    public UpdateInfo() {
    }

    public int getCODE() {
        return CODE;
    }

    public void setCODE(int CODE) {
        this.CODE = CODE;
    }

    public String getDIS() {
        return DIS;
    }

    public void setDIS(String DIS) {
        this.DIS = DIS;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getLINKDATA() {
        return LINKDATA;
    }

    public void setLINKDATA(String LINKDATA) {
        this.LINKDATA = LINKDATA;
    }
}
