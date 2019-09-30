package com.arefin.jockeychat.loginregister;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arefin.jockeychat.MainActivity;
import com.arefin.jockeychat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupAccount extends AppCompatActivity {

    private CircleImageView setupImage;
    private Uri mainImageURI = null;
    private Uri downloadUrl = null;
    private EditText setupName;
    private Button setupBtn;
    private ProgressBar setup_Progress;

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private DatabaseReference mDatabase;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);

        Toolbar setupToolbar =  (Toolbar)findViewById(R.id.setup_toolbar);
        setSupportActionBar(setupToolbar);

        setupImage = findViewById(R.id.setup_image);
        setupName = findViewById(R.id.setup_name);
        setupBtn = findViewById(R.id.setup_btn);
        setup_Progress = findViewById(R.id.setup_ProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        user_id =firebaseAuth.getCurrentUser().getUid();

        setup_Progress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);

        retrieveUserData();






        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String userName = setupName.getText().toString();
                final String user_id = firebaseAuth.getCurrentUser().getUid();


                if (!TextUtils.isEmpty(userName) && mainImageURI != null){

                    setup_Progress.setVisibility(View.VISIBLE);


                    final StorageReference image_path = storageReference.child("pro_pic").child(user_id + ".jpg");


                    image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()){

                                image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //downloadUrl = uri;
                                        //userMap.put("image", uri.toString());
                                        mDatabase.child(user_id).child("image").setValue(uri.toString());

                                        mDatabase.child(user_id).child("name").setValue(userName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    setup_Progress.setVisibility(View.INVISIBLE);
                                                    retrieveUserData();
                                                    startActivity(new Intent(SetupAccount.this, MainActivity.class));
                                                    finish();

                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(SetupAccount.this, "error on mdatabase "+error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                });


                                //Toast.makeText(SetupAccount.this, downloadUrl.toString(), Toast.LENGTH_SHORT).show();
                                //userMap.put("image", downloadUrl.toString());



                                Toast.makeText(SetupAccount.this, "Uploaded SuccessFully !!", Toast.LENGTH_LONG).show();
                            }
                            else {
                                String error = task.getException().getMessage();
                                Toast.makeText(SetupAccount.this, "Error : " + error, Toast.LENGTH_LONG).show();
                            }
                            setup_Progress.setVisibility(View.INVISIBLE);

                        }
                    });

                }
                //When Only name change
                else if (!TextUtils.isEmpty(userName) && mainImageURI == null){
                    mDatabase.child(user_id).child("name").setValue(userName).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                setup_Progress.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(SetupAccount.this, MainActivity.class));
                                finish();

                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(SetupAccount.this, "error on mdatabase "+error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(SetupAccount.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(SetupAccount.this, "Permission Denied", Toast.LENGTH_LONG).show();

                    //Request dialogue to accept permission
                    ActivityCompat.requestPermissions(SetupAccount.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                } else {

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(SetupAccount.this);

                }

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private void retrieveUserData() {

        // ------------- Retrieve Name and Image from database -----------------//
        mDatabase.child(user_id).keepSynced(true);
        mDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {

                    String name = (String) dataSnapshot.child("name").getValue();
                    final String image = (String) dataSnapshot.child("image").getValue();


                    setupName.setText(name);
                    Picasso.get().load(image).placeholder(R.drawable.default_profile_pic).into(setupImage);

                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_profile_pic).into(setupImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.drawable.default_profile_pic).into(setupImage);
                        }
                    });

                } else {
                    Toast.makeText(SetupAccount.this, "Null info For this user", Toast.LENGTH_SHORT).show();
                }

                setup_Progress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
