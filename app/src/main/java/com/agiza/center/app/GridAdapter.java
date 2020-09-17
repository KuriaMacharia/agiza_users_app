package com.agiza.center.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GridAdapter extends ArrayAdapter <Item>{

    ArrayList <Item> gridList = new ArrayList<>();

    public GridAdapter(Context context, int textViewResourceId, ArrayList <Item> objects) {
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
        v = inflater.inflate(R.layout.grid_content, null);
        TextView textView = (TextView) v.findViewById(R.id.txt_grid_tem);
        ImageView imageView = (ImageView) v.findViewById(R.id.img_grid_item);

        textView.setText(gridList.get(position).getbirdName());
        imageView.setImageResource(gridList.get(position).getbirdImage());
        return v;

    }

}