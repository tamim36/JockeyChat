package com.arefin.jockeychat.loginregister;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arefin.jockeychat.MainActivity;
import com.arefin.jockeychat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText regEmail;
    private EditText regPass;
    private EditText regConfirmPass;
    private Button regBtn;
    private Button regLoginBtn;
    private ProgressBar regProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        regEmail = (EditText)findViewById(R.id.reg_email_text);
        regPass = (EditText)findViewById(R.id.reg_password_text);
        regConfirmPass = (EditText)findViewById(R.id.reg_confirm_Text);
        regBtn = (Button) findViewById(R.id.reg_btn);
        regLoginBtn = (Button) findViewById(R.id.reg_login_Btn);
        regProgress = (ProgressBar)findViewById(R.id.regProgressBar);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = regEmail.getText().toString();
                String pass = regPass.getText().toString();
                String confPass = regConfirmPass.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confPass)){

                    if (pass.equals(confPass)){

                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    sendTomani();
                                }
                                else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error is : " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                    else {

                        Toast.makeText(RegisterActivity.this, "Password DOesn't match", Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Must Fill all Field", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            sendTomani();
        }
    }

    private void sendTomani() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
