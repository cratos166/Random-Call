package com.nbird.call_random.MAIN.MODEL;

public class OnlineModel {

    String uid;
    int status;

    public OnlineModel() {
    }

    public OnlineModel(String uid, int status) {
        this.uid = uid;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
