package com.niet.medicalblocks;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AppointDetailsAdaptor extends ArrayAdapter<BlockModel> {
    private Context context;
    private ArrayList<BlockModel> appointmentDetailsList;

    public AppointDetailsAdaptor(Context context, ArrayList<BlockModel> object){
        super(context,0,object);
        this.appointmentDetailsList = object;
        this.context = context;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position,  View view,  ViewGroup parent) {

        if (view == null){
            view = (View) ((Activity)getContext()).getLayoutInflater().inflate(R.layout.custom_appointment_list,parent,false);
        }
        TextView doctorName = view.findViewById(R.id.doctor_name);
        TextView patientName = view.findViewById(R.id.patient_name);
        TextView appointmentId = view.findViewById(R.id.appointment_id);
        TextView timeDate = view.findViewById(R.id.time_date);
        BlockModel appointmentDetails = appointmentDetailsList.get(position);
        appointmentId.setText(appointmentDetails.getaID());
        doctorName.setText(appointmentDetails.getdName());
        patientName.setText(appointmentDetails.getpName());
        timeDate.setText(appointmentDetails.getData());
        return view;
    }
}
