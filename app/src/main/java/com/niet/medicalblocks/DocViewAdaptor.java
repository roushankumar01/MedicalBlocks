package com.niet.medicalblocks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DocViewAdaptor extends ArrayAdapter<ProfileDetails>{
    private ArrayList<ProfileDetails> docList;
    private Context context;
    public DocViewAdaptor(Context context, ArrayList<ProfileDetails> object){
        super(context,0,object);
        this.docList = object;
        this.context = context;
    }
    @Override
    public int getCount() {
        return super.getCount();
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null){
            view = (View) ((Activity)getContext()).getLayoutInflater().inflate(R.layout.custom_doc_view,parent,false);
        }
        ImageView docView = view.findViewById(R.id.doc_image_view);
        TextView docName = (TextView) view.findViewById(R.id.view_doc_name);
        docName.setText(docList.get(position).getDoc_name());

        if (docList.get(position).getImage_url()!=null){
            Uri uri = Uri.parse(docList.get(position).getImage_url());
            Glide.with(context).load(uri).into(docView);
        }else {
            Glide.with(context).load(R.drawable.student_01).into(docView);
        }
        return view;
    }
}
