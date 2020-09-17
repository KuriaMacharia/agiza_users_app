package com.agiza.center.app;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    public PlaceAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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
            vi = inflater.inflate(R.layout.single_place_item, null);

        TextView addressTxt = (TextView)vi.findViewById(R.id.txt_address_my_place);
        TextView flagTxt = (TextView)vi.findViewById(R.id.txt_date_place);

        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);

        addressTxt.setText(song.get(PlacesActivity.KEY_ADDRESS_FLAG));
        flagTxt.setText(song.get(PlacesActivity.KEY_FLAG));
        return vi;

    }
}

/**/