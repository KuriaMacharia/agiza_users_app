package com.agiza.center.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GridPlacesAdapter extends ArrayAdapter <ItemAddress>{

    ArrayList <ItemAddress> gridList = new ArrayList<>();

    public GridPlacesAdapter(Context context, int textViewResourceId, ArrayList <ItemAddress> objects) {
        super(context, textViewResourceId, objects);
        this.gridList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.grid_address, null);
        TextView textView = (TextView) v.findViewById(R.id.txt_ownership_address);

        textView.setText(gridList.get(position).getbirdName());
        return v;

    }

}
