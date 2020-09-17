package com.agiza.center.app;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OtherAmbulanceAdapter extends ArrayAdapter<Ambulance> {

    private Activity activity;
    private ArrayList<Ambulance> data;
    private static LayoutInflater inflater=null;
    private OtherAmbulanceCallBack callback;

    HashMap<String, String> song = new HashMap<String, String>();

    public OtherAmbulanceAdapter(Activity a, ArrayList<Ambulance> d) {
        super(a, 0, d);
        this.data=d;
    }

    public View getView(final int position, View vi, ViewGroup parent) {

        if(vi == null){
            vi =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.single_ambulance_item,parent,false);
        }

        ImageView callImg=(ImageView) vi.findViewById(R.id.img_call_ambulance_item);
        TextView nameTxt = (TextView)vi.findViewById(R.id.txt_ambulance_name_item);
        TextView contactTxt = (TextView)vi.findViewById(R.id.txt_contact_ambulance_item);

        Ambulance incidence = getItem(position);

        nameTxt.setText(incidence.getName());
        contactTxt.setText(incidence.getContact());

        /*callImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.CallAmbulance(position);
            }
        });*/

        return vi;
    }

    public void setCallback(OtherAmbulanceCallBack callback){

        this.callback = callback;
    }

    public interface OtherAmbulanceCallBack {
        public void CallAmbulance (int position);
    }

}
