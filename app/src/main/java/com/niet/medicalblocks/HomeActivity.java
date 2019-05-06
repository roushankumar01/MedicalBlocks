package com.niet.medicalblocks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String uid;
    private String userType;
    private String name;
    private String phone;
    private String address;
    private String email;
    private ProfileDetails profileDetails;
    private TextView navName;
    private TextView navEmail;
    private ListView appointmentListView;
    private FloatingActionButton fab;
    private ArrayAdapter<BlockModel> appointmentDetails;
    private ImageView profileImageView;
    private ProgressBar progressbar;
    MenuItem mitem;
    Toolbar toolbar;
    ArrayList<BlockModel> AppointmentList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user==null){
            gotoLogin();
        }else {
            fetchData();
            setContentView(R.layout.activity_home);
            startProgressBar();
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoDoctorList();
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);


            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        mitem = item;

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.new_appointment) {
            gotoDoctorList();
            // Handle the camera action
        } else if (id == R.id.pre_appointment) {
            toolbar.setVisibility(View.GONE);

        } else if (id == R.id.view_profile) {
            gotoUserProfile();

        }  else if (id == R.id.logout) {
            logout();

        }else if (id == R.id.upload_doc){
            gotoUploadDoc();

        }else if (id == R.id.view_doc){
            gotoViewDoc();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void fetchData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d("TAG", "User yes");
            // User is signed in
        }
        uid = user.getUid();
        FirebaseDatabase rtdb = FirebaseDatabase.getInstance();
        DatabaseReference mRef = rtdb.getReference("users");
        mRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getData(dataSnapshot);
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getData(DataSnapshot dataSnapshot){
        profileDetails = dataSnapshot.getValue(ProfileDetails.class);
        Log.d("TAG" , "Profile Details are "+ profileDetails.getUser_type() + profileDetails.getName());
        init();
        fetchAppointmentDetails();
    }

    private void fetchAppointmentDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(profileDetails.getUser_type()).document(profileDetails.getUid()).collection("BlockChain").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    int i = 0;
                    for (DocumentSnapshot doc : task.getResult()){
                        if (i != 0) {
                            BlockModel blockModel = new BlockModel(doc.getString("aID"), doc.getString("pName"), doc.getString("pUID"), doc.getString("hash"), doc.getString("previousHash"), doc.getString("data"), doc.getString("dName"), doc.getString("dUID"));
                            AppointmentList.add(blockModel);
                        }
                        i++;
                    }
                    AppointDetailsAdaptor appointmentAdapter = new AppointDetailsAdaptor(HomeActivity.this, AppointmentList);
                    appointmentListView.setAdapter(appointmentAdapter);
                    progressbar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void init(){
        navName = (TextView) findViewById(R.id.nav_name);
        navEmail = (TextView) findViewById(R.id.nav_email);
        toolbar.setTitle(profileDetails.getName());
        navName.setText(profileDetails.getName());
        navEmail.setText(profileDetails.getEmail());
        appointmentListView = findViewById(R.id.appointment_listview);
        profileImageView = findViewById(R.id.name_profile_image);
        if (profileDetails.getImage_url()!=null){
            Uri uri = Uri.parse(profileDetails.getImage_url());
            Glide.with(getApplicationContext()).load(uri).into(profileImageView);
        }else {
            Glide.with(getApplicationContext()).load(R.drawable.student_01).into(profileImageView);
        }
        if (profileDetails.getUser_type().equals("Doctor")){
            fab.setEnabled(false);
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menuNav=navigationView.getMenu();
            MenuItem nav_item2 = menuNav.findItem(R.id.new_appointment);
            nav_item2.setEnabled(false);
        }
    }
    private void gotoDoctorList(){
        Intent i = new Intent(this, DoctorListActivity.class);
        i.putExtra("user_type",profileDetails.getUser_type());
        i.putExtra("name",profileDetails.getName());
        i.putExtra("email",profileDetails.getEmail());
        i.putExtra("phone",profileDetails.getPhone());
        i.putExtra("address",profileDetails.getAddress());
        i.putExtra("uid",profileDetails.getUid());
        i.putExtra("image_url", profileDetails.getImage_url());
        startActivity(i);
    }
    private void gotoLogin(){
        Intent i = new Intent(this, LoginActivity.class);
        finish();
        startActivity(i);
    }
    private void logout(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        gotoLogin();
    }
    private void gotoUserProfile(){
        Intent i = new Intent(this, UserProfile.class);
        i.putExtra("user_type",profileDetails.getUser_type());
        i.putExtra("name",profileDetails.getName());
        i.putExtra("email",profileDetails.getEmail());
        i.putExtra("phone",profileDetails.getPhone());
        i.putExtra("address",profileDetails.getAddress());
        i.putExtra("uid",profileDetails.getUid());
        i.putExtra("image_url", profileDetails.getImage_url());
        startActivity(i);
    }
    private void startProgressBar() {

        progressbar=(ProgressBar)findViewById(R.id.progressbar_home);
        Circle wave=new Circle();
        progressbar.setIndeterminateDrawable(wave);
        progressbar.setVisibility(View.VISIBLE);
    }
    private void gotoUploadDoc(){
        Intent i = new Intent(this, UploadDocActivity.class);
        i.putExtra("user_type",profileDetails.getUser_type());
        i.putExtra("name",profileDetails.getName());
        i.putExtra("email",profileDetails.getEmail());
        i.putExtra("phone",profileDetails.getPhone());
        i.putExtra("address",profileDetails.getAddress());
        i.putExtra("uid",profileDetails.getUid());
        i.putExtra("image_url", profileDetails.getImage_url());
        startActivity(i);
    }

    private void gotoViewDoc(){
        Intent i = new Intent(this, ViewDocActivity.class);
        i.putExtra("user_type",profileDetails.getUser_type());
        i.putExtra("name",profileDetails.getName());
        i.putExtra("email",profileDetails.getEmail());
        i.putExtra("phone",profileDetails.getPhone());
        i.putExtra("address",profileDetails.getAddress());
        i.putExtra("uid",profileDetails.getUid());
        i.putExtra("image_url", profileDetails.getImage_url());
        startActivity(i);
    }
}
