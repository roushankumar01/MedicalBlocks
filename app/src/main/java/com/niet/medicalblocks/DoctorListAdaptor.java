package com.niet.medicalblocks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.MessagePattern;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
public class DoctorListAdaptor extends ArrayAdapter<ProfileDetails> {

    private ArrayList<ProfileDetails> doctorDetails;
    private Context context;
    private ProfileDetails currentUser;
    public DoctorListAdaptor(Context context, ArrayList<ProfileDetails> object, ProfileDetails currentUser){
        super(context,0,object);
        this.doctorDetails = object;
        this.context = context;
        this.currentUser = currentUser;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null){
            view = (View) ((Activity)getContext()).getLayoutInflater().inflate(R.layout.custom_doctor_list,parent,false);
        }
        TextView docName = (TextView) view.findViewById(R.id.textView_name);
        TextView docEmail = (TextView) view.findViewById(R.id.textView_email);
        ImageView docImg = (ImageView) view.findViewById(R.id.imageView);
        docName.setText(doctorDetails.get(position).getName());
        docEmail.setText(doctorDetails.get(position).getEmail());

        if (doctorDetails.get(position).getImage_url()!=null){
            Uri uri = Uri.parse(doctorDetails.get(position).getImage_url());
            Glide.with(context).load(uri).into(docImg);
        }else {
            Glide.with(context).load(R.drawable.student_01).into(docImg);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", " Item Clicked");
                ProfileDetails selectedDoctor = doctorDetails.get(position);
                Log.d("TAG", " Intent Data are " + selectedDoctor.getUid() + currentUser.getUid());
                Log.d("TAG", " Intent Data Selected doctor user type " + selectedDoctor.getUser_type());
                Intent i = new Intent(context, AppointmentDetails.class);
                i.putExtra("uuser_type",currentUser.getUser_type());
                i.putExtra("uname",currentUser.getName());
                i.putExtra("uemail",currentUser.getEmail());
                i.putExtra("uphone",currentUser.getPhone());
                i.putExtra("uaddress",currentUser.getAddress());
                i.putExtra("uuid",currentUser.getUid());
                i.putExtra("duser_type",selectedDoctor.getUser_type());
                i.putExtra("dname",selectedDoctor.getName());
                i.putExtra("demail",selectedDoctor.getEmail());
                i.putExtra("dphone",selectedDoctor.getPhone());
                i.putExtra("daddress",selectedDoctor.getAddress());
                i.putExtra("duid",selectedDoctor.getUid());
                context.startActivity(i);

            }
        });
        return view;
    }
}
