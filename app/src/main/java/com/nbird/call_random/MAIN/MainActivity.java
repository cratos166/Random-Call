package com.nbird.call_random.MAIN;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;
import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static android.app.Service.START_NOT_STICKY;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nbird.call_random.CALL.CallActivity;
import com.nbird.call_random.CALL.MODEL.AgoraAccount;
import com.nbird.call_random.CALL.MODEL.AgoraData;
import com.nbird.call_random.DATA.AppData;
import com.nbird.call_random.DATA.Constant;
import com.nbird.call_random.MAIN.MODEL.AgoraKeyModel;
import com.nbird.call_random.MAIN.MODEL.OnlineModel;
import com.nbird.call_random.R;
import com.nbird.call_random.REGISTRATION.MODEL.User;
import com.nbird.call_random.REGISTRATION.RegistrationActivity;
import com.nbird.call_random.UNIVERSAL.DIALOG.LoadingDialog;
import com.nbird.call_random.UNIVERSAL.DIALOG.LowBalanceDialog;
import com.nbird.call_random.UNIVERSAL.MODEL.UpdateInfo;
import com.nbird.call_random.UNIVERSAL.UTILS.ConnectionStatus;
import com.nbird.call_random.UNIVERSAL.UTILS.NotificationIntentService;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    Switch onlineSwitch;
    RadioGroup radioGroup;
    TextView onlineStatus;
    Button beginner,intermediate,advance,save;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    AppData appData;

    String levelStr,genderStr;
    RadioButton anyGender,male,female;

    ValueEventListener valueEventListener,connectionEventLisner;

    String myName,myUID,myImage,myGender;
    ConnectionStatus connectionStatus;

    LottieAnimationView profile;

    String previousUID;

    boolean userGot=false;

    public static final int APP_VERSION=1;

    LoadingDialog loadingDialog;
    NativeAd NATIVE_ADS,NATIVE_ADS1,NATIVE_ADS2;

    MediaPlayer mediaPlayer;
    CountDownTimer countDownTimer;

    int balance;
    TextView balanceTextView;
    RewardedAd rewardedAd;
    Button rewardAdButton,razorPayButton;


    int indicator;

    String pp2="";


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID)) {

        }else{
            Toast.makeText(this, "Please grant the permissions for normal functioning of the app.", Toast.LENGTH_LONG).show();
        }

        rewardAdsLoader();

        appData=new AppData(MainActivity.this);


        balanceTextView=(TextView) findViewById(R.id.balanceTextView);


        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        balance=appData.getMyBalance();

        if(balance<40){


            if(!formattedDate.equals(appData.getDate())){
                appData.setMyBalance(40);

                myRef.child("USER").child(appData.getMyUID()).child("balance").setValue(40).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                balanceTextView.setText(String.valueOf(40));

                appData.setDate(formattedDate);

            }



        }else{
            appData.setDate(formattedDate);
            balanceTextView.setText(String.valueOf(appData.getMyBalance()));
        }













        MobileAds.initialize(MainActivity.this);
        AdLoader adLoader = new AdLoader.Builder(MainActivity.this, Constant.NATIVE_ID)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        ColorDrawable cd = new ColorDrawable(0x393F4E);

                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(cd).build();
                        TemplateView template = findViewById(R.id.my_template);
                        template.setStyles(styles);
                        template.setNativeAd(nativeAd);
                        template.setVisibility(View.VISIBLE);
                        NATIVE_ADS=nativeAd;
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());


        previousUID=getIntent().getStringExtra("previousUID");

        loadingDialog=new LoadingDialog(MainActivity.this);


        int BALANCE_ZERO=getIntent().getIntExtra("BALANCE_ZERO",0);

        if(BALANCE_ZERO==1){
            LowBalanceDialog lowBalanceDialog=new LowBalanceDialog(MainActivity.this,balanceTextView,balanceTextView);
            lowBalanceDialog.start();

        }
//        //TODO REMOVE
//        LowBalanceDialog lowBalanceDialog=new LowBalanceDialog(MainActivity.this,balanceTextView,balanceTextView);
//        lowBalanceDialog.start();


        onlineSwitch=(Switch) findViewById(R.id.onlineSwitch);
        onlineStatus=(TextView) findViewById(R.id.onlineStatus);
        beginner=(Button) findViewById(R.id.beginner);
        intermediate=(Button) findViewById(R.id.intermediate);
        advance=(Button) findViewById(R.id.advance);
        radioGroup=(RadioGroup) findViewById(R.id.radioGroup);
        save=(Button) findViewById(R.id.save);
        anyGender=(RadioButton) findViewById(R.id.anyGender);
        male=(RadioButton) findViewById(R.id.male);
        female=(RadioButton) findViewById(R.id.female);
        profile=(LottieAnimationView) findViewById(R.id.profile);
        rewardAdButton=(Button) findViewById(R.id.rewardAdButton);
        razorPayButton=(Button) findViewById(R.id.razorPayButton);




        setLayoutUI();


        try{
            noInternet();
        }catch (Exception e){

        }


        myName=appData.getMyName();
        myUID=appData.getMyUID();
        myImage=appData.getMyImage();
        myGender=appData.getMyGender();

        connectionStatus=new ConnectionStatus(myUID,connectionEventLisner);


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    connectionStatus.removeListner();
                }catch (Exception e){

                }
                try{
                    myRef.child("AGORA_ROOM").child(myUID).removeEventListener(valueEventListener);
                }catch (Exception e){

                }


                myRef.child("ONLINE").child(myUID).removeValue();



                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                intent.putExtra("isSetting",true);
                startActivity(intent);

                finish();

            }
        });


        beginner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appData.setLevel("BEGINNER");
                setLayoutUI();
            }
        });

        intermediate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appData.setLevel("INTERMEDIATE");
                setLayoutUI();
            }
        });

        advance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appData.setLevel("ADVANCE");
                setLayoutUI();
            }
        });


        anyGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appData.setGenderPref("ANY");
                setLayoutUI();
            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appData.setGenderPref("MALE");
                setLayoutUI();
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appData.setGenderPref("FEMALE");
                setLayoutUI();
            }
        });
        loadingDialog.showLoadingDialog();



                        myRef.child("UTILS").child("UpdateInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try{
                                    UpdateInfo updateInfo=snapshot.getValue(UpdateInfo.class);

                                    loadingDialog.dismissLoadingDialog();
                                    if(updateInfo.getCODE()==APP_VERSION){
                                        onlineSetter();
                                    }else{
                                        updateDialog(updateInfo.getDIS(), updateInfo.getTITLE(), updateInfo.getLINKDATA());
                                    }



                                }catch (Exception e){

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });





        onlineSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(onlineSwitch.isChecked()){
                    onlineSetter();
                    onlineStatus.setText("Online");
                    onlineStatus.setTextColor(Color.parseColor("#48D96C"));
                }else{
                    onlineStatus.setText("Offline");
                    myRef.child("ONLINE").child(myUID).removeValue();
                    connectionStatus.removeListner();
                    myRef.child("AGORA_ROOM").child(myUID).removeEventListener(valueEventListener);
                    onlineStatus.setTextColor(Color.parseColor("#BFD1FF"));
                }

            }
        });



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                myRef.child("ONLINE").child(myUID).removeValue();

                try{
                    connectionStatus.removeListner();
                }catch (Exception e){

                }

                try{
                    myRef.child("AGORA_ROOM").child(myUID).removeEventListener(valueEventListener);
                }catch (Exception e){

                }

                Intent intent=new Intent(MainActivity.this,CallNowActivity.class);
                intent.putExtra("previousUID",previousUID);
                startActivity(intent);
                finish();


            }
        });



        rewardAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rewardedAd != null) {

                    rewardedAd.show(MainActivity.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.

                            Toast.makeText(MainActivity.this, "You earned "+Constant.REWARD_AD_MONEY+" coins", Toast.LENGTH_SHORT).show();

                            appData.setMyBalance(appData.getMyBalance()+Constant.REWARD_AD_MONEY);

                            myRef.child("USER").child(appData.getMyUID()).child("balance").setValue(appData.getMyBalance()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                }
                            });


                            balanceTextView.setText(String.valueOf(appData.getMyBalance()));

                            rewardAdsLoader();



                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Ad failed to show fullscreen content", Toast.LENGTH_SHORT).show();
                }
            }
        });



        razorPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packageDialog();
            }
        });


    }



    private void updateDialog(String dis,String title,String linkdata){


            AlertDialog.Builder builderRemove=new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
            View viewRemove1= LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_model_1,(ConstraintLayout) findViewById(R.id.layoutDialogContainer),false);
            builderRemove.setView(viewRemove1);
            builderRemove.setCancelable(false);
            Button button=(Button) viewRemove1.findViewById(R.id.button);

            TextView textTitle=(TextView) viewRemove1.findViewById(R.id.textTitle);
            textTitle.setText(title);


            TextView textDis=(TextView) viewRemove1.findViewById(R.id.textDis);
            textDis.setText(dis);

            LottieAnimationView anim=(LottieAnimationView)  viewRemove1.findViewById(R.id.anim);
            anim.setAnimation(R.raw.updateanim);
            anim.playAnimation();
            anim.loop(true);



            button.setText("UPDATE");

        MobileAds.initialize(MainActivity.this);
        AdLoader adLoader = new AdLoader.Builder(MainActivity.this, Constant.NATIVE_ID)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        ColorDrawable cd = new ColorDrawable(0x393F4E);

                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(cd).build();
                        TemplateView template = viewRemove1.findViewById(R.id.my_template);
                        template.setStyles(styles);
                        template.setNativeAd(nativeAd);
                        template.setVisibility(View.VISIBLE);
                        NATIVE_ADS1=nativeAd;
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());



            final AlertDialog alertDialog=builderRemove.create();
            if(alertDialog.getWindow()!=null){
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            try{
                alertDialog.show();
            }catch (Exception e){

            }



            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try{
                        Intent browserIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(linkdata));
                        startActivity(browserIntent);
                    }catch (Exception e){

                    }
                }
            });
        }





    private void onlineSetter(){



        OnlineModel onlineModel=new OnlineModel(myUID,1);
        myRef.child("ONLINE").child(myUID).setValue(onlineModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                connectionStatus.myStatusSetter();


                valueEventListener=new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {

                            AgoraKeyModel agoraKeyModel = snapshot.getValue(AgoraKeyModel.class);


                            if (!pp2.equals(agoraKeyModel.getPlayer2())) {



                            Log.i("myuid", agoraKeyModel.getPlayer1());
                            Log.i("oppouid", agoraKeyModel.getPlayer2());
                            Log.i("appId", agoraKeyModel.getAppId());
                            Log.i("token", agoraKeyModel.getToken());
                            Log.i("channel", agoraKeyModel.getChannelName());


                            connectionStatus.removeListner();
                            myRef.child("AGORA_ROOM").child(myUID).removeEventListener(valueEventListener);
//                                Intent intent=new Intent(MainActivity.this,CallRequestActivity.class);


                            pp2 = agoraKeyModel.getPlayer2();


                            //   finalNotification("Incoming Call","Bob is calling you");


                            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.P) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setComponent(new ComponentName("com.nbird.call_random", "com.nbird.call_random.MAIN.CallRequestActivity"));
                                intent.putExtra("player1", agoraKeyModel.getPlayer1());
                                intent.putExtra("player2", agoraKeyModel.getPlayer2());
                                intent.putExtra("appId", agoraKeyModel.getAppId());
                                intent.putExtra("token", agoraKeyModel.getToken());
                                intent.putExtra("channel", agoraKeyModel.getChannelName());
                                intent.putExtra("mainUID", myUID);
                                //  intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);


                                //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // You need this if starting
//                            //  the activity from a service
//                            intent.setAction(Intent.ACTION_MAIN);
//                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                finish();
                            } else {

                                ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
                                ActivityManager.getMyMemoryState(myProcess);
                                Boolean isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                                if (isInBackground) {
                                    finalNotification(agoraKeyModel);


                                } else {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setComponent(new ComponentName("com.nbird.call_random", "com.nbird.call_random.MAIN.CallRequestActivity"));
                                    intent.putExtra("player1", agoraKeyModel.getPlayer1());
                                    intent.putExtra("player2", agoraKeyModel.getPlayer2());
                                    intent.putExtra("appId", agoraKeyModel.getAppId());
                                    intent.putExtra("token", agoraKeyModel.getToken());
                                    intent.putExtra("channel", agoraKeyModel.getChannelName());
                                    intent.putExtra("mainUID", myUID);
                                    //  intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(intent);


                                    //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // You need this if starting
//                            //  the activity from a service
//                            intent.setAction(Intent.ACTION_MAIN);
//                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                    finish();
                                }


                            }


                        }


                        }catch (Exception e){

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };

                myRef.child("AGORA_ROOM").child(myUID).addValueEventListener(valueEventListener);



            }
        });
    }





    void finalNotification(AgoraKeyModel agoraKeyModel){




        myRef.child("USER").child(agoraKeyModel.getPlayer2()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);



        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                    "Random Calling",
                    NotificationManager.IMPORTANCE_HIGH);



            channel.setDescription("Incoming call. Please respond");




            mNotificationManager.createNotificationChannel(channel);
        }

                mediaPlayer= MediaPlayer.create(MainActivity.this, R.raw.oppo_ring);
                mediaPlayer.start();
                mediaPlayer.setVolume(0.4f,0.4f);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.parseColor("#98A8D0"))// notification icon
                .setContentTitle(user.getName()) // title for notification
                .setContentText("Random Calling...")// message for notification
                .setAutoCancel(true)
//                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)

                .setPriority(NotificationCompat.PRIORITY_MAX);
               // .setCustomBigContentView(expandedView);



        ; // clear notification after click
        Intent intent = new Intent(getApplicationContext(), CallRequestActivity.class);

                intent.putExtra("player1",agoraKeyModel.getPlayer1());
                intent.putExtra("player2",agoraKeyModel.getPlayer2());
                intent.putExtra("appId",agoraKeyModel.getAppId());
                intent.putExtra("token",agoraKeyModel.getToken());
                intent.putExtra("channel",agoraKeyModel.getChannelName());
                intent.putExtra("mainUID",myUID);

        PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
        finish();




//                Window window = MainActivity.this.getWindow();
//                window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//                window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//                window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

                Toast.makeText(MainActivity.this, "Please check the notification bar. You are getting a call.", Toast.LENGTH_LONG).show();
                countDownTimer=new CountDownTimer(1000*15,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
                        ActivityManager.getMyMemoryState(myProcess);
                        Boolean isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                        if (!isInBackground) {
                            try{
                                mediaPlayer.pause();
                                mediaPlayer.reset();
                                mediaPlayer.release();
                                mediaPlayer=null;
                            }catch (Exception e){

                            }
                        }



                        if(mNotificationManager==null){
                            mediaPlayer.pause();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            mediaPlayer=null;
                        }


                    }

                    @Override
                    public void onFinish() {



                        try{
                            mNotificationManager.cancel(0);

                        }catch (Exception e){

                        }

                        try{
                            mediaPlayer.pause();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            mediaPlayer=null;
                        }catch (Exception e){

                        }

                        onlineSetter();
                    }
                }.start();




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private void setLayoutUI(){
        levelStr= appData.getLevel();
        genderStr= appData.getGenderPref();

        switch (levelStr){
            case "BEGINNER":
                beginner.setBackgroundResource(R.drawable.uniform_nextbutton);
                intermediate.setBackgroundResource(R.drawable.border);
                advance.setBackgroundResource(R.drawable.border);
                break;
            case "INTERMEDIATE":
                beginner.setBackgroundResource(R.drawable.border);
                intermediate.setBackgroundResource(R.drawable.uniform_nextbutton);
                advance.setBackgroundResource(R.drawable.border);
                break;
            case "ADVANCE":
                beginner.setBackgroundResource(R.drawable.border);
                intermediate.setBackgroundResource(R.drawable.border);
                advance.setBackgroundResource(R.drawable.uniform_nextbutton);
                break;
        }


        switch (genderStr){
            case "ANY":
                anyGender.setChecked(true);
                male.setChecked(false);
                female.setChecked(false);
                break;
            case "MALE":
                anyGender.setChecked(false);
                male.setChecked(true);
                female.setChecked(false);
                break;
            case "FEMALE":
                anyGender.setChecked(false);
                male.setChecked(false);
                female.setChecked(true);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    private void noInternet(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {


        }
        else {


            noInternetDialog();


        }
    }













    private void noInternetDialog(){



        AlertDialog.Builder builderRemove=new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        View viewRemove1= LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_model_1,(ConstraintLayout) findViewById(R.id.layoutDialogContainer),false);
        builderRemove.setView(viewRemove1);
        builderRemove.setCancelable(false);
        Button button=(Button) viewRemove1.findViewById(R.id.button);

        TextView textTitle=(TextView) viewRemove1.findViewById(R.id.textTitle);
        textTitle.setText("No Internet");


        TextView textDis=(TextView) viewRemove1.findViewById(R.id.textDis);
        textDis.setText("This app requires internet connection to make calls. Please connect with internet and retry again.");

        LottieAnimationView anim=(LottieAnimationView)  viewRemove1.findViewById(R.id.anim);
        anim.setAnimation(R.raw.no_internet);
        anim.playAnimation();
        anim.loop(true);



        button.setText("OKAY");


        MobileAds.initialize(MainActivity.this);
        AdLoader adLoader = new AdLoader.Builder(MainActivity.this, Constant.NATIVE_ID)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        ColorDrawable cd = new ColorDrawable(0x393F4E);

                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(cd).build();
                        TemplateView template = viewRemove1.findViewById(R.id.my_template);
                        template.setStyles(styles);
                        template.setNativeAd(nativeAd);
                        template.setVisibility(View.VISIBLE);
                        NATIVE_ADS1=nativeAd;
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());


        final AlertDialog alertDialog=builderRemove.create();
        if(alertDialog.getWindow()!=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        try{
            alertDialog.show();
        }catch (Exception e){

        }



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    alertDialog.dismiss();
                }catch (Exception e){

                }

            }
        });

    }


    private void rewardAdsLoader(){

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(MainActivity.this, Constant.REWAD_ADS_ID,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.

                     //   Toast.makeText(MainActivity.this, loadAdError.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        // Toast.makeText(context, "Ad was loaded", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Ad was loaded.");
                    }
                });


        try{
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.




                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    rewardedAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Toast.makeText(MainActivity.this, "Ads failed to load", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Ad failed to show fullscreen content.");
                    rewardedAd = null;
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.



                    Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.");
                }
            });
        }catch (Exception e){

        }



    }




    private void packageDialog(){



        AlertDialog.Builder builderRemove=new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        View viewRemove1= LayoutInflater.from(MainActivity.this).inflate(R.layout.package_dialog,(ConstraintLayout) findViewById(R.id.layoutDialogContainer),false);
        builderRemove.setView(viewRemove1);
        builderRemove.setCancelable(false);


        TextView balanceTextView=(TextView) viewRemove1.findViewById(R.id.balanceTextView);
        balanceTextView.setText(String.valueOf(appData.getMyBalance()));

        CardView package1=(CardView) viewRemove1.findViewById(R.id.package1);
        CardView package2=(CardView) viewRemove1.findViewById(R.id.package2);
        CardView package3=(CardView) viewRemove1.findViewById(R.id.package3);
        CardView package4=(CardView) viewRemove1.findViewById(R.id.package4);


        ImageView cancel=(ImageView) viewRemove1.findViewById(R.id.cancel);




        final AlertDialog alertDialog=builderRemove.create();
        if(alertDialog.getWindow()!=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        try{
            alertDialog.show();
        }catch (Exception e){

        }


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    alertDialog.dismiss();
                }catch (Exception e){

                }
            }
        });

        package1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                indicator=1;
                startPayment();

                try{
                    alertDialog.dismiss();
                }catch (Exception e){

                }

            }
        });

        package2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                indicator=2;
                startPayment();

                try{
                    alertDialog.dismiss();
                }catch (Exception e){

                }

            }
        });

        package3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                indicator=3;
                startPayment();

                try{
                    alertDialog.dismiss();
                }catch (Exception e){

                }

            }
        });

        package4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                indicator=4;
                startPayment();

                try{
                    alertDialog.dismiss();
                }catch (Exception e){

                }

            }
        });


    }

    public void startPayment() {

        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.ic_launcher_foreground);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Random Caller");
            options.put("description", "Get gold coins");
            options.put("image", "https://drive.google.com/file/d/1WH96VlHZORi5C9CQjvnmdkAyp6CQelOt/view?usp=sharing");
            options.put("theme.color", "#7E92CE");
            options.put("currency", "INR");


//            JSONObject configObj = new JSONObject();
//            JSONObject displayObj = new JSONObject();
//            JSONArray hideObj = new JSONArray();
//            JSONObject methodObj = new JSONObject();
//            JSONObject preferencesObj = new JSONObject();
//
//            preferencesObj.put("show_default_blocks", "true");
//            methodObj.put("method","upi");
//            hideObj.put(methodObj);
//            displayObj.put("hide", hideObj);
//            displayObj.put("preferences", preferencesObj);
//            configObj.put("display",displayObj);
//
//            options.put("config", configObj);




            switch (indicator){
                case 1:
                    options.put("amount", "1500");break;
                case 2:
                    options.put("amount", "3000");break;
                case 3:
                    options.put("amount", "8000");break;
                case 4:
                    options.put("amount", "14000");break;

            }

            checkout.open(activity, options);
        } catch(Exception e) {
            Toast.makeText(activity, "Error in starting Razorpay", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPaymentSuccess(String s, PaymentData paymentData) {

        String paymentId = paymentData.getPaymentId();
      //  String mail=paymentData.getUserEmail();
        String number=paymentData.getUserContact();





        switch (indicator){
            case 1:
                Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show();

                appData.setMyBalance(appData.getMyBalance()+100);

                myRef.child("USER").child(appData.getMyUID()).child("balance").setValue(appData.getMyBalance()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                    }
                });


                balanceTextView.setText(String.valueOf(appData.getMyBalance()));

                 break;
            case 2:
                Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show();
                appData.setMyBalance(appData.getMyBalance()+250);

                myRef.child("USER").child(appData.getMyUID()).child("balance").setValue(appData.getMyBalance()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                    }
                });


                balanceTextView.setText(String.valueOf(appData.getMyBalance()));
                break;
            case 3:
                Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show();
                appData.setMyBalance(appData.getMyBalance()+800);

                myRef.child("USER").child(appData.getMyUID()).child("balance").setValue(appData.getMyBalance()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                    }
                });


                balanceTextView.setText(String.valueOf(appData.getMyBalance()));
                break;
            case 4:
                Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show();

                appData.setMyBalance(appData.getMyBalance()+1500);

                myRef.child("USER").child(appData.getMyUID()).child("balance").setValue(appData.getMyBalance()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                    }
                });


                balanceTextView.setText(String.valueOf(appData.getMyBalance()));

                break;

        }

    }

    public void onPaymentError(int i, String s,PaymentData paymentData) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
        Log.i("ERROR",s);
    }


}





