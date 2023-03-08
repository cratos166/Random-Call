package com.nbird.call_random.MAIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nbird.call_random.DATA.AppData;
import com.nbird.call_random.MAIN.MODEL.OnlineModel;
import com.nbird.call_random.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        appData=new AppData(MainActivity.this);


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

        setLayoutUI();

        connectionStatus=new ConnectionStatus(myUID);

        myName=appData.getMyName();
        myUID=appData.getMyUID();
        myImage=appData.getMyImage();
        myGender=appData.getMyGender();


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

        onlineSetter();

    }




    private void onlineSetter(){



        OnlineModel onlineModel=new OnlineModel(myUID,1);
        myRef.child("ONLINE").child(myUID).setValue(onlineModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                connectionStatus.myStatusSetter(connectionEventLisner);


//                valueEventListener=new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        try{
//
//
//
//                        }catch (Exception e){
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                };
//
//                myRef.child("ONLINE").child(myUID).addValueEventListener(valueEventListener);



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

}