package com.nbird.call_random.CALL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nbird.call_random.CALL.MODEL.AgoraAccount;
import com.nbird.call_random.CALL.MODEL.AgoraData;
import com.nbird.call_random.DATA.AppData;
import com.nbird.call_random.MAIN.CallRequestActivity;
import com.nbird.call_random.MAIN.MainActivity;
import com.nbird.call_random.R;
import com.nbird.call_random.REGISTRATION.MODEL.User;
import com.nbird.call_random.REGISTRATION.RegistrationActivity;
import com.nbird.call_random.UNIVERSAL.DIALOG.LoadingDialog;
import com.nbird.call_random.UNIVERSAL.UTILS.ConnectionStatus;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
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
    TextView text1,callTimer,name,status,mic_text,speaker_text;
    CardView speaker,mic,decline;
    CircleImageView propic;
    AppData appData;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    LoadingDialog loadingDialog;
    CountDownTimer countDownTimer;


    int minutes=30;
    int second=0;
    String minutestext;
    String secondtext;

    ImageView speakerImage,micImage;
    boolean isMicOn=true;


    String mainUID;

    ValueEventListener valueEventListener,callValueLisner;
    ConnectionStatus connectionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);


        appData=new AppData(CallActivity.this);
        loadingDialog=new LoadingDialog(CallActivity.this);
        loadingDialog.showLoadingDialog();

        appId=getIntent().getStringExtra("appId");
        channelName=getIntent().getStringExtra("channel");
        token=getIntent().getStringExtra("token");
        player1UID=getIntent().getStringExtra("player1");
        player2UID=getIntent().getStringExtra("player2");
        mainUID=getIntent().getStringExtra("mainUID");

        text1=(TextView) findViewById(R.id.text1);
        callTimer=(TextView) findViewById(R.id.callTimer);
        name=(TextView) findViewById(R.id.name);
        status=(TextView) findViewById(R.id.status);
        mic_text=(TextView) findViewById(R.id.mic_text);
        speaker_text=(TextView) findViewById(R.id.speaker_text);

        speaker=(CardView) findViewById(R.id.speaker);
        mic=(CardView) findViewById(R.id.mic);
        decline=(CardView) findViewById(R.id.decline);

        propic=(CircleImageView) findViewById(R.id.propic);

        speakerImage=(ImageView) findViewById(R.id.speakerImage);
        micImage=(ImageView) findViewById(R.id.micImage);


        if(appData.getMyName().equals(player1UID)){

            uiManu(player2UID);

        }else{
            uiManu(player1UID);
        }






        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRtcEngine.isSpeakerphoneEnabled()){
                    speakerImage.setBackgroundResource(R.drawable.speaker_off);
                    mRtcEngine.setEnableSpeakerphone(false);
                    speaker_text.setText("Speaker Off");
                }else{
                    speakerImage.setBackgroundResource(R.drawable.speaker_on);
                    mRtcEngine.setEnableSpeakerphone(true);
                    speaker_text.setText("Speaker On");
                }
            }
        });

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(isMicOn){
                    mRtcEngine.leaveChannel();
                    micImage.setBackgroundResource(R.drawable.mic_off);
                    isMicOn=false;
                    mic_text.setText("Mic Off");
                }else{
                    mRtcEngine.joinChannel(token, channelName, "", 0);
                    micImage.setBackgroundResource(R.drawable.mic_on);
                    isMicOn=true;
                    mic_text.setText("Mic On");
                }




            }
        });


        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              intentFun();
            }
        });


        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID)) {
            initializeAndJoinChannel();
        }

        callerTracker();



        connectionStatus=new ConnectionStatus(callValueLisner);
        connectionStatus.myCallConnecterStatus(mainUID);

    }


    private void callerTracker(){
        valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try{
                    Boolean callOver=snapshot.getValue(Boolean.class);
                    if(callOver){
                        Toast.makeText(CallActivity.this, "User disconnected the call.", Toast.LENGTH_LONG).show();
                        intentFun();
                    }
                }catch (Exception e){

                    Toast.makeText(CallActivity.this, "User disconnected the call", Toast.LENGTH_LONG).show();
                    intentFun();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myRef.child("AGORA_ROOM").child(mainUID).child("callOver").addValueEventListener(valueEventListener);

    }

    public void onBackPressed(){

    }

    private void intentFun(){
        try{countDownTimer.cancel();}catch (Exception e){}
        loadingDialog.showLoadingDialog();
        myRef.child("AGORA_ROOM").child(mainUID).child("callOver").removeEventListener(valueEventListener);
        connectionStatus.removeConnectionMyCall();


        try{mRtcEngine.leaveChannel();}catch (Exception e){}
        myRef.child("AGORA_ROOM").child(mainUID).removeValue();
        Intent intent=new Intent(CallActivity.this, MainActivity.class);
        startActivity(intent);

    }


    private void timerManu(){
        countDownTimer=new CountDownTimer(1000*30*60,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(second==0){
                    minutes--;

                    if(minutes<10){
                        minutestext="0"+String.valueOf(minutes);
                    }else{
                        minutestext=String.valueOf(minutes);
                    }
                    second=59;
                    if(second<10){
                        secondtext="0"+String.valueOf(second);
                    }else{
                        secondtext=String.valueOf(second);
                    }
                    callTimer.setText(minutestext+" Min :"+secondtext+" Sec");

                }else{
                    if(minutes<10){
                        minutestext="0"+String.valueOf(minutes);
                    }else{
                        minutestext=String.valueOf(minutes);
                    }
                    if(second<10){
                        secondtext="0"+String.valueOf(second);
                    }else{
                        secondtext=String.valueOf(second);
                    }
                    callTimer.setText(minutestext+" Min:"+secondtext+" Sec");
                    second--;
                }



            }

            @Override
            public void onFinish() {

            }
        }.start();

    }

    private void uiManu(String uid){
        myRef.child("USER").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(CallActivity.this).load(user.getImageURL()).into(propic);
                text1.setText("Your are speaking to "+user.getName());
                name.setText(user.getName()+" | "+user.getGender().toUpperCase());
                loadingDialog.dismissLoadingDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appId, mRtcEventHandler);
        } catch (Exception e) {
            status.setText("ERROR CONNECTING");
            throw new RuntimeException("Check the error");
        }
        mRtcEngine.joinChannel(token, channelName, "", 0);

        status.setText("SAFELY CONNECTED");
        timerManu();
    }
    protected void onDestroy() {
        super.onDestroy();
        mRtcEngine.leaveChannel();
        mRtcEngine.destroy();
        Runtime.getRuntime().gc();
    }
}