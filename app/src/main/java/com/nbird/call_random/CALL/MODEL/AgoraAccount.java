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



        //vironikapa141@gmail.com
        agoraDataArrayList.add(new AgoraData("c2afa16c2589482c9ba2d5c511eb60e1", "f5d5f71d00fa4d77af3fbb8d155d2fa2"));

        //modi49214@gmail.com
        agoraDataArrayList.add(new AgoraData("28f4daf17681473caa58d7eb67f0c5ab", "4696e6263b584ba2b71a99b97981478d"));


        //mohammedqasim245@gmail.com
        agoraDataArrayList.add(new AgoraData("9321140355e64ed6b264e1bddc3fc9cb", "03e5a555fecb41ea9d05986b7278f055"));


        //pritesh.patel9615@gmail.com
        agoraDataArrayList.add(new AgoraData("dd1a7933acda40ad821cafa591c2d4e1", "4a898def06f343af88c661ba5231e012"));


        //mokesh.patel9615@gmail.com
        agoraDataArrayList.add(new AgoraData("4c1851a3c2694c51aa18b43e055f0670", "c319cf7b09454a12a76ffd6a60958c12"));

        //nitesh.patel9615@gmail.com
        agoraDataArrayList.add(new AgoraData("12a988c2898a4d689897df7c39baad7a", "c158fdcaf61445648d51a14a0eb15bcf"));


        //mitesh.patel9615@gmail.com
        agoraDataArrayList.add(new AgoraData("faee72b322804b0995dba84865b49553", "be72b1af0ea74bc28e5876e7dc5a45c5"));


        //ratesh.patel9615@gmail.com
        agoraDataArrayList.add(new AgoraData("eb50ead076144a81bfc5af1000d346e1", "03afa0500a8147d49e636d1b3e518860"));

        //sachinkartikwar@gmail.com
        agoraDataArrayList.add(new AgoraData("2aaea573211540a698cd837d2ebc216f", "a28fcb443ccd4d6cb9381bc730523175"));

        //niftynileads@gmai.com
        agoraDataArrayList.add(new AgoraData("47efc510a43a41f5904dd66b20a3824c", "60043a1e4ef04e64926728053419a365"));

        //kartiksachu166@gmail.com
        agoraDataArrayList.add(new AgoraData("d510cdbb70154430a4e324811810fc2b", "d2f7aa7ac571431d8569539b9dc639bc"));

        //kajalsingh051995@gmail.com
        agoraDataArrayList.add(new AgoraData("1e2e63f9f3c1431b98786bb327cc707c", "edc0b52cf8304d3486b587436a9d57f4"));

        //zebramafia13@gmail.com
        //agoraDataArrayList.add(new AgoraData("2f97af0d22e431ab993c763c6ade4ba", "e2d59b01fe8049d4b5b460e3c9e1dab1"));

        //zebramafia6@gmail.com
        agoraDataArrayList.add(new AgoraData("e629bd333277405182f075151fca4d86", "3b34012b1fe440508625f6968bbfc872"));

        //manikantsharma126@gmail.com
        agoraDataArrayList.add(new AgoraData("cbda3405187147fdb7330788a69a8cb5", "2b9eaed3eb65446291bfa586484f9274"));


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
        int timeStamp = (int) (date.getTime() / 1000 + 2000);
        accessToken = tokenBuilder.buildTokenWithUserAccount(agoraData.getAppId(), agoraData.getAppCertificate(), channelName, "", RtcTokenBuilder.Role.Role_Publisher, timeStamp);
        return accessToken;
    }
}
