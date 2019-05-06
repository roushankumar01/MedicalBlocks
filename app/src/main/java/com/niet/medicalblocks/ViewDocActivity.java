package com.niet.medicalblocks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewDocActivity extends AppCompatActivity {
    private ListView listView;
    private Toolbar toolbar;
    private String user_type;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String uid;
    private String image_url;
    ArrayList<ProfileDetails> docList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_doc);
        getIntentData();
        itit();
        fetchDoc();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void itit(){
        listView = findViewById(R.id.view_doc_listview);
        toolbar = findViewById(R.id.doc_view_toolbar);
        toolbar.setTitle("View Doc");
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        setSupportActionBar(toolbar);
    }
    private void fetchDoc(){
        Log.d("TAG", "Details are "+ user_type + uid);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(user_type).document(uid).collection("doc").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot doc : task.getResult()){
                        ProfileDetails current = new ProfileDetails(doc.getString("user_type"),doc.getString("name"),doc.getString("email"),doc.getString("phone"),doc.getString("address"),doc.getString("uid"),doc.getString("image_url"),doc.getString("doc_name"));
                        docList.add(current);
                    }
                    DocViewAdaptor docViewAdaptor = new DocViewAdaptor(ViewDocActivity.this, docList);
                    listView.setAdapter(docViewAdaptor);
                }
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
}
