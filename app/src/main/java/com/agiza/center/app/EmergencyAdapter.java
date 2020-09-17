package com.agiza.center.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EmergencyAdapter extends ArrayAdapter<Hospital> {
    private ArrayList<Hospital> data;

    public EmergencyAdapter(Activity a, ArrayList<Hospital> d) {
        super(a, 0, d);
        this.data=d;
    }

    public View getView(final int position, View vi, ViewGroup parent) {
        if(vi == null){
            vi =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.single_emergency_item, parent,false);
        }

        TextView nameTxt = (TextView)vi.findViewById(R.id.txt_police_name_police_list);
        TextView contactTxt = (TextView)vi.findViewById(R.id.txt_police_contact_list);
        TextView latitudeTxt = (TextView)vi.findViewById(R.id.txt_latitude_police_list);
        TextView longitudeTxt = (TextView)vi.findViewById(R.id.txt_longitude_police_list);
        TextView initTxt = (TextView)vi.findViewById(R.id.txt_init_emergency);

        Hospital hospital = getItem(position);

        String thePhone = hospital.getContact() + hospital.getPhone();
        thePhone=thePhone.replace("null", "");

        String letterOne= String.valueOf(hospital.getName().charAt(0));

        nameTxt.setText(hospital.getName());
        contactTxt.setText(thePhone);
        latitudeTxt.setText(hospital.getLatitude());
        longitudeTxt.setText(hospital.getLongitude());
        initTxt.setText(String.valueOf(hospital.getName().charAt(0)));
        return vi;
    }
}
