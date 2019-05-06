package com.niet.medicalblocks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorListActivity extends AppCompatActivity {
    private ListView doctorListView;
    private String user_type;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String uid;
    private String image_url;
    private Toolbar toolbar;
    private ProfileDetails currentUser;
    ArrayList<ProfileDetails> doctorList;
    private ProgressBar progressbar;

    Blockchain tcpCoin;
    List<BlockModel> preBlocks = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        startProgressBar();
        doctorListView = (ListView) findViewById(R.id.doctor_list_view);
        toolbar = (Toolbar) findViewById(R.id.doctorlisttoolbar);
        toolbar.setTitle("Select Doctor");
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        currentUser = new ProfileDetails(user_type, name, email, phone, address, uid, image_url);

        fetchData();
    }

    private void fetchData(){
        doctorList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Doctor").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    Log.d("TAG", "Data Fetched");
                    for (DocumentSnapshot doc : task.getResult()){
                        ProfileDetails profileDetails = new ProfileDetails();
                        profileDetails.setUser_type(doc.getString("user_type"));
                        profileDetails.setName(doc.getString("name"));
                        profileDetails.setPhone(doc.getString("phone"));
                        profileDetails.setEmail(doc.getString("email"));
                        profileDetails.setAddress(doc.getString("address"));
                        profileDetails.setUid(doc.getString("uid"));
                        profileDetails.setImage_url(doc.getString("image_url"));
                        doctorList.add(profileDetails);
                    }
                    DoctorListAdaptor employeeAdapter = new DoctorListAdaptor(DoctorListActivity.this, doctorList, currentUser);
                    doctorListView.setAdapter(employeeAdapter);
                    progressbar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void startProgressBar() {

        progressbar=(ProgressBar)findViewById(R.id.progressbar_home);
        Circle wave=new Circle();
        progressbar.setIndeterminateDrawable(wave);
        progressbar.setVisibility(View.VISIBLE);
    }
}
