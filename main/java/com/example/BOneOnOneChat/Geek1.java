package com.example.BOneOnOneChat;


import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.graphics.drawable.Drawable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class Geek1 extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String[]> userDetail;

    public Geek1(Activity context, String[] names,ArrayList<String[]> userDetail) {
        super(context, R.layout.activity_geek1,names);
        this.context=context;
        this.userDetail=userDetail;

    }


    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView=inflater.inflate(R.layout.activity_geek1, null,true);

        TextView titleText = rowView.findViewById(R.id.title1);
        ImageView imageView = rowView.findViewById(R.id.icon1);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_account_circle_24);
        if (drawable != null) {
            drawable = drawable.mutate();
            drawable.setTint(Integer.parseInt("-"+userDetail.get(position)[2]));
        }
        titleText.setText(userDetail.get(position)[0]);
        imageView.setImageDrawable(drawable);
        return rowView;
    }
}