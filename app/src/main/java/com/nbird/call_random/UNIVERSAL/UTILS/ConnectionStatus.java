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

    public ConnectionStatus(String myUID) {
        this.myUID=myUID;
    }


    public void myStatusSetter(ValueEventListener valueEventListener){
        DatabaseReference connectedRef = database.getReference(".info/connected");

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

}
