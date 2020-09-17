package com.agiza.center.app;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlacesAdapter extends ArrayAdapter<Place> {

    private ArrayList<Place> data;

    public PlacesAdapter(Activity a, ArrayList<Place> d) {
        super(a, 0, d);
        this.data=d;
    }

    public View getView(final int position, View vi, ViewGroup parent) {
        if(vi == null){
            vi =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.single_place_item,parent,false);
        }

        TextView nameTxt = (TextView)vi.findViewById(R.id.txt_address_my_place);
        Place hospital = getItem(position);
        nameTxt.setText(hospital.getAddress());
        return vi;
    }
}

