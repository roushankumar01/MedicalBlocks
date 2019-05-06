package com.niet.medicalblocks;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class FotgotPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private Button btnForgot;
    private View buttonClickView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotgot_password);
        etEmail = findViewById(R.id.forgot_email);
        btnForgot = findViewById(R.id.btn_forgot);
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickView = v;
                sendVerificationLink();
            }
        });
    }

    private void sendVerificationLink() {

        String email = etEmail.getText().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("TAG","Link has been send");
                    confirmation();
                }else {
                    Log.d("TAG", "Error due to "+task.getException());
                    errorMessage("Sending verification mail failed due to "+task.getException());
                }
            }
        });

    }
    private void confirmation(){
        Snackbar.make(buttonClickView, "Password reset link has been send to email ", Snackbar.LENGTH_LONG).show();
        finish();
    }
    private void errorMessage(String errorMessage){
        Snackbar.make(buttonClickView, errorMessage,Snackbar.LENGTH_LONG).show();
    }
}
