package com.nbird.call_random.MAIN;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;
import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static android.app.Service.START_NOT_STICKY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

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
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
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
import com.nbird.call_random.UNIVERSAL.MODEL.UpdateInfo;
import com.nbird.call_random.UNIVERSAL.UTILS.ConnectionStatus;
import com.nbird.call_random.UNIVERSAL.UTILS.NotificationIntentService;

public class MainActivity extends AppCompatActivity {

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

    private void notification(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.mic_on)
                        .setContentTitle("Notifications Example")
                        .setContentText("This is a test notification");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        appData=new AppData(MainActivity.this);


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
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                intent.putExtra("isSetting",true);
                startActivity(intent);


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

                        try{

                            AgoraKeyModel agoraKeyModel=snapshot.getValue(AgoraKeyModel.class);

                            Log.i("myuid",agoraKeyModel.getPlayer1());
                            Log.i("oppouid",agoraKeyModel.getPlayer2());
                            Log.i("appId",agoraKeyModel.getAppId());
                            Log.i("token",agoraKeyModel.getToken());
                            Log.i("channel",agoraKeyModel.getChannelName());


                                connectionStatus.removeListner();
                                myRef.child("AGORA_ROOM").child(myUID).removeEventListener(valueEventListener);
//                                Intent intent=new Intent(MainActivity.this,CallRequestActivity.class);










                         //   finalNotification("Incoming Call","Bob is calling you");














                            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.P) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setComponent(new ComponentName("com.nbird.call_random", "com.nbird.call_random.MAIN.CallRequestActivity"));
                                intent.putExtra("player1",agoraKeyModel.getPlayer1());
                                intent.putExtra("player2",agoraKeyModel.getPlayer2());
                                intent.putExtra("appId",agoraKeyModel.getAppId());
                                intent.putExtra("token",agoraKeyModel.getToken());
                                intent.putExtra("channel",agoraKeyModel.getChannelName());
                                intent.putExtra("mainUID",myUID);
                                //  intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);




                                //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // You need this if starting
//                            //  the activity from a service
//                            intent.setAction(Intent.ACTION_MAIN);
//                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                finish();
                            }else{

                                ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
                                ActivityManager.getMyMemoryState(myProcess);
                                Boolean isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                                if (isInBackground) {
                                    finalNotification(agoraKeyModel);



                                }else{
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setComponent(new ComponentName("com.nbird.call_random", "com.nbird.call_random.MAIN.CallRequestActivity"));
                                    intent.putExtra("player1",agoraKeyModel.getPlayer1());
                                    intent.putExtra("player2",agoraKeyModel.getPlayer2());
                                    intent.putExtra("appId",agoraKeyModel.getAppId());
                                    intent.putExtra("token",agoraKeyModel.getToken());
                                    intent.putExtra("channel",agoraKeyModel.getChannelName());
                                    intent.putExtra("mainUID",myUID);
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
                .setColor(Color.parseColor("#3D4352"))// notification icon
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


}





