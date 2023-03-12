package com.nbird.call_random.UNIVERSAL.UTILS;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class NotificationIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NotificationIntentService() {
        super("notificationIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        switch (intent.getAction()) {
            case "left":
                Handler leftHandler = new Handler(Looper.getMainLooper());
                leftHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setComponent(new ComponentName("com.nbird.call_random", "com.nbird.call_random.MAIN.MainActivity"));
//                            intent.putExtra("player1",agoraKeyModel.getPlayer1());
//                            intent.putExtra("player2",agoraKeyModel.getPlayer2());
//                            intent.putExtra("appId",agoraKeyModel.getAppId());
//                            intent.putExtra("token",agoraKeyModel.getToken());
//                            intent.putExtra("channel",agoraKeyModel.getChannelName());
//                            intent.putExtra("mainUID",myUID);
                            startActivity(intent);
                    }
                });
                break;
            case "right":
                Handler rightHandler = new Handler(Looper.getMainLooper());
                rightHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setComponent(new ComponentName("com.nbird.call_random", "com.nbird.call_random.MAIN.MainActivity"));
//                            intent.putExtra("player1",agoraKeyModel.getPlayer1());
//                            intent.putExtra("player2",agoraKeyModel.getPlayer2());
//                            intent.putExtra("appId",agoraKeyModel.getAppId());
//                            intent.putExtra("token",agoraKeyModel.getToken());
//                            intent.putExtra("channel",agoraKeyModel.getChannelName());
//                            intent.putExtra("mainUID",myUID);
                        startActivity(intent);
                    }
                });
                break;
        }
    }
}