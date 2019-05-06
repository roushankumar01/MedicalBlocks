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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadDocActivity extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;
    private EditText docName;
    private ImageView addDoc;
    private Button uploadDoc;

    private String user_type;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String uid;
    private String image_url;
    private String download_url;
    private DatabaseReference mRef;
    private StorageReference profileImageReference;
    private View buttonClickView;
    private Toolbar toolbar;

    private ProgressBar progressbar;

    Map<String, Object> data;
    Uri uriProfileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_doc);
        getIntentData();
        init();
        addDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        uploadDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickView = v;
                startProgressBar();
                updateProfile();

            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init(){
        docName = findViewById(R.id.input_doc_name);
        addDoc = findViewById(R.id.btn_adddoc);
        uploadDoc = findViewById(R.id.btn_upload_doc);
        toolbar = findViewById(R.id.doc_upload_toolbar);
        toolbar.setTitle("Upload Doc");
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        setSupportActionBar(toolbar);
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
                addDoc.setImageBitmap(bitmap);


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

    private void updateProfile() {
        if (uriProfileImage!=null){
            profileImageReference = FirebaseStorage.getInstance().getReference("doc/").child(System.currentTimeMillis() + "."+getFileExtension(uriProfileImage));
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
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }else {

        }
    }

    private void storeUserInfo(String download_url) {
        String docNameValue = docName.getText().toString();
        docName.setText(null);
        if(docNameValue.isEmpty()){
            showSnacker("Enter doc Name");
            return;
        }
        data = new HashMap<>();
        data.put("user_type",user_type);
        data.put("name",name);
        data.put("email",email);
        data.put("phone",phone);
        data.put("address",address);
        data.put("uid",uid);
        data.put("doc_name", docNameValue);
        data.put("image_url", download_url);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(user_type).document(uid).collection("doc").document().set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    showSnacker("Doc has been uploaded");
                    finish();
                }else {
                    showSnacker("Doc upload failed due " + task.getException());
                }
            }
        });



    }
    private void showSnacker(String message){
        Snackbar.make(buttonClickView, message, Snackbar.LENGTH_LONG ).show();
    }
    private void startProgressBar() {

        progressbar=(ProgressBar)findViewById(R.id.progressbar_upload_doc);
        Circle wave=new Circle();
        progressbar.setIndeterminateDrawable(wave);
        progressbar.setVisibility(View.VISIBLE);
    }
}
