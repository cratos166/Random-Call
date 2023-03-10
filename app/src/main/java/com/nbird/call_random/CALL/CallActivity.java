package com.nbird.call_random.CALL;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.nbird.call_random.CALL.MODEL.AgoraAccount;
import com.nbird.call_random.CALL.MODEL.AgoraData;
import com.nbird.call_random.R;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;

import io.agora.rtc.RtcEngine;
import io.agora.rtc.IRtcEngineEventHandler;


public class CallActivity extends AppCompatActivity {

    private static final int PERMISSION_REQ_ID = 22;

    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }

    private String appId;
    // Fill the channel name.
    private String channelName;
    // Fill the temp token generated on Agora Console.
    private String token;
    private RtcEngine mRtcEngine;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
    };

    AgoraData data;
    String player1UID,player2UID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);


        appId=getIntent().getStringExtra("appId");
        channelName=getIntent().getStringExtra("channel");
        token=getIntent().getStringExtra("token");
        player1UID=getIntent().getStringExtra("player1");
        player2UID=getIntent().getStringExtra("player2");



        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID)) {
            initializeAndJoinChannel();
        }

    }


    private void initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appId, mRtcEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("Check the error");
        }
        mRtcEngine.joinChannel(token, channelName, "", 0);
    }
    protected void onDestroy() {
        super.onDestroy();
        mRtcEngine.leaveChannel();
        mRtcEngine.destroy();
    }
}