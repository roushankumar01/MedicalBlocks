package com.niet.medicalblocks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AppointmentDetails extends AppCompatActivity {

    private ProfileDetails currentUser;
    private ProfileDetails selectedDoctor;
    private EditText dateTime;
    private Button bookAppointment;
    private Toolbar toolbar;
    Blockchain userBlockChain;
    Blockchain doctorBlockChain;
    View buttonClickView;
    List<BlockModel> preBlocks = new ArrayList<>();
    String doctorBlockResult;
    String userBlockResult;
    private String appoitnmentID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmsssss");
        appoitnmentID= "APNT"+df.format(c.getTime());
        if (bundle!=null){
            Log.d("TAG", "Selected USer type" + bundle.getString("duser_type") );
            Log.d("TAG" ,"Received intent data" + bundle.getString("uuser_type")+ bundle.getString("uname")+bundle.getString("uemail")+bundle.getString("uphone")+bundle.getString("uaddress")+ bundle.getString("uuid"));
            currentUser = new ProfileDetails(bundle.getString("uuser_type"), bundle.getString("uname"),bundle.getString("uemail"),bundle.getString("uphone"),bundle.getString("uaddress"), bundle.getString("uuid"),bundle.getString("uimage_url"));
            selectedDoctor = new ProfileDetails(bundle.getString("duser_type"), bundle.getString("dname"),bundle.getString("demail"),bundle.getString("dphone"),bundle.getString("daddress"), bundle.getString("duid"),bundle.getString("dimage_url"));
        }else {
            Log.d("TAG" ,"No intent data found");
        }
        Log.d("TAG", " Profile details are  " + currentUser.getUid() + currentUser.getName() + selectedDoctor.getUid());
        dateTime = findViewById(R.id.input_timing);
        toolbar = findViewById(R.id.appointmenttoolbar);
        toolbar.setTitle("Enter Appointment Details");
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG" ,"clicked");
                finish();
            }
        });
        bookAppointment = findViewById(R.id.btn_add_appointment);

        bookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickView = v;
                fetchBlockChain();
            }
        });

    }

    private void fetchBlockChain(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        preBlocks.clear();
        db.collection(currentUser.getUser_type()).document(currentUser.getUid()).collection("BlockChain").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()){
                    BlockModel blockModel = new BlockModel(doc.getString("aID"),doc.getString("pName"),doc.getString("pUID"),doc.getString("hash"),doc.getString("previousHash"),doc.getString("data"),doc.getString("dName"),doc.getString("dUID"));
                    preBlocks.add(blockModel);
                    deleteChainFromDB(doc.getId());
                }
                Log.d("TAG" , "Size of fetched data " +preBlocks.size());
                addBlock();
                //Log.d("TAG", " is chain valid?  " + userBlockChain.isValid());
            }
        });
    }
    private void deleteChainFromDB(final String docID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(currentUser.getUser_type()).document(currentUser.getUid()).collection("BlockChain").document(docID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("TAG" , "Chain is deleted for doc" + docID);
                }
                else {
                    Log.d("TAG", "Chain deletion error with doc " + docID +task.getException());
                }
            }
        });

    }

    private void addBlock(){
        userBlockChain = new Blockchain();
        if (preBlocks.size()==0){
            Log.d("TAG", "If part ");
            Block a = new Block(appoitnmentID, currentUser.getName(),currentUser.getUid(),dateTime.getText().toString(),selectedDoctor.getName(),selectedDoctor.getUid());
            userBlockChain.addBlock(a);
        }else {
            Log.d("TAG", "Else part ");
            for (int i = 1; i<preBlocks.size(); i++){
                Log.d("TAG" , "For loop to add pre block " + i);
                Block block = new Block(preBlocks.get(i).getaID(),
                        preBlocks.get(i).getpName(),
                        preBlocks.get(i).getpUID(),
                        preBlocks.get(i).getHash(),
                        preBlocks.get(i).getPreviousHash(),
                        preBlocks.get(i).getData(),
                        preBlocks.get(i).getdName(),
                        preBlocks.get(i).getdUID());
                userBlockChain.addPreBlocks(block);
            }
            Log.d("TAG" ,"Is chain valid" + userBlockChain.isValid());
            if (userBlockChain.isValid().equals("Yes")){
                Block a = new Block(appoitnmentID, currentUser.getName(),currentUser.getUid(),dateTime.getText().toString(),selectedDoctor.getName(),selectedDoctor.getUid());
                userBlockChain.addBlock(a);
            }
        }
        userBlockResult = userBlockChain.isValid();
        fetchDBlockChain();
    }
    private void fetchDBlockChain(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        preBlocks.clear();
        Log.d("TAG", "Details" + selectedDoctor.getUid() + selectedDoctor.getUser_type());
        db.collection(selectedDoctor.getUser_type()).document(selectedDoctor.getUid()).collection("BlockChain").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()){
                    BlockModel blockModel = new BlockModel(doc.getString("aID"),doc.getString("pName"),doc.getString("pUID"),doc.getString("hash"),doc.getString("previousHash"),doc.getString("data"),doc.getString("dName"),doc.getString("dUID"));
                    preBlocks.add(blockModel);
                    deleteDChainFromDB(doc.getId());
                }
                Log.d("TAG" , "Size of fetched data " +preBlocks.size());
                addDBlock();            }
        });
    }
    private void deleteDChainFromDB(final String docID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(selectedDoctor.getUser_type()).document(selectedDoctor.getUid()).collection("BlockChain").document(docID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("TAG" , "Chain is deleted for doc" + docID);
                }
                else {
                    Log.d("TAG", "Chain deletion error with doc " + docID +task.getException());
                }
            }
        });

    }

    private void addDBlock(){
        doctorBlockChain = new Blockchain();
        if (preBlocks.size()==0){
            Log.d("TAG", "If part ");
            Block a = new Block(appoitnmentID, currentUser.getName(),currentUser.getUid(),dateTime.getText().toString(),selectedDoctor.getName(),selectedDoctor.getUid());
            doctorBlockChain.addBlock(a);
        }else {
            Log.d("TAG", "Else part ");
            for (int i = 1; i<preBlocks.size(); i++){
                Log.d("TAG" , "For loop to add pre block " + i);
                Block block = new Block(preBlocks.get(i).getaID(),
                        preBlocks.get(i).getpName(),
                        preBlocks.get(i).getpUID(),
                        preBlocks.get(i).getHash(),
                        preBlocks.get(i).getPreviousHash(),
                        preBlocks.get(i).getData(),
                        preBlocks.get(i).getdName(),
                        preBlocks.get(i).getdUID());
                doctorBlockChain.addPreBlocks(block);
            }
            Log.d("TAG" ,"Is chain valid" + doctorBlockChain.isValid());
            if (doctorBlockChain.isValid().equals("Yes")){
                Block a = new Block(appoitnmentID, currentUser.getName(),currentUser.getUid(),dateTime.getText().toString(),selectedDoctor.getName(),selectedDoctor.getUid());
                doctorBlockChain.addBlock(a);
            }
        }
        doctorBlockResult = doctorBlockChain.isValid();
        storeChainToDatabase();
    }

    private void storeChainToDatabase(){
        dateTime.setText("");

        if (userBlockResult.equals("Yes") && doctorBlockResult.equals("Yes")){
            userBlockChain.storeChain(currentUser.getUser_type(), currentUser.getUid());
            doctorBlockChain.storeChain(selectedDoctor.getUser_type(), selectedDoctor.getUid());
        }else {
            if (!userBlockResult.equals("Yes")){
                Snackbar.make(buttonClickView, "You can't add appointment, Your BlockChain Data is not valid",Snackbar.LENGTH_LONG).show();
            }
            if (!doctorBlockResult.equals("Yes")){
                Snackbar.make(buttonClickView, "You can't add appointment, Your doctor BlockChain Data is not valid",Snackbar.LENGTH_LONG).show();
            }
            if (!userBlockResult.equals("Yes") && !doctorBlockResult.equals("Yes")){
                Snackbar.make(buttonClickView, "You can add appointment, Your Blockchain and yours Doctor BlockChain Data is not valid",Snackbar.LENGTH_LONG).show();
            }
        }
    }

}
