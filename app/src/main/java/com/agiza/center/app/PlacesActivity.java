package com.agiza.center.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.usage.NetworkStats;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.agiza.center.app.Helper.CheckNetWorkStatus;
import com.center.agiza.Helper.HttpJsonParser;
import com.center.agiza.MyView;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlacesActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String firstName1 = "firstNameKey";
    public static final String lastName1= "lastNameKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String Password1 = "passwordKey";
    public static final String myid1 = "myidKey";

    static final String KEY_FLAG = "flag";
    static final String KEY_ADDRESS_FLAG = "address";

    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DATA = "data";
    private static final String KEY_ADDRESS_NAME = "name";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_TYPE = "address_type";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_SUCCESS = "success";
    private static final String BASE_URL = "http://anwani.net/seya/";

    View addPlace, viewPlaces, viewDeclare, allPlacesView, alertsView;
    ConstraintLayout addCons, placesCons, declareCons, addMenuCons, viewMenuCons, declareMenuCons, mapCons, allPlacesCons, alertsCons,
                        detailsAddCons, requestsCons, searchCons;
    private GoogleMap mMap;
    TextView resultsTxt, countTxt, addressMapTxt, alertsCountTxt, alertsTxt, mapTxt, addressNameTxt, categoryTxt, typeTxt, closeTxt;
    AutoCompleteTextView searchEdt;
    ImageView searchImg, clearImg, homeImg, logoImg, favoriteImg, terrainImg, flatImg, satelliteImg, searchOptionImg;
    String selectedAddress, theName, theAddress, searchedName, searchedAddress, Latitude, Longitude, Category, visitedString,
            LongitudemapLat, mapLong, mapLat, mapAddress, mapName, mapNameClicked, docId, stla, stlo, theCategory, theType;
    int success, limiter, checker;
    ArrayList<String> autoList, addressList, visitedList, alertsList, compiledAlertsList, namesList;
    ArrayList<HashMap<String, String>> namesMapList, myPlacesList, finalPlacesList;
    HashMap<String, String> map, map1;
    FirebaseFirestore db;
    String firstName, lastName, Phone, Email, Password, myid, visitType, theRating, theCounty, theRegion, theRoad;
    SharedPreferences sharedpreferences;
    ArrayList<Place> listPlaces, listFullAlerts;
    ListView placesList, listAlerts, feedbackList;
    String[] addressPart1;
    String addressTwo, theFeedback;
    GridView gridOptions;
    ArrayList gridList;
    Button favoriteBtn;
    Intent j;
    Bundle location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = FirebaseFirestore.getInstance();
        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Password = sharedpreferences.getString(Password1, "");
        Email = sharedpreferences.getString(Email1, "");
        Phone = sharedpreferences.getString(phoneNumber1, "");
        firstName = sharedpreferences.getString(firstName1, "");
        lastName = sharedpreferences.getString(lastName1, "");
        myid = sharedpreferences.getString(myid1, "");

        autoList= new ArrayList<>();
        namesList= new ArrayList<>();
        visitedList= new ArrayList<>();
        alertsList= new ArrayList<>();
        addressList= new ArrayList<>();
        namesMapList= new ArrayList<>();
        myPlacesList= new ArrayList<>();
        compiledAlertsList= new ArrayList<>();
        finalPlacesList= new ArrayList<>();
        listPlaces = new ArrayList<Place>();
        listFullAlerts= new ArrayList<Place>();
        limiter=0;
        checker=0;

        addCons=(ConstraintLayout) findViewById(R.id.cons_add_places);
        placesCons=(ConstraintLayout) findViewById(R.id.cons_view_places);
        declareCons=(ConstraintLayout) findViewById(R.id.cons_declare_places);

        addMenuCons=(ConstraintLayout) findViewById(R.id.cons_add_places_menu);
        viewMenuCons =(ConstraintLayout) findViewById(R.id.cons_view_places_menu);
        declareMenuCons=(ConstraintLayout)findViewById(R.id.cons_declare_places_menu);

        allPlacesCons=(ConstraintLayout) findViewById(R.id.cons_all_places_view);
        alertsCons=(ConstraintLayout) findViewById(R.id.cons_alerts_view);
        detailsAddCons=(ConstraintLayout) findViewById(R.id.cons_add_place_details);
        requestsCons=(ConstraintLayout) findViewById(R.id.cons_requests_places);
        searchCons=(ConstraintLayout) findViewById(R.id.cos_search_explore);
        alertsView=(View) findViewById(R.id.view_alerts_view);
        allPlacesView=(View) findViewById(R.id.view_all_places_view);

        addPlace=(View) findViewById(R.id.view_add_places);
        viewPlaces=(View) findViewById(R.id.view_my_places);
        viewDeclare=(View) findViewById(R.id.view_declare_places);
        resultsTxt=(TextView) findViewById(R.id.txt_result_add_place);
        searchEdt = (AutoCompleteTextView) findViewById(R.id.edt_search);
        searchImg=(ImageView) findViewById(R.id.img_search_lib);
        clearImg=(ImageView) findViewById(R.id.img_clear_search);
        searchOptionImg=(ImageView) findViewById(R.id.img_search_option_places);
        favoriteImg=(ImageView) findViewById(R.id.img_add_favorite_places);
        homeImg=(ImageView) findViewById(R.id.img_home);
        logoImg=(ImageView) findViewById(R.id.img_logo_places);
        placesList=(ListView) findViewById(R.id.list_places);
        listAlerts=(ListView) findViewById(R.id.list_alerts);
        countTxt=(TextView) findViewById(R.id.txt_count_view_places);
        addressMapTxt=(TextView) findViewById(R.id.txt_map_address_places);
        closeTxt=(TextView) findViewById(R.id.txt_close_map_places);
        alertsCountTxt=(TextView) findViewById(R.id.txt_alerts_count_places);
        alertsTxt=(TextView) findViewById(R.id.txt_alerts);
        mapTxt=(TextView) findViewById(R.id.txt_map_menu);
        addressNameTxt=(TextView) findViewById(R.id.txt_result_name_place);
        mapCons=(ConstraintLayout) findViewById(R.id.cons_map_places);
        gridOptions=(GridView) findViewById(R.id.grid_places);
        categoryTxt=(TextView) findViewById(R.id.txt_property_category_places);
        typeTxt=(TextView) findViewById(R.id.txt_property_type_places);
        favoriteBtn=(Button) findViewById(R.id.btn_favorite_address_places);

        terrainImg = (ImageView) findViewById(R.id.img_terrain_map);
        flatImg =(ImageView) findViewById(R.id.img_flat_map);
        satelliteImg = (ImageView) findViewById(R.id.img_sattelite_map);

        FetchPlaces();
        new AutoNames().execute();

        gridList= new ArrayList<>();
        gridList.add(new ItemAddress("Ownership"));
        gridList.add(new ItemAddress("Sub-Address"));
        gridList.add(new ItemAddress("Verification"));
        gridList.add(new ItemAddress("Transfer"));
        gridList.add(new ItemAddress("Update"));
        gridList.add(new ItemAddress("Remove"));

        GridPlacesAdapter myAdapter=new GridPlacesAdapter(this,R.layout.grid_address,gridList);
        gridOptions.setAdapter(myAdapter);

        ObjectAnimator animation1 = ObjectAnimator.ofFloat(logoImg, "rotationY", 90f, 270f);
        animation1.setDuration(2600);
        animation1.setRepeatCount(ObjectAnimator.INFINITE);
        animation1.setInterpolator(new AccelerateDecelerateInterpolator());
        animation1.start();

        gridOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        j = new Intent(PlacesActivity.this, RequestsActivity.class);
                        location = new Bundle();
                        location.putString("address", searchedAddress);
                        location.putString("type", "Own Address Request");
                        j.putExtras(location);
                        startActivity(j);

                        break;
                    case 1:
                        j = new Intent(PlacesActivity.this, RequestsActivity.class);
                        location = new Bundle();
                        location.putString("address", searchedAddress);
                        location.putString("type", "Sub-Address Request");
                        j.putExtras(location);
                        startActivity(j);
                        break;
                    case 2:
                        j = new Intent(PlacesActivity.this, RequestsActivity.class);
                        location = new Bundle();
                        location.putString("address", searchedAddress);
                        location.putString("type", "Verification Request");
                        j.putExtras(location);
                        startActivity(j);
                        break;
                    case 3:
                        j = new Intent(PlacesActivity.this, RequestsActivity.class);
                        location = new Bundle();
                        location.putString("address", searchedAddress);
                        location.putString("type", "Transfer Request");
                        j.putExtras(location);
                        startActivity(j);
                        break;
                    case 4:
                        j = new Intent(PlacesActivity.this, RequestsActivity.class);
                        location = new Bundle();
                        location.putString("address", searchedAddress);
                        location.putString("type", "Update Request");
                        j.putExtras(location);
                        startActivity(j);
                        break;
                    case 5:
                        j = new Intent(PlacesActivity.this, RequestsActivity.class);
                        location = new Bundle();
                        location.putString("address", searchedAddress);
                        location.putString("type", "Remove Request");
                        j.putExtras(location);
                        startActivity(j);
                        break;
                }
            }
        });

        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PlacesActivity.this, HomeActivity.class));
            }
        });

        searchOptionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchCons.getVisibility()==View.GONE) {
                    searchCons.setVisibility(View.VISIBLE);
                }else{
                    searchOptionImg.setVisibility(View.GONE);
                }
            }
        });

        searchEdt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedAddress=searchEdt.getText().toString();
                ProcessAddress();
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
                addressNameTxt.setText("");
                resultsTxt.setText("");
                detailsAddCons.setVisibility(View.GONE);
                clearImg.setVisibility(View.GONE);
            }
        });

        favoriteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker==0) {
                    favoriteImg.setImageResource(R.drawable.ic_star_orange_24dp);
                    AddPlace();
                }else{
                    RemovePlace();
                }
            }
        });

        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestsCons.getVisibility()==View.VISIBLE){
                    requestsCons.setVisibility(View.GONE);
                    placesCons.setVisibility(View.VISIBLE);
                    favoriteBtn.setText("REQUESTS");
                    searchCons.setVisibility(View.GONE);
                    searchOptionImg.setVisibility(View.VISIBLE);

                }else{
                    requestsCons.setVisibility(View.VISIBLE);
                    placesCons.setVisibility(View.GONE);
                    favoriteBtn.setText("FAVORITES");
                    searchCons.setVisibility(View.VISIBLE);
                    searchOptionImg.setVisibility(View.GONE);
                }

            }
        });

        mapTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapCons.setVisibility(View.VISIBLE);
                addressMapTxt.setText(searchedAddress);

                /*if(mapCons.getVisibility()==View.GONE){
                    mapCons.setVisibility(View.VISIBLE);
                    mapTxt.setBackground(getResources().getDrawable(R.drawable.a_custom_new_brown));
                    mapTxt.setText("HIDE");

                    addCons.setVisibility(View.GONE);
                    placesCons.setVisibility(View.GONE);
                    declareCons.setVisibility(View.GONE);

                    addMenuCons.setBackgroundResource(R.color.clear);
                    viewMenuCons.setBackgroundResource(R.color.clear);
                    declareMenuCons.setBackgroundResource(R.color.clear);
                }else{
                    mapCons.setVisibility(View.GONE);
                    mapTxt.setBackground(getResources().getDrawable(R.drawable.custom_button_app_white));
                    mapTxt.setText("MAP");

                    addCons.setVisibility(View.GONE);
                    placesCons.setVisibility(View.VISIBLE);
                    declareCons.setVisibility(View.GONE);

                    viewMenuCons.setBackgroundResource(R.drawable.a_custom_new_brown);
                    addMenuCons.setBackgroundResource(R.color.clear);
                    declareMenuCons.setBackgroundResource(R.color.clear);
                }*/
            }
        });

        closeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapCons.setVisibility(View.GONE);
            }
        });

        addMenuCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCons.setVisibility(View.VISIBLE);
                placesCons.setVisibility(View.GONE);
                declareCons.setVisibility(View.GONE);

                addPlace.setVisibility(View.VISIBLE);
                viewPlaces.setVisibility(View.GONE);
                viewDeclare.setVisibility(View.GONE);

                addMenuCons.setBackgroundResource(R.drawable.a_custom_new_brown);
                viewMenuCons.setBackgroundResource(R.color.clear);
                declareMenuCons.setBackgroundResource(R.color.clear);
            }
        });

        viewMenuCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCons.setVisibility(View.GONE);
                placesCons.setVisibility(View.VISIBLE);
                declareCons.setVisibility(View.GONE);

                addPlace.setVisibility(View.GONE);
                viewPlaces.setVisibility(View.VISIBLE);
                viewDeclare.setVisibility(View.GONE);

                viewMenuCons.setBackgroundResource(R.drawable.a_custom_new_brown);
                addMenuCons.setBackgroundResource(R.color.clear);
                declareMenuCons.setBackgroundResource(R.color.clear);
            }
        });

        declareMenuCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCons.setVisibility(View.GONE);
                placesCons.setVisibility(View.GONE);
                declareCons.setVisibility(View.VISIBLE);

                addPlace.setVisibility(View.GONE);
                viewPlaces.setVisibility(View.GONE);
                viewDeclare.setVisibility(View.VISIBLE);

                viewMenuCons.setBackgroundResource(R.color.clear);
                addMenuCons.setBackgroundResource(R.color.clear);
                declareMenuCons.setBackgroundResource(R.drawable.a_custom_new_brown);
            }
        });

        allPlacesCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placesList.setVisibility(View.VISIBLE);
                listAlerts.setVisibility(View.GONE);
                allPlacesView.setVisibility(View.VISIBLE);
                alertsView.setVisibility(View.GONE);

                alertsCons.setBackgroundResource(R.color.blue);
                allPlacesCons.setBackgroundResource(R.drawable.a_custom_new_brown);
            }
        });


        alertsCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placesList.setVisibility(View.GONE);
                listAlerts.setVisibility(View.VISIBLE);
                allPlacesView.setVisibility(View.GONE);
                alertsView.setVisibility(View.VISIBLE);

                allPlacesCons.setBackgroundResource(R.color.blue);
                alertsCons.setBackgroundResource(R.drawable.a_custom_new_brown);
            }
        });

        alertsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placesList.setVisibility(View.GONE);
                listAlerts.setVisibility(View.VISIBLE);
                allPlacesView.setVisibility(View.GONE);
                alertsView.setVisibility(View.VISIBLE);

                allPlacesCons.setBackgroundResource(R.color.blue);
                alertsCons.setBackgroundResource(R.drawable.a_custom_new_brown);
            }
        });

        terrainImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });

        flatImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        satelliteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-1.289238, 36.820442);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Start"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void ProcessAddress(){
        checker=0;
        if(selectedAddress.contains(":")){
            String[] addressPart = selectedAddress.split("\\:");
            resultsTxt.setText(addressPart[0]);
            searchEdt.setText(selectedAddress);
            searchedAddress=addressPart[0];
            searchedName=addressPart[1];
            resultsTxt.setVisibility(View.VISIBLE);
            addressNameTxt.setText(searchedName);
            detailsAddCons.setVisibility(View.VISIBLE);
        }else{
            resultsTxt.setText(selectedAddress);
            searchedAddress=selectedAddress;
            searchedName="No Name";
            resultsTxt.setVisibility(View.VISIBLE);
            addressNameTxt.setText(searchedName);
            detailsAddCons.setVisibility(View.VISIBLE);
        }

        int a=addressList.indexOf(searchedAddress);
        categoryTxt.setText(namesMapList.get(a).get(KEY_CATEGORY));
        typeTxt.setText(namesMapList.get(a).get(KEY_TYPE));
        addressNameTxt.setText(namesMapList.get(a).get(KEY_ADDRESS));

        stla=namesMapList.get(a).get(KEY_LATITUDE);
        stlo=namesMapList.get(a).get(KEY_LONGITUDE);
        SetMarkers();

        for (int i = 0; i < listPlaces.size(); i++) {
            if (listPlaces.get(i).getAddress().contentEquals(searchedAddress)){
                checker++;
            }
        }
        if(checker>0){
            favoriteImg.setImageResource(R.drawable.ic_star_orange_24dp);
            favoriteImg.setBackgroundResource(R.drawable.custom_pressed_medical);
        }
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
                        theName = incidence.getString(KEY_ADDRESS_NAME);
                        Latitude = incidence.getString(KEY_LATITUDE);
                        Longitude= incidence.getString(KEY_LONGITUDE);
                        theCategory = incidence.getString(KEY_CATEGORY);
                        theType= incidence.getString(KEY_TYPE);


                        map = new HashMap<>();
                        map.put(KEY_ADDRESS_NAME, theAddress);
                        map.put(KEY_ADDRESS, theName);
                        map.put(KEY_LATITUDE, Latitude);
                        map.put(KEY_LONGITUDE, Longitude);
                        map.put(KEY_CATEGORY, theCategory);
                        map.put(KEY_TYPE, theType);
                        namesMapList.add(map);
                        addressList.add(theAddress);
                        namesList.add(theName);

                        if(!theName.contentEquals("No Name")) {
                            autoList.add(theAddress + ": " +theName);
                            //autoList.add(theAddress);
                        }else{
                            autoList.add(theAddress);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            if (success == 1) {
                Set<String> set = new HashSet<>(autoList);
                autoList.clear();
                autoList.addAll(set);
                ProcessMap();

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PlacesActivity.this, android.R.layout.simple_dropdown_item_1line, autoList);
                searchEdt.setAdapter(dataAdapter);
                //testTxt.setText(String.valueOf(autoList.size()));
            } else {

            }
        }
    }
    private void AddPlace(){
            Date currentDate = new Date();
            Map<String, Object> emergency = new HashMap<>();
            emergency.put("address", searchedAddress);
            emergency.put("name", searchedName);
            emergency.put("category", Category);
            emergency.put("time", currentDate);
            emergency.put("myid", myid);


            db.collection("users").document(myid).collection("places")
                    .add(emergency)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            FetchPlaces();
                            Toast.makeText(PlacesActivity.this, "Marked as Favorite", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            favoriteImg.setImageResource(R.drawable.ic_star_white_24dp);
                        }
                    });
    }

    private void RemovePlace(){

        db.collection("users").document(myid).collection("places")
                .whereEqualTo("address", searchedAddress)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        docId = document.getId();
                    }
                    db.collection("users").document(myid).collection("places")
                            .document(docId).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    favoriteImg.setImageResource(R.drawable.ic_star_white_24dp);
                                    favoriteImg.setBackgroundResource(R.drawable.custom_button_app);
                                    FetchPlaces();
                                }
                            });

                } else {
                }
            }
        });


    }

    private  void FetchPlaces(){
        listPlaces.clear();
        db.collection("users").document(myid).collection("places")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Place miss = document.toObject(Place.class);
                        listPlaces.add(miss);

                        //listAlerts.setAdapter(new ArrayAdapter<String>(PlacesActivity.this, android.R.layout.simple_list_item_1, visitedList));
                    }

                    PlacesAdapter ria = new PlacesAdapter(PlacesActivity.this,listPlaces);
                    placesList.setAdapter(ria);

                    placesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (CheckNetWorkStatus.isNetworkAvailable(PlacesActivity.this)) {

                                selectedAddress=((TextView) view.findViewById(R.id.txt_address_my_place)).getText().toString();
                                searchEdt.setText(selectedAddress);

                                resultsTxt.setVisibility(View.GONE);
                                detailsAddCons.setVisibility(View.GONE);

                                ProcessAddress();

                                /*addressTwo=((TextView) view.findViewById(R.id.txt_address_my_place)).getText().toString();

                                LayoutInflater inflater = LayoutInflater.from(PlacesActivity.this);
                                final View dialogView = inflater.inflate(R.layout.places_dialog, null);
                                final AlertDialog dialogBuilder = new AlertDialog.Builder(PlacesActivity.this).create();
                                dialogBuilder.setView(dialogView);

                                ConstraintLayout feedbackCons=(ConstraintLayout) dialogView. findViewById(R.id.cons_feedback_places);
                                ConstraintLayout medicalCons=(ConstraintLayout) dialogView. findViewById(R.id.cons_medical_places);
                                final ConstraintLayout commentCons=(ConstraintLayout) dialogView. findViewById(R.id.cons_comment_places);
                                Button sendBtn=(Button) dialogView. findViewById(R.id.btn_send_feedback);
                                Button cancelBtn=(Button) dialogView. findViewById(R.id.btn_cancel_feedback);
                                final EditText commentEdt=(EditText) dialogView. findViewById(R.id.edt_comment_places);
                                TextView addressDialogTxt=(TextView) dialogView. findViewById(R.id.txt_address_places);
                                RatingBar placeBar=(RatingBar) dialogView.findViewById(R.id.rating_places);
                                addressDialogTxt.setText(addressTwo);

                                placeBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                    @Override
                                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                                        theRating =String.valueOf(v);
                                    }
                                });

                                feedbackCons.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        commentCons.setVisibility(View.VISIBLE);
                                    }
                                });

                                medicalCons.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        visitedString=addressTwo;
                                        AddAlert();
                                    }
                                });

                                sendBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        theFeedback=commentEdt.getText().toString();
                                        AddFeedback();
                                    }
                                });

                                cancelBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogBuilder.dismiss();
                                    }
                                });

                                dialogBuilder.show();*/

                            } else {
                                Toast.makeText(PlacesActivity.this,
                                        "Unable to connect to internet",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(PlacesActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void ProcessMap(){
        for(int i=0; i<namesMapList.size(); i++){
                for(int a=0; a<listPlaces.size(); a++){
                    if(addressList.get(i).contentEquals(listPlaces.get(a).getAddress())){
                        myPlacesList.add(namesMapList.get(i));
                    }
                }
                if (i==namesMapList.size()-1){
                }
            }

    }

    private void SetMarkers(){
        mMap.clear();

        Double stlat = (Double.parseDouble(stla));
        Double stlong = Double.parseDouble(stlo);
        LatLng building = new LatLng(stlat, stlong);

        mMap.addMarker(new MarkerOptions().position(building).title(searchedAddress));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(building));
        Log.e("PlaceLL", searchedAddress);

    }

    private  void FetchAlerts(){
        db.collection("alerts")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Place miss1 = document.toObject(Place.class);
                        listFullAlerts.add(miss1);
                        //alertsList.add(miss1.getAddress());

                        if (listFullAlerts.size()>0){
                            new FilterRecent().execute();
                            //new ProcessAlerts().execute();
                        }

                    }
                    //PlaceFilterAdapter ria = new PlaceFilterAdapter(PlacesActivity.this,listFullAlerts);
                    //listAlerts.setAdapter(ria);
                } else {
                    Toast.makeText(PlacesActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class FilterRecent extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            limiter++;
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            Date currentDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_YEAR, -7);
            Date pastDate = calendar.getTime();

            for (int a = 0; a < listFullAlerts.size(); a++) {
                if (listFullAlerts.get(a).getTime().before(currentDate)&&
                        listFullAlerts.get(a).getTime().after(pastDate)){
                        alertsList.add(listFullAlerts.get(a).getAddress());
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            Set<String> set = new HashSet<>(alertsList);
            alertsList.clear();
            alertsList.addAll(set);
            new ProcessAlerts().execute();

           // listAlerts.setAdapter(new ArrayAdapter<String>(PlacesActivity.this, android.R.layout.simple_list_item_1, alertsList));
        }
    }

    private class ProcessAlerts extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            Set<String> set = new HashSet<>(alertsList);
            alertsList.clear();
            alertsList.addAll(set);

            for (int i = 0; i < alertsList.size(); i++) {
//adjusted index 0
                addressPart1 = alertsList.get(i).split("\\:");
            }
            return null;
        }

        protected void onPostExecute(String result) {

            Collections.addAll(compiledAlertsList, addressPart1);
            Set<String> set = new HashSet<>(compiledAlertsList);
            compiledAlertsList.clear();
            compiledAlertsList.addAll(set);
            alertsCountTxt.setText("Alerts (" + String.valueOf(compiledAlertsList.size()) + ")");
            //listAlerts.setAdapter(new ArrayAdapter<String>(PlacesActivity.this, android.R.layout.simple_list_item_1, compiledAlertsList));

            Set<String> set1 = new HashSet<>(visitedList);
            visitedList.clear();
            visitedList.addAll(set1);
            visitedList.retainAll(compiledAlertsList);
            PlaceAdapter ria = new PlaceAdapter(PlacesActivity.this, finalPlacesList);
            placesList.setAdapter(ria);
        }
    }

    private void CreateString(){
        Set<String> set = new HashSet<>(visitedList);
        visitedList.clear();
        visitedList.addAll(set);
        visitedString = TextUtils.join(":", visitedList);
        AddAlert();

    }

    private void AddAlert(){
        Date currentDate = new Date();
        Map<String, Object> emergency = new HashMap<>();
        emergency.put("address", visitedString);
        emergency.put("time", currentDate);

        db.collection("alerts")
                .add(emergency)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(PlacesActivity.this, "Successful!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void AddFeedback(){
        Date currentDate = new Date();
        Map<String, Object> emergency = new HashMap<>();
        emergency.put("address", addressTwo);
        emergency.put("time", currentDate);
        emergency.put("rating", theRating);
        emergency.put("feedback", theFeedback);

        db.collection("feedback")
                .add(emergency)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(PlacesActivity.this, "Feedback Sent!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

}
