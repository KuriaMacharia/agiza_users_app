package com.agiza.center.app;

import android.os.AsyncTask;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.agiza.center.app.Helper.HttpJsonParser;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EstateActivity extends AppCompatActivity {

    private static final String KEY_REGION = "region";
    private static final String KEY_COUNTY = "county";

    private static final String KEY_LISTING_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_BEDROOMS = "bedrooms";
    private static final String KEY_BATHROOMS = "bathrooms";
    private static final String KEY_PRICE = "price";

    private static final String KEY_ADDRESS = "address";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";

    private static final String FETCH_LISTINGS_URL = "http://www.agizakenya.com/agiza/";

    ImageView logoImg;
    TextView providerTxt, mantraTxt, regionTxt, rentCountTxt, buyCountTxt, rentListTxt, rentMapTxt, buyListTxt, buyMapTxt;
    RadioGroup buyRentGroup, buyGroup;
    ConstraintLayout rentCons, buyCons, rentMapCons, buyMapCons;
    Spinner typeSpin, bedroomSpin, priceSpin;
    ListView rentList, buyList;
    private ArrayList<HashMap<String, String>> estateList;

    String listingId;
    int success;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estate);

        logoImg=(ImageView) findViewById(R.id.img_logo_estate);
        providerTxt=(TextView) findViewById(R.id.txt_provider_estate);
        mantraTxt=(TextView) findViewById(R.id.txt_mantra_estate);
        regionTxt=(TextView) findViewById(R.id.txt_region_estate);
        rentCountTxt=(TextView) findViewById(R.id.txt_rent_count_estate);
        buyCountTxt=(TextView) findViewById(R.id.txt_buy_count_estate);
        rentListTxt=(TextView) findViewById(R.id.txt_rent_list_estate);
        rentMapTxt=(TextView) findViewById(R.id.txt_rent_map_estate);
        buyListTxt=(TextView) findViewById(R.id.txt_buy_list_estate);
        buyMapTxt=(TextView) findViewById(R.id.txt_buy_map_estate);

        buyRentGroup=(RadioGroup) findViewById(R.id.group_buy_rent_estate);
        buyGroup=(RadioGroup) findViewById(R.id.group_buy_estate);
        rentCons=(ConstraintLayout) findViewById(R.id.cons_rent_estate);
        buyCons=(ConstraintLayout) findViewById(R.id.cons_buy_estate);
        rentMapCons=(ConstraintLayout) findViewById(R.id.cons_rent_map_estate);
        buyMapCons=(ConstraintLayout) findViewById(R.id.cons_buy_map_estate);

        typeSpin=(Spinner) findViewById(R.id.spin_type_estate);
        bedroomSpin=(Spinner) findViewById(R.id.spin_bedrooms_estate);
        priceSpin=(Spinner) findViewById(R.id.spin_price_estate);
        rentList=(ListView) findViewById(R.id.list_rent_estate);
        buyList=(ListView) findViewById(R.id.list_buy_estate);

        estateList = new ArrayList<>();

        SupportMapFragment rentMap = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rent_map);
        SupportMapFragment buyMap = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.buy_map);

        new FetchListings().execute();

        buyRentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.radio_rent){
                    rentCons.setVisibility(View.VISIBLE);
                    buyCons.setVisibility(View.GONE);
                }else if(i==R.id.radio_buy){
                    rentCons.setVisibility(View.GONE);
                    buyCons.setVisibility(View.VISIBLE);
                }else{
                    rentCons.setVisibility(View.VISIBLE);
                    buyCons.setVisibility(View.GONE);
                }
            }
        });

        rentListTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rentList.setVisibility(View.VISIBLE);
                rentMapCons.setVisibility(View.GONE);
            }
        });

        rentMapTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rentList.setVisibility(View.GONE);
                rentMapCons.setVisibility(View.VISIBLE);
            }
        });

        buyListTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyList.setVisibility(View.VISIBLE);
                buyMapCons.setVisibility(View.GONE);
            }
        });

        buyMapTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyList.setVisibility(View.GONE);
                buyMapCons.setVisibility(View.VISIBLE);
            }
        });
    }

    private class FetchListings extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    FETCH_LISTINGS_URL + "fetch_listings.php", "GET", null);

            try {
                success = jsonObject.getInt(KEY_SUCCESS);
                if (success == 1) {
                    JSONArray incidences = jsonObject.getJSONArray(KEY_DATA);
                    for (int i = 0; i < incidences.length(); i++) {
                        JSONObject incidence = incidences.getJSONObject(i);

                        Integer listingId = incidence.getInt(KEY_LISTING_ID);
                        String Category = incidence.getString(KEY_CATEGORY);
                        String Type = incidence.getString(KEY_TYPE);
                        String Price = incidence.getString(KEY_PRICE);
                        String Bedrooms = incidence.getString(KEY_BEDROOMS);
                        String Bathrooms = incidence.getString(KEY_BATHROOMS);
                        String Address = incidence.getString(KEY_ADDRESS);
                        String Latitude = incidence.getString(KEY_LATITUDE);
                        String Longitude = incidence.getString(KEY_LONGITUDE);

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_LISTING_ID, listingId.toString());
                        map.put(KEY_CATEGORY, Category);
                        map.put(KEY_TYPE, Type);
                        map.put(KEY_PRICE, Price);
                        map.put(KEY_BEDROOMS, Bedrooms);
                        map.put(KEY_BATHROOMS, Bathrooms);
                        map.put(KEY_ADDRESS, Address);
                        map.put(KEY_LATITUDE, Latitude);
                        map.put(KEY_LONGITUDE, Longitude);
                        estateList.add(map);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            runOnUiThread(new Runnable() {
                public void run() {
                    populateIncidenceList();
                    rentCountTxt.setText(String.valueOf(estateList.size()));
                }
            });
        }

    }
    /**
     * Updating parsed JSON data into ListView
     * */
    private void populateIncidenceList() {
        ListAdapter adapter = new SimpleAdapter(
                EstateActivity.this, estateList, R.layout.single_estate, new String[]{KEY_LISTING_ID, KEY_ADDRESS, KEY_PRICE, KEY_CATEGORY, KEY_TYPE,
                KEY_BEDROOMS, KEY_BATHROOMS},
                new int[]{R.id.txt_id, R.id.txt_address, R.id.txt_price, R.id.txt_category, R.id.txt_type, R.id.txt_bedrooms,
                R.id.txt_bathrooms});
        rentList.setAdapter(adapter);

        /*businessListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Check for network connectivity
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    String licenseNumber = ((TextView) view.findViewById(R.id.edit_contact_number_search))
                            .getText().toString();
                    Intent intent = new Intent(getApplicationContext(),
                            SingleBusinessActivity.class);
                    intent.putExtra(KEY_LICENSE_NUMBER, licenseNumber);
                    startActivityForResult(intent, 20);

                } else {
                    Toast.makeText(LicenseValidActivity.this,
                            "Unable to connect to internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });*/
    }
}
