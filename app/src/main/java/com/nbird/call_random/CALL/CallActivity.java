package com.nbird.call_random.CALL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nbird.call_random.CALL.MODEL.AgoraData;
import com.nbird.call_random.DATA.AppData;
import com.nbird.call_random.DATA.Constant;
import com.nbird.call_random.MAIN.MainActivity;
import com.nbird.call_random.R;
import com.nbird.call_random.REGISTRATION.MODEL.User;
import com.nbird.call_random.UNIVERSAL.DIALOG.LoadingDialog;
import com.nbird.call_random.UNIVERSAL.UTILS.ConnectionStatus;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.util.Log;
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



    private InterstitialAd mInterstitialAd;
    private void loadAds(){


        String key= Constant.INTERSTITIAL_ID;

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, key, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d("TAG", loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });


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
    TextView text1,callTimer,name,status,mic_text,speaker_text,balanceTextView;
    CardView speaker,mic,decline;
    CircleImageView propic;
    AppData appData;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    LoadingDialog loadingDialog;
    CountDownTimer countDownTimer,balanceCutterTimer;


    int minutes=30;
    int second=0;
    String minutestext;
    String secondtext;

    ImageView speakerImage,micImage;
    boolean isMicOn=true;


    String mainUID;

    ValueEventListener valueEventListener,callValueLisner;
    ConnectionStatus connectionStatus;
    AdView mAdView1,mAdView2;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        loadAds();

        mAdView1 = findViewById(R.id.adView1);
        mAdView1.setVisibility(View.VISIBLE);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest1);

        mAdView2 = findViewById(R.id.adView2);
        mAdView2.setVisibility(View.VISIBLE);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest2);






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
        balanceTextView=(TextView) findViewById(R.id.balanceTextView);

        speaker=(CardView) findViewById(R.id.speaker);
        mic=(CardView) findViewById(R.id.mic);
        decline=(CardView) findViewById(R.id.decline);

        propic=(CircleImageView) findViewById(R.id.propic);

        speakerImage=(ImageView) findViewById(R.id.speakerImage);
        micImage=(ImageView) findViewById(R.id.micImage);


        if(appData.getMyUID().equals(player1UID)){

            uiManu(player2UID);

        }else{
            uiManu(player1UID);
        }


        balanceTextView.setText(String.valueOf(appData.getMyBalance()));


        balanceCutterTimer=new CountDownTimer(1000*30*60,1000*60) {
            @Override
            public void onTick(long millisUntilFinished) {



                int b=appData.getMyBalance();
                b--;
                appData.setMyBalance(b);
                balanceTextView.setText(String.valueOf(appData.getMyBalance()));
                myRef.child("USER").child(appData.getMyUID()).child("balance").setValue(appData.getMyBalance()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(appData.getMyBalance()==0){
                            intentFun(1);
                        }


                    }
                });


                try{mAdView1.destroy();}catch (Exception e){}

                try{mAdView2.destroy();}catch (Exception e){}



                AdRequest adRequest1 = new AdRequest.Builder().build();
                mAdView1.loadAd(adRequest1);

                AdRequest adRequest2 = new AdRequest.Builder().build();
                mAdView2.loadAd(adRequest2);


            }

            @Override
            public void onFinish() {

            }
        }.start();



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
                adShow();
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
                        adShow();
                    }
                }catch (Exception e){
                    Toast.makeText(CallActivity.this, "User disconnected the call.", Toast.LENGTH_LONG).show();
                    adShow();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myRef.child("AGORA_ROOM").child(mainUID).child("callOver").addValueEventListener(valueEventListener);

    }

    public void onBackPressed(){

            adShow();

    }


    public void adShow(){
        intentFun(0);
    }

    private void intentFun(int i){





        try{balanceCutterTimer.cancel();}catch (Exception e){}
        try{countDownTimer.cancel();}catch (Exception e){}
        loadingDialog.showLoadingDialog();
        myRef.child("AGORA_ROOM").child(mainUID).child("callOver").removeEventListener(valueEventListener);
        connectionStatus.removeConnectionMyCall();
        try{mRtcEngine.leaveChannel();}catch (Exception e){}
        myRef.child("AGORA_ROOM").child(mainUID).removeValue();


        if(mInterstitialAd!=null) {
            // Step 1: Display the interstitial
            mInterstitialAd.show(CallActivity.this);
            // Step 2: Attach an AdListener
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Intent intent=new Intent(CallActivity.this, MainActivity.class);
                    if(i==1){
                        intent.putExtra("BALANCE_ZERO",1);
                    }
                    startActivity(intent);
                    finish();

                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    Intent intent=new Intent(CallActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            });


        }else{
            Intent intent=new Intent(CallActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }




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

                    if(second<10){
                        secondtext="0"+String.valueOf(second);
                    }else{
                        secondtext=String.valueOf(second);
                    }
                    callTimer.setText(minutestext+" Min :"+secondtext+" Sec");
                    second=59;
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
                Toast.makeText(CallActivity.this, "Call time over", Toast.LENGTH_LONG).show();
                adShow();


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

        try{
            mRtcEngine.leaveChannel();
        }catch (Exception e){

        }
        try{
            mRtcEngine.destroy();
        }catch (Exception e){

        }

        try{mAdView1.destroy();}catch (Exception e){}

        try{mAdView2.destroy();}catch (Exception e){}


        Runtime.getRuntime().gc();
    }




}