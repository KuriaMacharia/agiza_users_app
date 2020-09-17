package com.agiza.center.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.center.agiza.Helper.CheckNetWorkStatus;
import com.center.agiza.Helper.HttpJsonParser;
import com.center.agiza.MyView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExploreActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String KEY_DATA = "data";
    private static final String KEY_SUCCESS = "success";

    private static final String KEY_ID = "id";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_MYID = "myid";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_TYPE = "type";
    private static final String KEY_PRICE = "price";
    private static final String KEY_MIN_PRICE = "min_price";
    private static final String KEY_MAX_PRICE = "max_price";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_WEBSITE = "website";
    private static final String KEY_DELIVERY = "delivery";
    private static final String KEY_REGION = "region";
    private static final String KEY_COUNTY = "county";

    private static final String BASE_URL = "http://anwani.net/seya/";
    private static final String BASE_URL_PLACE = "http://www.agizakenya.com/agiza/";

    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String myid1 = "myidKey";

    SharedPreferences sharedpreferences;

    String[] category ={"---Select---","Health","Shop and Supermarkets", "Money Transfer","Food, Cereals and Groceries","Technology",
            "Hospitality and Accommodation", "Beauty and Fashion", "Transport and Logistics"};
    String[] health ={"---Select---","Chemist", "Clinic", "Pharmacy","Laboratory","Hospital","Other"};
    String[] shop = {"---Select---","General Shop","Wholesale Shop","Distributor","Other"};
    String[] money = {"---Select---","M-Pesa","Equity Agent","KCB Mtaani","Coop Agent","Airtel Money","Other"};
    String[] food ={"---Select---","Fast Food", "Restaurant","Bar and Grill","Hotel","Fruits","Other"};
    String[] technology = {"---Select---","Electronics Shop","Electronic Repair","Phones Sale","Computer Sales","Other"};
    String[] transport ={"---Select---","Car Wash","Garage","Spare Parts Shop","Vehicles Yard","Other"};
    String[] hospitality = {"---Select---","Hotel","Leisure Park","Phones Sale","Computer Sales","Other"};
    String[] fashion ={"---Select---","Salon","Kinyozi","Cosmetics Shop","Other"};
    String[] other = {"---Select---","Other"};
    String[] selCat= { };
    String[] homeSelCat= { };

    String[] county ={"---Select---","Nairobi City County","Kiambu County"};
    String[] kiambu = {"---Select---","Ruaka", "Kiambu Town"};
    String[] nairobi = {"---Select---","Parklands", "Ngara", "CBD"};
    String[] selCounty= { };


    String theAddress, selectedCategory, selectedType, typeSelected, Name, Phone, Price, minPrice, maxPrice, myEmail, myPhone, myid,
            homeSelectedCategory, homeSelectedType, County, Region, selectedCounty, selectedRegion, Delivery;
    TextView exploreTxt, placesTxt, addressTxt, myPlacesCountTxt, allPlacesCountTxt;
    ConstraintLayout consHome, consPlaces, consList, consAddBtn, consAddExplore, consResultHome;
    Button cancelBtn, addBtn;
    ScrollView addScroll;
    ImageView clearImg;
    EditText nameEdt, contactEdt,  priceEdt, minEdt, maxEdt, emailEdt, websiteEdt;
    ListView myPlacesList, placesList;
    AutoCompleteTextView searchEdt, homeSearchEdt;
    Spinner categorySpin, typeSpin, homeCategorySpin, homeTypeSpin, countySpin, regionSpin;
    ArrayList<String> autoList;
    int success;
    ArrayList<HashMap<String, String>> listAllPlaces, listMyPlaces, listFilteredPlaces, listFinalFilter;
    RadioGroup deliveryGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        myEmail = sharedpreferences.getString(Email1,"");
        myPhone = sharedpreferences.getString(phoneNumber1,"");
        myid = sharedpreferences.getString(myid1,"");

        autoList= new ArrayList<>();
        listAllPlaces = new ArrayList<>();
        listMyPlaces =new ArrayList<>();
        listFilteredPlaces =new ArrayList<>();
        listFinalFilter =new ArrayList<>();

        Delivery="No Delivery";

        exploreTxt=(TextView) findViewById(R.id.txt_explore);
        placesList=(ListView) findViewById(R.id.list_results_explore);
        myPlacesList=(ListView) findViewById(R.id.list_my_places_explore);
        placesTxt=(TextView) findViewById(R.id.txt_places);
        myPlacesCountTxt=(TextView) findViewById(R.id.txt_my_places_explore_count);
        allPlacesCountTxt=(TextView) findViewById(R.id.txt_count_all_places_explore);
        consHome=(ConstraintLayout) findViewById(R.id.cons_explore_home);
        consPlaces=(ConstraintLayout) findViewById(R.id.cons_explore_my_places_full);
        consList=(ConstraintLayout) findViewById(R.id.cons_explore_my_places);
        consAddBtn=(ConstraintLayout) findViewById(R.id.cons_explore_add_button);
        consAddExplore=(ConstraintLayout) findViewById(R.id.cons_explore_add_business);
        consResultHome=(ConstraintLayout) findViewById(R.id.cons_explore_region_home);
        addScroll=(ScrollView) findViewById(R.id.scroll_explore_add_business);
        cancelBtn=(Button) findViewById(R.id.btn_cancel_explore);
        addBtn=(Button) findViewById(R.id.btn_add_explore);
        clearImg=(ImageView) findViewById(R.id.img_clear_search_add);
        searchEdt = (AutoCompleteTextView) findViewById(R.id.edt_search_add);
        homeSearchEdt= (AutoCompleteTextView) findViewById(R.id.edt_search);
        addressTxt=(TextView) findViewById(R.id.txt_addres_explore_add);
        nameEdt=(EditText) findViewById(R.id.edt_name_explore_add);
        contactEdt=(EditText) findViewById(R.id.edt_contact_explore_add);
        priceEdt=(EditText) findViewById(R.id.edt_price_explore_add);
        minEdt=(EditText) findViewById(R.id.edt_min_price_explore_add);
        maxEdt=(EditText) findViewById(R.id.edt_max_price_explore_add);
        emailEdt=(EditText) findViewById(R.id.edt_email_explore_add);
        websiteEdt=(EditText) findViewById(R.id.edt_website_explore_add);
        categorySpin=(Spinner) findViewById(R.id.spin_category_explore_add);
        typeSpin=(Spinner) findViewById(R.id.spin_type_explore_add);
        homeCategorySpin=(Spinner) findViewById(R.id.spin_category_explore_home);
        homeTypeSpin=(Spinner) findViewById(R.id.spin_type_explore_home);
        regionSpin=(Spinner) findViewById(R.id.spin_region_explore);
        countySpin=(Spinner) findViewById(R.id.spin_county_explore);
        deliveryGroup=(RadioGroup) findViewById(R.id.group_delivery_explore_add);

        new AutoNames().execute();
        new FetchAllPlaces().execute();

        homeCategorySpin.setOnItemSelectedListener(this);
        countySpin.setOnItemSelectedListener(this);
        categorySpin.setOnItemSelectedListener(this);
        ArrayAdapter ct = new ArrayAdapter(ExploreActivity.this, android.R.layout.simple_spinner_item, category);
        ct.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpin.setAdapter(ct);
        homeCategorySpin.setAdapter(ct);

        ArrayAdapter cnt = new ArrayAdapter(ExploreActivity.this, android.R.layout.simple_spinner_item, county);
        cnt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countySpin.setAdapter(cnt);

        exploreTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consHome.setVisibility(View.VISIBLE);
                consPlaces.setVisibility(View.GONE);

                exploreTxt.setBackgroundColor(getResources().getColor(R.color.orange_faded));
                exploreTxt.setTextColor(getResources().getColor(R.color.places_dark_brown));
                placesTxt.setBackgroundColor(getResources().getColor(R.color.places_brown));
                placesTxt.setTextColor(getResources().getColor(R.color.white));
            }
        });

        placesTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consHome.setVisibility(View.GONE);
                consPlaces.setVisibility(View.VISIBLE);
                new FetchMyPlaces().execute();

                placesTxt.setBackgroundColor(getResources().getColor(R.color.orange_faded));
                placesTxt.setTextColor(getResources().getColor(R.color.places_dark_brown));
                exploreTxt.setBackgroundColor(getResources().getColor(R.color.places_brown));
                exploreTxt.setTextColor(getResources().getColor(R.color.white));
            }
        });

        consAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consList.setVisibility(View.GONE);
                consAddExplore.setVisibility(View.VISIBLE);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consList.setVisibility(View.VISIBLE);
                consAddExplore.setVisibility(View.GONE);
                addScroll.setVisibility(View.GONE);
            }
        });

        searchEdt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (CheckNetWorkStatus.isNetworkAvailable(ExploreActivity.this)) {
                    addScroll.setVisibility(View.VISIBLE);
                    addressTxt.setText(searchEdt.getText().toString());
                    String fullAddress=(searchEdt.getText().toString());
                    County= fullAddress.substring(fullAddress.lastIndexOf(',') +1).trim();

                    String [] Parts = fullAddress.split(",");
                    Region =Parts[1];
                    Region=Region.substring(1);
                }else{
                    Toast.makeText(ExploreActivity.this, "Unable to connect to internet", Toast.LENGTH_LONG).show();
                }
            }
        });

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()>0){
                    clearImg.setVisibility(View.VISIBLE);
                }else{
                    clearImg.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        clearImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEdt.setText("");
                addressTxt.setText("");
                clearImg.setVisibility(View.GONE);
                addScroll.setVisibility(View.GONE);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!typeSelected.isEmpty()){
                    new AddPlace().execute();
                }else {
                    Toast.makeText(ExploreActivity.this,"Select Type",Toast.LENGTH_LONG).show();
                }
            }
        });

        deliveryGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio_offers_delivery_explore_add:
                        Delivery="Offers Delivery";
                        break;

                    case R.id.radio_no_delivery_explore_add:
                        Delivery="No Delivery";
                        break;
                }
            }
        });

        countySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCounty = String.valueOf(countySpin.getSelectedItem());
                if(!selectedCounty.isEmpty()){

                    if(selectedCounty.contentEquals("---Select---")) {
                        regionSpin.setVisibility(View.GONE);
                    }else if(selectedCounty.contentEquals("Nairobi City County")){
                        selCounty=nairobi;
                        regionSpin.setVisibility(View.VISIBLE);
                    }else if(selectedCounty.contentEquals("Kiambu County")){
                        selCounty=kiambu;
                        regionSpin.setVisibility(View.VISIBLE);
                    }else{
                        regionSpin.setVisibility(View.GONE);
                    }

                    ArrayAdapter scsc = new ArrayAdapter(ExploreActivity.this, android.R.layout.simple_spinner_item, selCounty);
                    scsc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    regionSpin.setAdapter(scsc);

                    regionSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            selectedRegion = String.valueOf(regionSpin.getSelectedItem());
                            if(!selectedRegion.contentEquals("---Select---")) {
                                consResultHome.setVisibility(View.VISIBLE);
                                /*listFilteredPlaces.clear();
                                for (int j = 0; j < listAllPlaces.size(); j++) {
                                    if(listAllPlaces.get(j).get(KEY_COUNTY).contentEquals(selectedCounty) &&
                                            listAllPlaces.get(j).get(KEY_REGION).contentEquals(selectedRegion)){
                                        listFilteredPlaces.add(listAllPlaces.get(j));
                                    }

                                    ProcessPlacesList();
                                }*/
                            }else{
                                selectedRegion="";
                                consResultHome.setVisibility(View.GONE);
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        homeCategorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                homeSelectedCategory = String.valueOf(homeCategorySpin.getSelectedItem());
                if(!homeSelectedCategory.isEmpty()){

                    if(homeSelectedCategory.contentEquals("---Select---")) {
                        homeTypeSpin.setVisibility(View.GONE);
                    }else if(homeSelectedCategory.contentEquals("Health")){
                        homeSelCat=health;
                        homeTypeSpin.setVisibility(View.VISIBLE);
                    }else if(homeSelectedCategory.contentEquals("Shop and Supermarkets")){
                        homeSelCat=shop;
                        homeTypeSpin.setVisibility(View.VISIBLE);
                    }else if(homeSelectedCategory.contentEquals("Money Transfer")){
                        homeSelCat=money;
                        homeTypeSpin.setVisibility(View.VISIBLE);
                    }else if(homeSelectedCategory.contentEquals("Food, Cereals and Groceries")){
                        homeSelCat=food;
                        homeTypeSpin.setVisibility(View.VISIBLE);
                    }else if(homeSelectedCategory.contentEquals("Technology")){
                        homeSelCat=technology;
                        homeTypeSpin.setVisibility(View.VISIBLE);
                    }else if(homeSelectedCategory.contentEquals("Hospitality and Accommodation")){
                        homeSelCat=hospitality;
                        homeTypeSpin.setVisibility(View.VISIBLE);
                    }else if(homeSelectedCategory.contentEquals("Beauty and Fashion")){
                        homeSelCat=fashion;
                        homeTypeSpin.setVisibility(View.VISIBLE);
                    }else if(homeSelectedCategory.contentEquals("Transport")){
                        homeSelCat=transport;
                        homeTypeSpin.setVisibility(View.VISIBLE);
                    }else if(homeSelectedCategory.contentEquals("Other")){
                        homeSelCat=other;
                    }else{
                    }

                    ArrayAdapter scsc = new ArrayAdapter(ExploreActivity.this, android.R.layout.simple_spinner_item, homeSelCat);
                    scsc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    homeTypeSpin.setAdapter(scsc);

                    homeTypeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            homeSelectedType = String.valueOf(homeTypeSpin.getSelectedItem());
                            if(!homeSelectedType.contentEquals("---Select---")) {
                                listFilteredPlaces.clear();
                                for (int j = 0; j < listAllPlaces.size(); j++) {
                                    if(listAllPlaces.get(j).get(KEY_COUNTY).contentEquals(selectedCounty) &&
                                            listAllPlaces.get(j).get(KEY_REGION).contentEquals(selectedRegion)&&
                                            listAllPlaces.get(j).get(KEY_CATEGORY).contentEquals(homeSelectedCategory) &&
                                            listAllPlaces.get(j).get(KEY_TYPE).contentEquals(homeSelectedType)){

                                        listFilteredPlaces.add(listAllPlaces.get(j));
                                    }

                                    //ProcessPlacesList();
                                }
                                ProcessPlacesList();
                            }else{
                                //typeSelected=selectedType;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedCategory = String.valueOf(categorySpin.getSelectedItem());

        if(!selectedCategory.isEmpty()){

            if(selectedCategory.contentEquals("---Select---")) {
                typeSpin.setVisibility(View.GONE);
                deliveryGroup.setVisibility(View.GONE);
            }else if(selectedCategory.contentEquals("Health")){
                selCat=health;
                typeSpin.setVisibility(View.VISIBLE);
                deliveryGroup.setVisibility(View.GONE);
            }else if(selectedCategory.contentEquals("Shop and Supermarkets")){
                selCat=shop;
                typeSpin.setVisibility(View.VISIBLE);
                deliveryGroup.setVisibility(View.VISIBLE);
            }else if(selectedCategory.contentEquals("Money Transfer")){
                selCat=money;
                typeSpin.setVisibility(View.VISIBLE);
                deliveryGroup.setVisibility(View.GONE);
            }else if(selectedCategory.contentEquals("Food, Cereals and Groceries")){
                selCat=food;
                typeSpin.setVisibility(View.VISIBLE);
                deliveryGroup.setVisibility(View.VISIBLE);
            }else if(selectedCategory.contentEquals("Technology")){
                selCat=technology;
                typeSpin.setVisibility(View.VISIBLE);
                deliveryGroup.setVisibility(View.VISIBLE);
            }else if(selectedCategory.contentEquals("Hospitality and Accommodation")){
                selCat=hospitality;
                typeSpin.setVisibility(View.VISIBLE);
                deliveryGroup.setVisibility(View.GONE);
            }else if(selectedCategory.contentEquals("Beauty and Fashion")){
                selCat=fashion;
                typeSpin.setVisibility(View.VISIBLE);
                deliveryGroup.setVisibility(View.VISIBLE);
            }else if(selectedCategory.contentEquals("Transport")){
                selCat=transport;
                typeSpin.setVisibility(View.VISIBLE);
                deliveryGroup.setVisibility(View.VISIBLE);
            }else if(selectedCategory.contentEquals("Other")){
                selCat=other;
            }else{
            }

            ArrayAdapter scsc = new ArrayAdapter(ExploreActivity.this, android.R.layout.simple_spinner_item, selCat);
            scsc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            typeSpin.setAdapter(scsc);

            typeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedType = String.valueOf(typeSpin.getSelectedItem());
                    if(!selectedType.contentEquals("---Select---")) {
                            typeSelected=selectedType;
                        }else{
                            typeSelected=selectedType;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }



    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class AutoNames extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "fetch_addresses.php", "GET", null);
            try {
                success = jsonObject.getInt(KEY_SUCCESS);
                if (success == 1) {
                    JSONArray incidences = jsonObject.getJSONArray(KEY_DATA);
                    for (int i = 0; i < incidences.length(); i++) {
                        JSONObject incidence = incidences.getJSONObject(i);
                        theAddress = incidence.getString(KEY_ADDRESS);
                        autoList.add(theAddress);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            if (success == 1) {
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ExploreActivity.this, android.R.layout.simple_dropdown_item_1line, autoList);
                searchEdt.setAdapter(dataAdapter);
                homeSearchEdt.setAdapter(dataAdapter);
            } else {

            }
        }
    }

    private class AddPlace extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            httpParams.put(KEY_MYID, myid);
            httpParams.put(KEY_ADDRESS, theAddress);
            httpParams.put(KEY_NAME, nameEdt.getText().toString());
            httpParams.put(KEY_PHONE, contactEdt.getText().toString());
            httpParams.put(KEY_CATEGORY, selectedCategory);
            httpParams.put(KEY_TYPE, typeSelected);
            httpParams.put(KEY_PRICE, priceEdt.getText().toString());
            httpParams.put(KEY_MIN_PRICE, minEdt.getText().toString());
            httpParams.put(KEY_MAX_PRICE, maxEdt.getText().toString());

            httpParams.put(KEY_EMAIL, emailEdt.getText().toString());
            httpParams.put(KEY_WEBSITE, websiteEdt.getText().toString());
            httpParams.put(KEY_DELIVERY, Delivery);
            httpParams.put(KEY_REGION, Region);
            httpParams.put(KEY_COUNTY, County);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL_PLACE + "add_place.php", "POST", httpParams);
            if(success==1)
                try {
                    success = jsonObject.getInt(KEY_SUCCESS);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }

        protected void onPostExecute(String result) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (success == 1) {
                        Toast.makeText(ExploreActivity.this,"Successfully Added",Toast.LENGTH_LONG).show();
                        consList.setVisibility(View.VISIBLE);
                        consAddExplore.setVisibility(View.GONE);
                        addScroll.setVisibility(View.GONE);

                        startActivity(new Intent(ExploreActivity.this, ExploreActivity.class));

                    } else {
                        Toast.makeText(ExploreActivity.this,"Adding Fail",Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    private class FetchMyPlaces extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            httpParams.put(KEY_MYID, myid);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL_PLACE + "fetch_individual_places.php", "GET", httpParams);
            try {
                success = jsonObject.getInt(KEY_SUCCESS);
                if (success == 1) {
                    JSONArray incidences = jsonObject.getJSONArray(KEY_DATA);
                    for (int i = 0; i < incidences.length(); i++) {
                        JSONObject incidence = incidences.getJSONObject(i);

                        String Id = incidence.getString(KEY_ID);
                        String placeName = incidence.getString(KEY_NAME);
                        String Address = incidence.getString(KEY_ADDRESS);
                        String Contact = incidence.getString(KEY_PHONE);
                        String Category = incidence.getString(KEY_CATEGORY);
                        String Type = incidence.getString(KEY_TYPE);
                        String Price = incidence.getString(KEY_PRICE);
                        String miniPrice = incidence.getString(KEY_MIN_PRICE);
                        String maxiPrice = incidence.getString(KEY_MAX_PRICE);
                        String theCounty = incidence.getString(KEY_COUNTY);
                        String theRegion = incidence.getString(KEY_REGION);


                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_ID, Id);
                        map.put(KEY_NAME, placeName);
                        map.put(KEY_ADDRESS, Address);
                        map.put(KEY_PHONE, Contact);
                        map.put(KEY_CATEGORY, Category);
                        map.put(KEY_TYPE, Type);
                        map.put(KEY_PRICE, Price);
                        map.put(KEY_MIN_PRICE, miniPrice);
                        map.put(KEY_MAX_PRICE, maxiPrice);
                        map.put(KEY_COUNTY, theCounty);
                        map.put(KEY_REGION, theRegion);
                        listMyPlaces.add(map);
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
                    myPlacesCountTxt.setText("My Places -:" + String.valueOf(listMyPlaces.size()));
                }
            });
        }
    }
    /**
     * Updating parsed JSON data into ListView
     * */
    private void populateIncidenceList() {
        ListAdapter adapter = new SimpleAdapter(
                ExploreActivity.this, listMyPlaces, R.layout.single_explore_places_item,
                new String[]{KEY_ID, KEY_NAME, KEY_ADDRESS, KEY_PHONE, KEY_CATEGORY, KEY_TYPE, KEY_PRICE, KEY_MIN_PRICE, KEY_MAX_PRICE},
                new int[]{R.id.txt_id_explore_item, R.id.txt_place_item, R.id.txt_address_explore_item, R.id.txt_phone_explore_item,
                        R.id.txt_category, R.id.txt_type_explore_item, R.id.txt_price, R.id.txt_min_price_explore_item, R.id.txt_max_price_explore_item});
        myPlacesList.setAdapter(adapter);
    }

    private class FetchAllPlaces extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL_PLACE + "fetch_all_places.php", "GET", httpParams);
            try {
                success = jsonObject.getInt(KEY_SUCCESS);
                if (success == 1) {
                    JSONArray incidences = jsonObject.getJSONArray(KEY_DATA);
                    for (int i = 0; i < incidences.length(); i++) {
                        JSONObject incidence = incidences.getJSONObject(i);

                        String Id = incidence.getString(KEY_ID);
                        String placeName = incidence.getString(KEY_NAME);
                        String Address = incidence.getString(KEY_ADDRESS);
                        String Contact = incidence.getString(KEY_PHONE);
                        String Category = incidence.getString(KEY_CATEGORY);
                        String Type = incidence.getString(KEY_TYPE);
                        String Price = incidence.getString(KEY_PRICE);
                        String miniPrice = incidence.getString(KEY_MIN_PRICE);
                        String maxiPrice = incidence.getString(KEY_MAX_PRICE);
                        String theCounty = incidence.getString(KEY_COUNTY);
                        String theRegion = incidence.getString(KEY_REGION);


                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_ID, Id);
                        map.put(KEY_NAME, placeName);
                        map.put(KEY_ADDRESS, Address);
                        map.put(KEY_PHONE, Contact);
                        map.put(KEY_CATEGORY, Category);
                        map.put(KEY_TYPE, Type);
                        map.put(KEY_PRICE, Price);
                        map.put(KEY_MIN_PRICE, miniPrice);
                        map.put(KEY_MAX_PRICE, maxiPrice);
                        map.put(KEY_COUNTY, theCounty);
                        map.put(KEY_REGION, theRegion);
                        listAllPlaces.add(map);
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
                    //populateAllList();
                    allPlacesCountTxt.setText("My Places -:" + String.valueOf(listAllPlaces.size()));
                }
            });
        }
    }
    /**
     * Updating parsed JSON data into ListView
     * */
    private void ProcessPlacesList() {
        Set<HashMap<String, String>> set = new HashSet<>(listFilteredPlaces);
        listFilteredPlaces.clear();
        listFilteredPlaces.addAll(set);
        ListAdapter adapter = new SimpleAdapter(

                ExploreActivity.this, listFilteredPlaces, R.layout.single_explore_all_places_item,
                new String[]{KEY_ID, KEY_NAME, KEY_ADDRESS, KEY_PHONE, KEY_CATEGORY, KEY_TYPE, KEY_PRICE, KEY_MIN_PRICE, KEY_MAX_PRICE},
                new int[]{R.id.txt_id_explore_item, R.id.txt_place_item, R.id.txt_address_explore_item, R.id.txt_phone_explore_item,
                        R.id.txt_category, R.id.txt_type_explore_item, R.id.txt_price, R.id.txt_min_price_explore_item, R.id.txt_max_price_explore_item});
        placesList.setAdapter(adapter);
    }
}
