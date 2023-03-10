package com.nbird.call_random.MAIN.MODEL;

public class AgoraKeyModel {

    String player2,player1,appId,channelName,token;
    int accept;
    boolean callOver;

    //0->not answered
    //1->accept
    //2->reject



    public AgoraKeyModel() {
    }


    public AgoraKeyModel(String player2, String player1, String appId, String channelName, String token, int accept) {
        this.player2 = player2;
        this.player1 = player1;
        this.appId = appId;
        this.channelName = channelName;
        this.token = token;
        this.accept = accept;
        this.callOver = false;
    }

    public boolean isCallOver() {
        return callOver;
    }

    public void setCallOver(boolean callOver) {
        this.callOver = callOver;
    }

    public int getAccept() {
        return accept;
    }

    public void setAccept(int accept) {
        this.accept = accept;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
