package com.niet.medicalblocks;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class App extends Application {
    /*@Override
    public void onCreate() {
        super.onCreate();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("TAG", "User yes");
            gotoMain();
            // User is signed in
        } else {
            // No user is signed in
            Log.d("TAG","No user");
            gotoLogin();
        }
    }*/

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    private void gotoLogin(){
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
    private void gotoMain(){
        Intent i = new Intent(this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
