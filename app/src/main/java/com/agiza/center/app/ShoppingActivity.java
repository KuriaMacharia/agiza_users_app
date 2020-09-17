package com.agiza.center.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

public class ShoppingActivity extends AppCompatActivity {
    ConstraintLayout homeCons, listCons, neighborhoodCons, deliveryCons, servicesCons, categoriesCons;
    TextView backTxt, categoryTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        homeCons=(ConstraintLayout) findViewById(R.id.cons_shoping_home);
        listCons=(ConstraintLayout) findViewById(R.id.cons_list_businesses);
        neighborhoodCons=(ConstraintLayout) findViewById(R.id.cons_neighborhood_shopping);
        deliveryCons=(ConstraintLayout) findViewById(R.id.cons_delivery_shopping);
        servicesCons=(ConstraintLayout) findViewById(R.id.cons_services_shopping);
        categoriesCons=(ConstraintLayout) findViewById(R.id.cons_categories_shopping);
        backTxt=(TextView) findViewById(R.id.txt_back_shopping);
        categoryTxt=(TextView) findViewById(R.id.txt_selected_category_shopping);

        neighborhoodCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeCons.setVisibility(View.GONE);
                listCons.setVisibility(View.VISIBLE);
                categoryTxt.setText("Neighborhood Shops");
            }
        });

        deliveryCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeCons.setVisibility(View.GONE);
                listCons.setVisibility(View.VISIBLE);
                categoryTxt.setText("Express Delivery");
            }
        });

        servicesCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeCons.setVisibility(View.GONE);
                listCons.setVisibility(View.VISIBLE);
                categoryTxt.setText("Neighborhood Service Businesses");
            }
        });

        categoriesCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeCons.setVisibility(View.GONE);
                listCons.setVisibility(View.VISIBLE);
                categoryTxt.setText("Listed Businesses");
            }
        });

        backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeCons.setVisibility(View.VISIBLE);
                listCons.setVisibility(View.GONE);
            }
        });
    }
}
