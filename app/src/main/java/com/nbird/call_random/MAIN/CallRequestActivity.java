package com.nbird.call_random.MAIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nbird.call_random.CALL.CallActivity;
import com.nbird.call_random.R;
import com.nbird.call_random.REGISTRATION.MODEL.User;
import com.nbird.call_random.REGISTRATION.RegistrationActivity;
import com.nbird.call_random.UNIVERSAL.DIALOG.LoadingDialog;
import com.nbird.call_random.UNIVERSAL.UTILS.SongSetting;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallRequestActivity extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    String appId,channelName,token;
    String player1UID,player2UID,mainUID;


    CircleImageView propic;
    TextView name,dis;
    CardView decline,accept;
    LoadingDialog loadingDialog;

    SongSetting songSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_request);


        appId=getIntent().getStringExtra("appId");
        channelName=getIntent().getStringExtra("channel");
        token=getIntent().getStringExtra("token");
        player1UID=getIntent().getStringExtra("player1");
        player2UID=getIntent().getStringExtra("player2");
        mainUID=getIntent().getStringExtra("mainUID");


        propic=(CircleImageView) findViewById(R.id.propic);
        name=(TextView) findViewById(R.id.name);
        dis=(TextView) findViewById(R.id.dis);
        decline=(CardView) findViewById(R.id.decline);
        accept=(CardView) findViewById(R.id.accept);


        loadingDialog=new LoadingDialog(CallRequestActivity.this);
        loadingDialog.showLoadingDialog();

        songSetting=new SongSetting(CallRequestActivity.this);



        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showLoadingDialog();
                songSetting.songStop();
                myRef.child("AGORA_ROOM").child(player2UID).child("accept").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        loadingDialog.dismissLoadingDialog();
                        Intent intent = new Intent(CallRequestActivity.this, CallActivity.class);
                        intent.putExtra("player1",player1UID);
                        intent.putExtra("player2",player2UID);
                        intent.putExtra("appId",appId);
                        intent.putExtra("token",token);
                        intent.putExtra("channel",channelName);
                        intent.putExtra("mainUID",mainUID);
                        startActivity(intent);
                        finish();
                    }
                });


            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showLoadingDialog();
                songSetting.songStop();
                myRef.child("AGORA_ROOM").child(player2UID).child("accept").setValue(2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        loadingDialog.dismissLoadingDialog();

                        Intent intent = new Intent(CallRequestActivity.this, MainActivity.class);
                        intent.putExtra("previousUID",player1UID);
                        startActivity(intent);
                        finish();
                    }
                });


            }
        });




        myRef.child("USER").child(player2UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                Glide.with(CallRequestActivity.this).load(user.getImageURL()).into(propic);
                name.setText(user.getName());
                dis.setText(user.getDis());

                loadingDialog.dismissLoadingDialog();

                songSetting.startMusic();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }


}