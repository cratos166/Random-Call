package com.nbird.call_random.CALL.MODEL;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import agora.media.RtcTokenBuilder;


public class AgoraAccount {

    ArrayList<AgoraData> agoraDataArrayList = new ArrayList<>();
    public AgoraAccount() {

    }

    public AgoraData getRandomAgoraAcc() {
        //zimmy changela
        agoraDataArrayList.add(new AgoraData("f943ae0145c94688ad1a8ca76828751b", "acc8aa4a75ed4b168d2c61e4905c7ee9"));

        //Nifty Nile
        agoraDataArrayList.add(new AgoraData("82093c7e34d0487c8c50c3cf2c3f4c9e", "002cdfd0f9e6445d84637d968be8b3f8"));

        //sachinkartik166
        agoraDataArrayList.add(new AgoraData("2a5a6192f24745408472ca2b7cd95730", "9116d74b63794980a0faf58bcd2182fe"));



        int size = agoraDataArrayList.size();

        Random random=new Random();

        int kk=random.nextInt(size);

        Log.v(AgoraAccount.class.getSimpleName(), "app id here " + agoraDataArrayList.get(kk).getAppId());
        return agoraDataArrayList.get(kk);

    }


    public  String generateToken(AgoraData agoraData, String channelName) {
        String accessToken;
        RtcTokenBuilder tokenBuilder = new RtcTokenBuilder();
        Date date = new Date();
        int timeStamp = (int) (date.getTime() / 1000 + 1800);
        accessToken = tokenBuilder.buildTokenWithUserAccount(agoraData.getAppId(), agoraData.getAppCertificate(), channelName, "", RtcTokenBuilder.Role.Role_Publisher, timeStamp);
        return accessToken;
    }
}
