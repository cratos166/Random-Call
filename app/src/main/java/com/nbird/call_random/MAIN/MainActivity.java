package com.nbird.call_random.MAIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
import com.nbird.call_random.MAIN.MODEL.AgoraKeyModel;
import com.nbird.call_random.MAIN.MODEL.OnlineModel;
import com.nbird.call_random.R;
import com.nbird.call_random.REGISTRATION.RegistrationActivity;
import com.nbird.call_random.UNIVERSAL.UTILS.ConnectionStatus;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        appData=new AppData(MainActivity.this);


        previousUID=getIntent().getStringExtra("previousUID");


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


        isAnyPlayerActive();

        onlineSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onlineSwitch.isChecked()){
                    isAnyPlayerActive();
                }else{
                    myRef.child("ONLINE").child(myUID).removeValue();
                }
            }
        });

    }


    private void isAnyPlayerActive(){
        myRef.child("ONLINE").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()!=null){


                    try{
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){

                            try{
                                OnlineModel onlineModel=dataSnapshot.getValue(OnlineModel.class);


                                if(onlineModel.getStatus()==0){
                                    myRef.child("ONLINE").child(onlineModel.getUid()).removeValue();
                                    onlineSetter();
                                    break;
                                }else {

                                        if(!onlineModel.getUid().equals(previousUID)){
                                            myRef.child("ONLINE").child(onlineModel.getUid()).removeValue();



                                            AgoraAccount agoraAccount=new AgoraAccount();
                                            AgoraData data =agoraAccount.getRandomAgoraAcc();

                                            String appId=data.getAppId();
                                            String channelName=onlineModel.getUid();
                                            String token=agoraAccount.generateToken(data,channelName);


                                            AgoraKeyModel agoraKeyModel=new AgoraKeyModel(myUID,onlineModel.getUid(),appId,channelName,token,0);
                                            myRef.child("AGORA_ROOM").child(onlineModel.getUid()).setValue(agoraKeyModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {


                                                    connectionStatus.removeListner();
                                                    Intent intent=new Intent(MainActivity.this,CallRequestActivity.class);
                                                    intent.putExtra("player1",myUID);
                                                    intent.putExtra("player2",onlineModel.getUid());
                                                    intent.putExtra("appId",appId);
                                                    intent.putExtra("token",token);
                                                    intent.putExtra("channel",channelName);
                                                    intent.putExtra("mainUID",onlineModel.getUid());
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                            break;
                                        }else{
                                            onlineSetter();
                                            break;
                                        }





                                }
                            }catch (Exception e){

                                onlineSetter();
                                e.printStackTrace();


                            }



                        }
                    }catch (Exception e4){
                        onlineSetter();
                    }


                }else{
                    onlineSetter();
                }






            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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



                            if(agoraKeyModel.getAccept()==1){
                                connectionStatus.removeListner();
                                myRef.child("AGORA_ROOM").child(myUID).removeEventListener(valueEventListener);
                                Intent intent=new Intent(MainActivity.this,CallActivity.class);
                                intent.putExtra("player1",agoraKeyModel.getPlayer1());
                                intent.putExtra("player2",agoraKeyModel.getPlayer2());
                                intent.putExtra("appId",agoraKeyModel.getAppId());
                                intent.putExtra("token",agoraKeyModel.getToken());
                                intent.putExtra("channel",agoraKeyModel.getChannelName());
                                intent.putExtra("mainUID",myUID);
                                startActivity(intent);
                                finish();
                            }else if(agoraKeyModel.getAccept()==2){

                                myRef.child("AGORA_ROOM").child(myUID).removeValue();

                                try{
                                    myRef.child("AGORA_ROOM").child(myUID).removeEventListener(valueEventListener);
                                }catch (Exception e){

                                }


                                if(onlineSwitch.isChecked()){
                                    isAnyPlayerActive();
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

}