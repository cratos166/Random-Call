package com.nbird.call_random.UNIVERSAL.UTILS;

import android.app.ActivityManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;


import com.nbird.call_random.R;

import java.util.ArrayList;
import java.util.Random;

public class SongSetting {
    Context context;
    MediaPlayer mediaPlayer;
    Boolean isInBackground;
    CountDownTimer countDownTimer;

    public SongSetting(Context context) {
        this.context = context;
    }


    public void songStop(){
        try{
            mediaPlayer.pause();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
        }catch (Exception e){

        }

        try{
            countDownTimer.cancel();
        }catch (Exception e){

        }

    }



    private void mainMusic(){
        mediaPlayer= MediaPlayer.create(context, R.raw.ring);
        mediaPlayer.start();
        mediaPlayer.setVolume(0.8f,0.8f);
    }

    public void startMusic(){
        mainMusic();
        countDownTimer=new CountDownTimer(1000 * 60 * 24 * 30, 1000) {
            @Override
            public void onTick(long l) {

                try{
                    ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
                    ActivityManager.getMyMemoryState(myProcess);
                    isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                    if (isInBackground) {
                        mediaPlayer.pause();
                    } else {
                        try{
                            if (!mediaPlayer.isPlaying()) {
                                mediaPlayer.start();
                                mediaPlayer.setVolume(0.4f,0.4f);
                            }
                        }catch (Exception e){

                        }

                    }
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {


                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.stop();
                                    try {
                                        mediaPlayer.reset();
                                    }catch (Exception e){

                                    }
                                    mediaPlayer.release();

                                } else {
                                    mediaPlayer.reset();
                                    mediaPlayer.release();
                                }

                                mainMusic();

                        }
                    });
                }catch (Exception e){

                }


            }

            @Override
            public void onFinish() {

            }
        }.start();

    }





}
