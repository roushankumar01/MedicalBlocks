package com.niet.medicalblocks;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;
    private ImageView profileImageView;
    private ImageView editProfileImage;
    private TextView viewProfileName;
    private TextView viewProfileEmail;
    private TextView viewProfilePhone;
    private EditText editProfileName;
    private EditText editProfileAddress;
    private EditText editProfilePhone;
    private Button btnUpdateProfile;
    private String user_type;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String uid;
    private String image_url;
    private String download_url;
    Map<String, Object> data;
    Uri uriProfileImage;
    private DatabaseReference mRef;
    private StorageReference profileImageReference;
    private View buttonClickView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getIntentData();
        init();

        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickView = v;
                updateProfile();
            }
        });
    }

    private void getIntentData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle!=null){
            user_type = bundle.getString("user_type");
            name = bundle.getString("name");
            email = bundle.getString("email");
            phone = bundle.getString("phone");
            address = bundle.getString("address");
            uid = bundle.getString("uid");
            image_url = bundle.getString("image_url");
        }
    }

    private void init(){
        profileImageView = findViewById(R.id.view_profile_image);
        editProfileImage = findViewById(R.id.edit_profile_image);
        viewProfileName = findViewById(R.id.view_profile_name);
        viewProfileEmail = findViewById(R.id.view_profile_email);
        viewProfilePhone = findViewById(R.id.view_profile_phone);
        editProfileName = findViewById(R.id.edit_profile_name);
        editProfileAddress = findViewById(R.id.update_address);
        editProfilePhone = findViewById(R.id.update_phone);
        btnUpdateProfile = findViewById(R.id.btn_update_profile);

        if (image_url!=null){
            Uri uri = Uri.parse(image_url);
            Glide.with(getApplicationContext()).load(uri).into(profileImageView);
        }else {
            Glide.with(getApplicationContext()).load(R.drawable.student_01).into(profileImageView);
        }
        viewProfileName.setText(name);;
        viewProfileEmail.setText(email);
        viewProfilePhone.setText(phone);
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                profileImageView.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void updateProfile() {
        if (uriProfileImage!=null){
            profileImageReference = FirebaseStorage.getInstance().getReference("profilePics/").child(System.currentTimeMillis() + "."+getFileExtension(uriProfileImage));
            profileImageReference.putFile(uriProfileImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        profileImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                download_url=uri.toString();
                                storeUserInfo(download_url);
                            }
                        });

                    }
                    else {
                        Toast.makeText(UserProfile.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }else {
            storeUserInfo(image_url);
        }
    }

    private void storeUserInfo(String download_url) {
        String profileName=editProfileName.getText().toString();
        String profilePhone=editProfilePhone.getText().toString();
        String profileAddress=editProfileAddress.getText().toString();
        editProfileName.setText(null);
        editProfilePhone.setText(null);
        editProfileAddress.setText(null);
        if(profileName.isEmpty()){
            showSnacker("Name is required");
            return;
        }
        if(profilePhone.isEmpty()){
            showSnacker("Phone number is required");
            return;
        }
        if(profileAddress.isEmpty()){
            showSnacker("Address is required");
            return;
        }
        data = new HashMap<>();
        data.put("user_type",user_type);
        data.put("name",profileName);
        data.put("email",email);
        data.put("phone",profilePhone);
        data.put("address",profileAddress);
        data.put("uid",uid);
        data.put("image_url", download_url);
        mRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mRef.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("TAG", "Realtime Updates");
                    showSnacker("Profile has been update");
                    updateToFireStore();
                }else {
                    Log.d("TAG", "Error" + task.getException());
                    showSnacker("Profile update failed due to " +task.getException());
                }
            }
        });



    }
    private void showSnacker(String message){
        Snackbar.make(buttonClickView, message, Snackbar.LENGTH_LONG ).show();
    }
    private void updateToFireStore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(user_type).document(uid).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    showSnacker("Profile updated to Firestore");
                    finish();
                }else {
                    showSnacker("Profile updated to Firestore failed due to" + task.getException());
                }
            }
        });
    }
}
