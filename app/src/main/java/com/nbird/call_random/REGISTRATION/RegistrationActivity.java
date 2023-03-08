package com.nbird.call_random.REGISTRATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nbird.call_random.DATA.AppData;
import com.nbird.call_random.DATA.Constant;
import com.nbird.call_random.MAIN.MainActivity;
import com.nbird.call_random.R;
import com.nbird.call_random.REGISTRATION.MODEL.User;
import com.nbird.call_random.UNIVERSAL.DIALOG.LoadingDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    CircleImageView propic;
    TextInputEditText username,dis;
    RadioGroup radioGroup;
    Button save;

    Uri imageUri;
    FirebaseStorage storage;
    StorageReference storageReference;

    String uid;
    AppData appData;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    String IMAGE_URL;
    int RC_SIGN_IN = 1;
    LoadingDialog loadingDialog;

    RadioButton male,female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        appData=new AppData(RegistrationActivity.this);
        loadingDialog=new LoadingDialog(RegistrationActivity.this);

        IMAGE_URL= Constant.DEFAULT_IMAGE;
        uid=UUID.randomUUID().toString();

        propic=(CircleImageView) findViewById(R.id.propic);
        radioGroup=(RadioGroup) findViewById(R.id.radioGroup);
        save=(Button) findViewById(R.id.save);
        username=(TextInputEditText) findViewById(R.id.username);
        dis=(TextInputEditText) findViewById(R.id.dis);
        male=(RadioButton) findViewById(R.id.male);
        female=(RadioButton) findViewById(R.id.female);


        Boolean isSetting=getIntent().getBooleanExtra("isSetting",false);

        if(!isSetting){
            if(!appData.isFirstTime()){
                Intent intent=new Intent(RegistrationActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }else{
            propic.setBackgroundResource(0);
            Glide.with(RegistrationActivity.this).load(appData.getMyImage()).apply(RequestOptions
                            .bitmapTransform(new RoundedCorners(18)))
                    .into(propic);

            username.setText(appData.getMyName());
            dis.setText(appData.getMyDis());

            IMAGE_URL=appData.getMyImage();


            if(appData.getMyGender().equals("Male")){
                male.setChecked(true);
                female.setChecked(false);
            }else{
                male.setChecked(false);
                female.setChecked(true);
            }

        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("LENGTH",String.valueOf(dis.getText().toString().length()));

                if(username.getText().toString().equals("")){
                    username.setError("Fields cannot be empty!");
                }else if(dis.getText().toString().length()>120){

                    dis.setError("Fields must be less than 120 characters");
                }
                else{
                    loadingDialog.showLoadingDialog();
                    dataUploader();
                }




            }
        });



        propic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });



    }


    private void dataUploader(){
        int selectedId=radioGroup.getCheckedRadioButtonId();
        RadioButton radioSexButton=(RadioButton)findViewById(selectedId);

        User user=new User(username.getText().toString(),IMAGE_URL,uid,radioSexButton.getText().toString(),dis.getText().toString());

        myRef.child("USER").child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    loadingDialog.dismissLoadingDialog();

                    appData.setMyName(username.getText().toString());
                    appData.setMyImage(IMAGE_URL);
                    appData.setMyUID(uid);
                    appData.setMyGender(radioSexButton.getText().toString());
                    appData.setMyDis(dis.getText().toString());
                    appData.setGenderPref("ANY");
                    appData.setLevel("BEGINNER");

                    Intent intent=new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void selectImage() {
        final CharSequence[] options = {"Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

               /* if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } */
                if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }

        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);



        try {
            if (resultCode != RESULT_CANCELED) {
                switch (requestCode) {
              /*  case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        nav_image.setImageBitmap(selectedImage);
                        uploadImage();
                    }
                    break;*/
                    case 1:
                        if (resultCode == RESULT_OK) {
                            try {
                                imageUri = data.getData();
                                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                try {
                                    propic.setImageBitmap(selectedImage);
                                } catch (Exception e) {
                                }


                                uploadImage();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(RegistrationActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(RegistrationActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }

        } catch (Exception e) {

        }


    }

    private void uploadImage() {


        if (imageUri != null) {


            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();



            storageReference = storageReference.child("USER/IMAGE/" + uid);


            // adding listeners on upload
            // or failure of image
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast.makeText(RegistrationActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                    try {

                                        StorageReference urlref = storageReference;
                                        urlref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri downloadUrl) {
                                                IMAGE_URL = downloadUrl.toString();
                                            }
                                        });

                                    } catch (Exception e) {

                                    }
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(RegistrationActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }
    }






}