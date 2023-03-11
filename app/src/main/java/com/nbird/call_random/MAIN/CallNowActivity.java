package com.nbird.call_random.MAIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.nbird.call_random.UNIVERSAL.UTILS.ConnectionStatus;

import org.w3c.dom.Text;

public class CallNowActivity extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    TextView notice,declineText,name;
    CountDownTimer countDownTimer;

    

    String  previousUID,myUID;
    AppData appData;
    ValueEventListener valueEventListener;
    CardView decline;
    Boolean userGot=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_now);

      //  previousUID=getIntent().getStringExtra("previousUID");
        
        appData=new AppData(CallNowActivity.this);


        

        notice=(TextView) findViewById(R.id.notice);
        declineText=(TextView) findViewById(R.id.declineText);
        decline=(CardView) findViewById(R.id.decline);
        name=(TextView) findViewById(R.id.name);





        myUID=appData.getMyUID();

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{countDownTimer.cancel();}catch (Exception e){}

                try{ myRef.child("AGORA_ROOM").child(appData.getMyUID()).removeEventListener(valueEventListener);}catch (Exception e){}

                Intent intent=new Intent(CallNowActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });






        isAnyPlayerActive();



    }


    private void cc(){
        countDownTimer=new CountDownTimer(15*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                notice.setVisibility(View.VISIBLE);
            }
        }.start();
    }






    private void isAnyPlayerActive(){


        valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()!=null){


                    try{
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){

                            try{
                                OnlineModel onlineModel=dataSnapshot.getValue(OnlineModel.class);


                                if(onlineModel.getStatus()==0){
                                    myRef.child("ONLINE").child(onlineModel.getUid()).removeValue();
                                }else {

                                    if(!onlineModel.getUid().equals(myUID)){
                                        if(!onlineModel.getUid().equals(previousUID)){


                                            try{countDownTimer.cancel();}catch (Exception e){}
                                            notice.setVisibility(View.GONE);
                                            decline.setVisibility(View.GONE);
                                            declineText.setVisibility(View.GONE);


                                            myRef.child("ONLINE").removeEventListener(valueEventListener);

                                            userGot=true;

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

                                                    Toast.makeText(CallNowActivity.this, "Call send to other user. Waiting for response.", Toast.LENGTH_LONG).show();

                                                    name.setText("WAITING FOR RESPONSE...\nPLEASE WAIT!");

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

                                                                    Toast.makeText(CallNowActivity.this, "Call accepted", Toast.LENGTH_LONG).show();
                                                                    myRef.child("AGORA_ROOM").child(onlineModel.getUid()).removeEventListener(valueEventListener);

                                                                    try{countDownTimer.cancel();}catch (Exception e){}


                                                                    Intent intent=new Intent(CallNowActivity.this,CallActivity.class);
                                                                    intent.putExtra("player1",agoraKeyModel.getPlayer1());
                                                                    intent.putExtra("player2",agoraKeyModel.getPlayer2());
                                                                    intent.putExtra("appId",agoraKeyModel.getAppId());
                                                                    intent.putExtra("token",agoraKeyModel.getToken());
                                                                    intent.putExtra("channel",agoraKeyModel.getChannelName());
                                                                    intent.putExtra("mainUID",onlineModel.getUid());
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }else if(agoraKeyModel.getAccept()==2 || agoraKeyModel.getAccept()==3){
                                                                    OnlineModel onlineModel2=new OnlineModel(onlineModel.getUid(),1);
                                                                    myRef.child("ONLINE").child(onlineModel.getUid()).setValue(onlineModel2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                        }
                                                                    });


                                                                    if(agoraKeyModel.getAccept()==2){
                                                                        Toast.makeText(CallNowActivity.this, "Call rejected. Trying more calls, please wait", Toast.LENGTH_LONG).show();
                                                                    }else{
                                                                        Toast.makeText(CallNowActivity.this, "Call was not picked up. Trying more calls, please wait", Toast.LENGTH_LONG).show();
                                                                    }


                                                                    myRef.child("AGORA_ROOM").child(onlineModel.getUid()).removeValue();

                                                                    try{
                                                                        myRef.child("AGORA_ROOM").child(onlineModel.getUid()).removeEventListener(valueEventListener);
                                                                    }catch (Exception e){

                                                                    }
                                                                    name.setText("CONNECTING\nPLEASE WAIT!");
                                                                    decline.setVisibility(View.VISIBLE);
                                                                    declineText.setVisibility(View.VISIBLE);
                                                                    cc();
                                                                    previousUID=onlineModel.getUid();
                                                                    isAnyPlayerActive();

                                                                }




                                                            }catch (Exception e){



                                                            }





                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    };
                                                    myRef.child("AGORA_ROOM").child(onlineModel.getUid()).addValueEventListener(valueEventListener);



                                                }
                                            });
                                            break;
                                        }else{

                                        }
                                    }else{

                                        myRef.child("ONLINE").child(myUID).removeValue();


                                    }




                                }
                            }catch (Exception e){


                                e.printStackTrace();

                            }

                        }
                        if(!userGot){

                        }


                    }catch (Exception e4){

                    }


                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myRef.child("ONLINE").addValueEventListener(valueEventListener);

    }

    public void onBackPressed(){

    }


}