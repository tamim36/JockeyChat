package com.arefin.jockeychat.loginregister;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.arefin.jockeychat.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupAccount extends AppCompatActivity {

    CircleImageView setupImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);

        Toolbar setupToolbar =  (Toolbar)findViewById(R.id.setup_toolbar);
        setSupportActionBar(setupToolbar);

        setupImage = findViewById(R.id.setup_image);

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(SetupAccount.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(SetupAccount.this, "Permission Denied", Toast.LENGTH_LONG).show();

                    //Request dialogue to accept permission
                    ActivityCompat.requestPermissions(SetupAccount.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                } else {

                    Toast.makeText(SetupAccount.this, "You already have Permission", Toast.LENGTH_LONG).show();

                }

            }
        });
    }
}
