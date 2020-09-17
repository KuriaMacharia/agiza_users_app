package com.agiza.center.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceFilterAdapter extends ArrayAdapter<Place> {

    private Activity activity;
    private ArrayList<Place> data;
    private static LayoutInflater inflater=null;
    String incidenceNumber;

    public PlaceFilterAdapter(Activity a, ArrayList<Place> d) {
        super(a, 0, d);
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).getAddress().contentEquals("No Name")) {
            return 0;
        }else{
            return 1;
        }
    }

    public int getCount() {
        return data.size();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi=convertView;
        int type = getItemViewType(position);
        if(convertView==null)
            if(type ==0){
                vi = inflater.inflate(R.layout.single_place_item, null);
            }else{
                vi = inflater.inflate(R.layout.single_place_item, null);
            }

        TextView addressTxt = (TextView)vi.findViewById(R.id.txt_address_my_place);
        TextView dateTxt = (TextView)vi.findViewById(R.id.txt_date_place);

        final Place place = getItem(position);

        if(type==0){

            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
            final String date=dateFormat.format(place.getTime());

            addressTxt.setText(place.getAddress());
            dateTxt.setText(date);

        }else {
            //final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
            final String date=dateFormat.format(place.getTime());

            addressTxt.setText(place.getAddress());
            dateTxt.setText(date);
        }
        return vi;
    }
}
