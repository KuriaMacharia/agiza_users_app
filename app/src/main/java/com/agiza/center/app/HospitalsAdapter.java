package com.agiza.center.app;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HospitalsAdapter extends ArrayAdapter<PoliceStation> {

    private Activity activity;
    private ArrayList<PoliceStation> data;
    private static LayoutInflater inflater=null;

    HashMap<String, String> song = new HashMap<String, String>();

    public HospitalsAdapter(Activity a, ArrayList<PoliceStation> d) {
        super(a, 0, d);
        this.data=d;
    }

    public View getView(final int position, View vi, ViewGroup parent) {

        if(vi == null){
            vi =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.single_hospital_name_item,parent,false);
        }

        TextView nameTxt = (TextView)vi.findViewById(R.id.txt_police_name_police_list);
        TextView contactTxt = (TextView)vi.findViewById(R.id.txt_police_contact_list);
        TextView latitudeTxt = (TextView)vi.findViewById(R.id.txt_latitude_police_list);
        TextView longitudeTxt = (TextView)vi.findViewById(R.id.txt_longitude_police_list);

        PoliceStation hospital = getItem(position);

        nameTxt.setText(hospital.getName());
        contactTxt.setText(hospital.getContact());
        latitudeTxt.setText(hospital.getLatitude());
        longitudeTxt.setText(hospital.getLongitude());


        return vi;
    }

}
