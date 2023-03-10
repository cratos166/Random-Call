package com.nbird.call_random.MAIN.MODEL;

public class AgoraKeyModel {

    String player2,player1,roomKey;

    public AgoraKeyModel() {
    }

    public AgoraKeyModel(String myUID, String player1, String roomKey) {
        this.player2 = myUID;
        this.player1 = player1;
        this.roomKey = roomKey;
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

    public String getRoomKey() {
        return roomKey;
    }

    public void setRoomKey(String roomKey) {
        this.roomKey = roomKey;
    }
}
