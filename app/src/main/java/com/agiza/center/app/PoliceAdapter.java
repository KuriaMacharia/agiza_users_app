package com.agiza.center.app;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PoliceAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    public PoliceAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.single_police_item, null);


        TextView policeNameTxt = (TextView)vi.findViewById(R.id.txt_police_name_police_list);
        TextView policeContactTxt = (TextView)vi.findViewById(R.id.txt_police_contact_list);
        TextView distanceTxt = (TextView)vi.findViewById(R.id.txt_distance_police_list);
        TextView timeTxt = (TextView)vi.findViewById(R.id.txt_time_police_list);
        TextView latitudeTxt = (TextView)vi.findViewById(R.id.txt_latitude_police_list);
        TextView longitudeTxt = (TextView)vi.findViewById(R.id.txt_longitude_police_list);

        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);

        policeNameTxt.setText(song.get(PoliceActivity.KEY_POLICE_STATION_NAME));
        policeContactTxt.setText(song.get(PoliceActivity.KEY_POLICE_NUMBER));
        distanceTxt.setText(song.get(PoliceActivity.TAG_DISTANCE));
        timeTxt.setText(song.get(PoliceActivity.TAG_DISTANCE_VALUE));
        latitudeTxt.setText(song.get(PoliceActivity.KEY_POLICE_LATITUDE));
        longitudeTxt.setText((song.get(PoliceActivity.KEY_POLICE_LONGITUDE)));

        return vi;

    }
}

