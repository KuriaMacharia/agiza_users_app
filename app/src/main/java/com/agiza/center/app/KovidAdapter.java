package com.agiza.center.app;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class KovidAdapter extends ArrayAdapter<Kovid> {

    private Activity activity;
    private ArrayList<Kovid> data;
    private static LayoutInflater inflater=null;

    HashMap<String, String> song = new HashMap<String, String>();

    public KovidAdapter(Activity a, ArrayList<Kovid> d) {
        super(a, 0, d);
        this.data=d;
    }

    public View getView(final int position, View vi, ViewGroup parent) {

        if(vi == null){
            vi =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.single_kovid_contact_item,parent,false);
        }

        TextView nameTxt = (TextView)vi.findViewById(R.id.txt_police_name_police_list);
        TextView countyTxt = (TextView)vi.findViewById(R.id.txt_county_kovid_item);
        TextView phoneTxt = (TextView)vi.findViewById(R.id.txt_phone_kovid_item);
        Kovid contact = getItem(position);

        nameTxt.setText(contact.getName());
        countyTxt.setText(contact.getCounty());
        phoneTxt.setText(contact.getPhone());

        return vi;
    }

}
