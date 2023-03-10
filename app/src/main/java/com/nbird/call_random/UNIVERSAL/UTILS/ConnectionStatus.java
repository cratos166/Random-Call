package com.nbird.call_random.UNIVERSAL.UTILS;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConnectionStatus {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    String myUID;
    DatabaseReference connectedRef = database.getReference(".info/connected");

    ValueEventListener myCallvalueEventListener,valueEventListener;

    public ConnectionStatus(String myUID,ValueEventListener valueEventListener) {
        this.myUID=myUID;
        this.valueEventListener=valueEventListener;
    }

    public ConnectionStatus(ValueEventListener myCallvalueEventListener){
        this.myCallvalueEventListener=myCallvalueEventListener;
    }


    public void myStatusSetter(){


        valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    myRef.child("ONLINE").child(myUID).child("status").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                    myRef.child("ONLINE").child(myUID).child("status").onDisconnect().setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        connectedRef.addValueEventListener(valueEventListener);



    }


    public void myCallConnecterStatus(String mainUID){


        myCallvalueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    myRef.child("AGORA_ROOM").child(mainUID).child("callOver").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                    myRef.child("AGORA_ROOM").child(mainUID).child("callOver").onDisconnect().setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        connectedRef.addValueEventListener(myCallvalueEventListener);


    }

    public void removeListner(){

        try{
            connectedRef.removeEventListener(valueEventListener);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void removeConnectionMyCall(){

        try{
            connectedRef.removeEventListener(myCallvalueEventListener);
        }catch (Exception e){
            e.printStackTrace();
        }


    }



}
