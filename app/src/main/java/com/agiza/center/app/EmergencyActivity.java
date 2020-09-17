package com.agiza.center.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.agiza.center.app.Helper.CheckNetWorkStatus;
import com.agiza.center.app.Helper.HttpJsonParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmergencyActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    static final String KEY_PROVIDER_ADDRESS = "address";
    static final String KEY_NAME = "name";
    static final String KEY_PHONE = "phone1";

    static final String KEY_PROVIDER_LATITUDE = "latitude";
    static final String KEY_PROVIDER_LONGITUDE = "longitude";
    static final String KEY_PROVIDER_TYPE = "responder_type";
    static final String KEY_STATUS = "status";

    static final String KEY_PROVIDER_ID = "provider_id";
    static final String KEY_TYPE = "type";
    static final String KEY_PROVIDER_NAME = "provider_name";
    static final String KEY_ADDRESS = "address";
    static final String KEY_INCIDENCE_LATITUDE = "latitude";
    static final String KEY_INCIDENCE_LONGITUDE = "longitude";
    static final String KEY_CALLER_ID = "caller_id";
    static final String KEY_CALLER_PHONE = "caller_phone_number";

    private static final String ADD_INCIDENCE_URL = "http://www.agizakenya.com/agiza/";

    public static final String firstName1 = "firstNameKey";
    public static final String lastName1= "lastNameKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String Password1 = "passwordKey";
    public static final String myid1 = "myidKey";

    String firstName, lastName, Phone, Email, Password, myid, visitType;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    private static final String KEY_ORIGIN_COORDINATES = "origin_matrix";
    private static final String KEY_DESTINATION_COORDINATES = "destination_matrix";

    static final String KEY_HOSPITAL_NUMBER = "contact";
    static final String KEY_HOSPITAL_NAME = "name";
    static final String KEY_HOSPITAL_LATITUDE = "latitude";
    static final String KEY_HOSPITAL_LONGITUDE = "longitude";

    private static final String BASE_FINAL_LIST = "https://centerforty.com/apps/final_police_list.php";
    static final String TAG_DURATION = "text";
    static final String TAG_DISTANCE = "text";
    static final String TAG_DISTANCE_VALUE = "value";
    private static final String KEY_SUCCESS = "success";

    String distance, duration, destinPol, polfiltered, myAddress, myLocation;

    String distanceValue, policeContact, stationName;
    String latMap, longMap, policeContactNumber;

    TextView emergencyType, latitudeTxt, longitudeTxt, testTxt, countTxt, locationTxt, hospitalsTxt;
    String Latitude, Longitude, locationStatus, selectedStation, stationLat, stationLong, stationPhone, Name2, providerType;

    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    LocationRequest locationRequest;
    LocationManager locationManager;
    boolean GpsStatus = false;
    Context context;

    private ArrayList<HashMap<String, String>> distanceList, baseList;
    HashMap<String, String> map1, map2;
    ArrayList<Hospital> listAmbulanceM, listHospitalsM, listFireM, listPoliceM, fullList;
    public static ArrayList<Hospital> listAllContacts= new ArrayList<Hospital>();
    int success, counter;
    Animation animation;
    JSONArray step3;
    Activity activity;
    LayoutInflater inflater = null;
    FirebaseFirestore db;
    private ArrayList<String> listPoliceStation, listName, listName2, listLat, listLong, listCombined;
    private ProgressDialog pDialog;

    ListView policeList, fireList, ambulanceList, hospitalList;
    ConstraintLayout medicalConsFull, fireCons, policeCons, ambulanceCons, hospitalCons, fireConsFull, policeConsFull, medicalCons,
                    ambulanceListCons, hospitalListCons, searchCons, searchResultCons, saveCons, callCons, visitCons, emergencyCons;
    TextView ambulanceTxt, hospitalTxt, nameTxt;
    View policeView, fireView, medicalView, ambulanceView, hospitalView;
    private EmergencyAdapter emergencyAdapter;
    ImageView homeImg, logoImg, showSearch, clearImg;
    AutoCompleteTextView searchEdt;
    ArrayList<String> nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        pDialog = new ProgressDialog(EmergencyActivity.this);
        pDialog.setMessage("Loading. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        db = FirebaseFirestore.getInstance();
        listPoliceStation = new ArrayList<String>();
        listName = new ArrayList<String>();
        listName2 = new ArrayList<String>();
        listLat = new ArrayList<String>();
        listLong = new ArrayList<String>();
        baseList = new ArrayList<>();
        nameList= new ArrayList<>();
        listCombined= new ArrayList<>();
        listAmbulanceM= new ArrayList<>();
        listHospitalsM= new ArrayList<>();
        listFireM= new ArrayList<>();
        listPoliceM= new ArrayList<>();
        fullList= new ArrayList<>();
        listAllContacts= new ArrayList<Hospital>();

        visitType = "Fire";
        locationStatus="Not Captured";
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        context = getApplicationContext();

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Password = sharedpreferences.getString(Password1, "");
        Email = sharedpreferences.getString(Email1, "");
        Phone = sharedpreferences.getString(phoneNumber1, "");
        firstName = sharedpreferences.getString(firstName1, "");
        lastName = sharedpreferences.getString(lastName1, "");
        myid = sharedpreferences.getString(myid1, "");

        policeList=(ListView) findViewById(R.id.list_police_emergency);
        fireList=(ListView) findViewById(R.id.list_fire_emergency);
        ambulanceList=(ListView) findViewById(R.id.list_medical_list);
        hospitalList=(ListView) findViewById(R.id.list_hospital_emergency);
        medicalConsFull=(ConstraintLayout) findViewById(R.id.cons_medical_full);
        fireCons=(ConstraintLayout) findViewById(R.id.cons_fire);
        policeCons=(ConstraintLayout) findViewById(R.id.cons_police);
        ambulanceCons=(ConstraintLayout) findViewById(R.id.cons_ambulance);
        hospitalCons=(ConstraintLayout) findViewById(R.id.cons_hospital);
        fireConsFull=(ConstraintLayout) findViewById(R.id.cons_list_fire);
        policeConsFull=(ConstraintLayout) findViewById(R.id.cons_list_police);
        medicalCons=(ConstraintLayout) findViewById(R.id.cons_medical);
        ambulanceListCons=(ConstraintLayout) findViewById(R.id.cons_ambulance_list);
        hospitalListCons=(ConstraintLayout) findViewById(R.id.cons_hospital_list);
        searchCons=(ConstraintLayout) findViewById(R.id.cos_search_explore);
        searchResultCons=(ConstraintLayout) findViewById(R.id.cons_search_result_emergency);

        callCons=(ConstraintLayout) findViewById(R.id.cons_call_result);
        visitCons=(ConstraintLayout) findViewById(R.id.cons_visit_result);
        saveCons=(ConstraintLayout) findViewById(R.id.cons_save_result);
        emergencyCons=(ConstraintLayout) findViewById(R.id.cons_emergency_lists);

        ambulanceTxt=(TextView) findViewById(R.id.txt_ambulance_emergency_menu);
        hospitalTxt=(TextView) findViewById(R.id.txt_hospital_emergency_menu);
        medicalView=(View) findViewById(R.id.view_medical);
        policeView=(View) findViewById(R.id.view_police);
        fireView=(View) findViewById(R.id.view_fire);
        ambulanceView=(View) findViewById(R.id.view_ambulance_emergency);
        hospitalView=(View) findViewById(R.id.view_hospital_emergency);
        latitudeTxt=(TextView) findViewById(R.id.txt_lat_emergency);
        longitudeTxt=(TextView) findViewById(R.id.txt_lon_emergency);
        testTxt=(TextView) findViewById(R.id.txt_test_joined);
        homeImg=(ImageView) findViewById(R.id.img_home_emergency);
        logoImg=(ImageView) findViewById(R.id.img_logo_emergency);
        showSearch=(ImageView) findViewById(R.id.img_show_search_emergency);
        clearImg=(ImageView) findViewById(R.id.img_clear_search);
        searchEdt=(AutoCompleteTextView) findViewById(R.id.edt_search);
        nameTxt = (TextView)findViewById(R.id.txt_police_name_police_list);

        animation = new AlphaAnimation((float) 0, 1);
        animation.setDuration(400);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);

        ObjectAnimator animation1 = ObjectAnimator.ofFloat(logoImg, "rotationY", 90f, 270f);
        animation1.setDuration(2600);
        animation1.setRepeatCount(ObjectAnimator.INFINITE);
        animation1.setInterpolator(new AccelerateDecelerateInterpolator());
        animation1.start();

        setUpGClient();
        CheckGpsStatus();

        FetchAmbulances();
        FetchHospitals();
        FetchFireStations();
        FetchPoliceStations();

        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmergencyActivity.this, HomeActivity.class));
            }
        });

        showSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchCons.getVisibility()==View.GONE){
                    searchCons.setVisibility(View.VISIBLE);
                    emergencyCons.setVisibility(View.GONE);
                    showSearch.setImageResource(R.drawable.ic_clear_white_24dp);
                }else{
                    searchCons.setVisibility(View.GONE);
                    searchResultCons.setVisibility(View.GONE);
                    emergencyCons.setVisibility(View.VISIBLE);
                    showSearch.setImageResource(R.drawable.ic_search_black_24dp);
                }
            }
        });

        clearImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEdt.setText("");
                nameTxt.setText("");
                searchResultCons.setVisibility(View.GONE);
                clearImg.setVisibility(View.GONE);
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

        searchEdt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedStation=searchEdt.getText().toString();
                ProcessResult();
            }
        });

        callCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent();
                intent1.setAction(Intent.ACTION_CALL);
                intent1.setData(Uri.parse("tel:" + stationPhone));
                startActivity (intent1);
            }
        });

        visitCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(
                        Uri.parse("https://www.google.com/maps?saddr="+ stationLat +"," + stationLong + "&daddr=" +
                                (stationLat + "," + stationLong)));
                startActivity(intent);
            }
        });

        saveCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.NAME, selectedStation);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, stationPhone);
                startActivity(intent);

            }
        });

        medicalCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                medicalConsFull.setVisibility(View.VISIBLE);
                policeConsFull.setVisibility(View.GONE);
                fireConsFull.setVisibility(View.GONE);

                medicalCons.setBackgroundResource(R.drawable.a_custom_new_brown);
                policeCons.setBackgroundResource(R.color.clear);
                fireCons.setBackgroundResource(R.color.clear);

                medicalView.setVisibility(View.VISIBLE);
                policeView.setVisibility(View.GONE);
                fireView.setVisibility(View.GONE);
            }
        });

        fireCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                medicalConsFull.setVisibility(View.GONE);
                policeConsFull.setVisibility(View.GONE);
                fireConsFull.setVisibility(View.VISIBLE);

                medicalCons.setBackgroundResource(R.color.clear);
                policeCons.setBackgroundResource(R.color.clear);
                fireCons.setBackgroundResource(R.drawable.a_custom_new_brown);

                medicalView.setVisibility(View.GONE);
                policeView.setVisibility(View.GONE);
                fireView.setVisibility(View.VISIBLE);
            }
        });

        policeCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                medicalConsFull.setVisibility(View.GONE);
                policeConsFull.setVisibility(View.VISIBLE);
                fireConsFull.setVisibility(View.GONE);

                medicalCons.setBackgroundResource(R.color.clear);
                policeCons.setBackgroundResource(R.drawable.a_custom_new_brown);
                fireCons.setBackgroundResource(R.color.clear);

                medicalView.setVisibility(View.GONE);
                policeView.setVisibility(View.VISIBLE);
                fireView.setVisibility(View.GONE);
            }
        });

        ambulanceCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ambulanceListCons.setVisibility(View.VISIBLE);
                hospitalListCons.setVisibility(View.GONE);
                ambulanceView.setVisibility(View.VISIBLE);
                hospitalView.setVisibility(View.GONE);

                ambulanceCons.setBackgroundColor(getResources().getColor(R.color.places_brown));
                ambulanceTxt.setTextColor(getResources().getColor(R.color.white));
                hospitalCons.setBackgroundColor(getResources().getColor(R.color.findme));
                hospitalTxt.setTextColor(getResources().getColor(R.color.white));
            }
        });

        hospitalCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ambulanceListCons.setVisibility(View.GONE);
                hospitalListCons.setVisibility(View.VISIBLE);
                ambulanceView.setVisibility(View.GONE);
                hospitalView.setVisibility(View.VISIBLE);

                ambulanceCons.setBackgroundColor(getResources().getColor(R.color.findme));
                ambulanceTxt.setTextColor(getResources().getColor(R.color.white));
                hospitalCons.setBackgroundColor(getResources().getColor(R.color.places_brown));
                hospitalTxt.setTextColor(getResources().getColor(R.color.white));
            }
        });
    }

    private void FetchAmbulances(){
        db.collection("ambulance").orderBy("name")
                //.whereEqualTo("category", "sub")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        //Ambulance miss = document.toObject(Ambulance.class);
                        Hospital miss = document.toObject(Hospital.class);
                        listPoliceStation.add(miss.getLatitude()+ ", ");
                        listPoliceStation.add(miss.getLongitude() + "|");
                        listName.add(miss.getName());
                        nameList.add(miss.getName());
                        listName2.add(miss.getContact());
                        listLat.add(miss.getLatitude());
                        listLong.add(miss.getLongitude());
                        listAmbulanceM.add(miss);
                        listCombined.add(miss.getName() + ": " + miss.getContact() + ": " + miss.getLatitude() + ": " + miss.getLongitude());
                    }
                    //FetchHospitals();
                    emergencyAdapter = new EmergencyAdapter(EmergencyActivity.this,listAmbulanceM);
                    ambulanceList.setAdapter(emergencyAdapter);
                    pDialog.dismiss();

                    ambulanceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (CheckNetWorkStatus.isNetworkAvailable(EmergencyActivity.this)) {

                                Name2=((TextView) view.findViewById(R.id.txt_police_name_police_list)).getText().toString();
                                latMap=((TextView) view.findViewById(R.id.txt_latitude_police_list)).getText().toString();
                                longMap=((TextView) view.findViewById(R.id.txt_longitude_police_list)).getText().toString();
                                policeContactNumber=((TextView) view.findViewById(R.id.txt_police_contact_list)).getText().toString();

                                LayoutInflater inflater = LayoutInflater.from(EmergencyActivity.this);
                                final View dialogView = inflater.inflate(R.layout.alert_direction_call, null);
                                final AlertDialog dialogBuilder = new AlertDialog.Builder(EmergencyActivity.this).create();
                                dialogBuilder.setView(dialogView);

                                ConstraintLayout directionConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_directions_alert);
                                ConstraintLayout callConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_call_alert);
                                ConstraintLayout cancelConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_cancel_alert);
                                TextView selectNameTxt = (TextView)dialogView.findViewById(R.id.txt_selected_provider);
                                TextView statusTxt = (TextView)dialogView.findViewById(R.id.txt_location_status_dialog);
                                selectNameTxt.setText(Name2);
                                if (locationStatus.contentEquals("Not Captured")){
                                    statusTxt.setBackgroundResource(R.color.red);
                                    statusTxt.setText("Location not Captured!");
                                    statusTxt.startAnimation(animation);
                                }else{
                                    statusTxt.setBackgroundResource(R.color.green);
                                    statusTxt.setText("Location Captured!");
                                    statusTxt.clearAnimation();
                                }

                                directionConstraint.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW).setData(
                                                Uri.parse("https://www.google.com/maps?saddr="+ latMap +"," + longMap + "&daddr=" +
                                                        (latitudeTxt.getText().toString()) + "," + (longitudeTxt.getText().toString())));
                                        startActivity(intent);
                                        //providerType="Ambulance";
                                        //new AddProvider().execute();
                                    }
                                });

                                callConstraint.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent intent1 = new Intent();
                                        intent1.setAction(Intent.ACTION_CALL);
                                        intent1.setData(Uri.parse("tel:" + policeContactNumber));
                                        startActivity (intent1);
                                    }
                                });

                                cancelConstraint.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogBuilder.dismiss();

                                    }
                                });

                                dialogBuilder.show();

                            } else {
                                Toast.makeText(EmergencyActivity.this,
                                        "Unable to connect to internet",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(EmergencyActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void FetchHospitals(){
        db.collection("hospitals").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Hospital miss = document.toObject(Hospital.class);
                        listPoliceStation.add(miss.getLatitude()+ ", ");
                        listPoliceStation.add(miss.getLongitude() + "|");
                        listName.add(miss.getName());
                        nameList.add(miss.getName());
                        listName2.add(miss.getContact());
                        listLat.add(miss.getLatitude());
                        listLong.add(miss.getLongitude());
                        listHospitalsM.add(miss);
                        listCombined.add(miss.getName() + ": " + miss.getContact() + ": " + miss.getLatitude() + ": " + miss.getLongitude());
                    }
                    //FetchPoliceStations();
                    emergencyAdapter = new EmergencyAdapter(EmergencyActivity.this,listHospitalsM);
                    hospitalList.setAdapter(emergencyAdapter);

                    hospitalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (CheckNetWorkStatus.isNetworkAvailable(EmergencyActivity.this)) {

                                Name2=((TextView) view.findViewById(R.id.txt_police_name_police_list)).getText().toString();
                                latMap=((TextView) view.findViewById(R.id.txt_latitude_police_list)).getText().toString();
                                longMap=((TextView) view.findViewById(R.id.txt_longitude_police_list)).getText().toString();
                                policeContactNumber=((TextView) view.findViewById(R.id.txt_police_contact_list)).getText().toString();

                                LayoutInflater inflater = LayoutInflater.from(EmergencyActivity.this);
                                final View dialogView = inflater.inflate(R.layout.alert_direction_call, null);
                                final AlertDialog dialogBuilder = new AlertDialog.Builder(EmergencyActivity.this).create();
                                dialogBuilder.setView(dialogView);

                                ConstraintLayout directionConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_directions_alert);
                                ConstraintLayout callConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_call_alert);
                                ConstraintLayout cancelConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_cancel_alert);
                                TextView selectNameTxt = (TextView)dialogView.findViewById(R.id.txt_selected_provider);
                                TextView statusTxt = (TextView)dialogView.findViewById(R.id.txt_location_status_dialog);
                                selectNameTxt.setText(Name2);
                                if (locationStatus.contentEquals("Not Captured")){
                                    statusTxt.setBackgroundResource(R.color.red);
                                    statusTxt.setText("Location not Captured!");
                                    statusTxt.startAnimation(animation);
                                }else{
                                    statusTxt.setBackgroundResource(R.color.green);
                                    statusTxt.setText("Location Captured!");
                                    statusTxt.clearAnimation();
                                }

                                directionConstraint.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW).setData(
                                                Uri.parse("https://www.google.com/maps?saddr="+ latMap +"," + longMap + "&daddr=" +
                                                        (latitudeTxt.getText().toString()) + "," + (longitudeTxt.getText().toString())));
                                        startActivity(intent);
                                    }
                                });

                                callConstraint.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent intent1 = new Intent();
                                        intent1.setAction(Intent.ACTION_CALL);
                                        intent1.setData(Uri.parse("tel:" + policeContactNumber));
                                        startActivity (intent1);
                                    }
                                });

                                cancelConstraint.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogBuilder.dismiss();

                                    }
                                });

                                dialogBuilder.show();

                            } else {
                                Toast.makeText(EmergencyActivity.this,
                                        "Unable to connect to internet",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(EmergencyActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void FetchPoliceStations(){
        db.collection("police_station").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        //PoliceStation miss = document.toObject(PoliceStation.class);
                        Hospital miss = document.toObject(Hospital.class);
                        listPoliceStation.add(miss.getLatitude()+ ", ");
                        listPoliceStation.add(miss.getLongitude() + "|");
                        listName.add(miss.getName());
                        nameList.add(miss.getName());
                        listName2.add(miss.getContact());
                        listLat.add(miss.getLatitude());
                        listLong.add(miss.getLongitude());
                        listPoliceM.add(miss);
                    }
                    //FetchFireStations();
                    emergencyAdapter = new EmergencyAdapter(EmergencyActivity.this,listPoliceM);
                    policeList.setAdapter(emergencyAdapter);

                    policeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (CheckNetWorkStatus.isNetworkAvailable(EmergencyActivity.this)) {

                                Name2=((TextView) view.findViewById(R.id.txt_police_name_police_list)).getText().toString();
                                latMap=((TextView) view.findViewById(R.id.txt_latitude_police_list)).getText().toString();
                                longMap=((TextView) view.findViewById(R.id.txt_longitude_police_list)).getText().toString();
                                policeContactNumber=((TextView) view.findViewById(R.id.txt_police_contact_list)).getText().toString();

                                LayoutInflater inflater = LayoutInflater.from(EmergencyActivity.this);
                                final View dialogView = inflater.inflate(R.layout.alert_direction_call, null);
                                final AlertDialog dialogBuilder = new AlertDialog.Builder(EmergencyActivity.this).create();
                                dialogBuilder.setView(dialogView);

                                ConstraintLayout directionConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_directions_alert);
                                ConstraintLayout callConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_call_alert);
                                ConstraintLayout cancelConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_cancel_alert);
                                TextView selectNameTxt = (TextView)dialogView.findViewById(R.id.txt_selected_provider);
                                TextView statusTxt = (TextView)dialogView.findViewById(R.id.txt_location_status_dialog);
                                selectNameTxt.setText(Name2);
                                if (locationStatus.contentEquals("Not Captured")){
                                    statusTxt.setBackgroundResource(R.color.red);
                                    statusTxt.setText("Location not Captured!");
                                    statusTxt.startAnimation(animation);
                                }else{
                                    statusTxt.setBackgroundResource(R.color.green);
                                    statusTxt.setText("Location Captured!");
                                    statusTxt.clearAnimation();
                                }

                                directionConstraint.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW).setData(
                                                Uri.parse("https://www.google.com/maps?saddr="+ latMap +"," + longMap + "&daddr=" +
                                                        (latitudeTxt.getText().toString()) + "," + (longitudeTxt.getText().toString())));
                                        startActivity(intent);

                                        //providerType="Police Station";
                                        //new AddProvider().execute();
                                    }
                                });

                                callConstraint.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent intent1 = new Intent();
                                        intent1.setAction(Intent.ACTION_CALL);
                                        intent1.setData(Uri.parse("tel:" + policeContactNumber));
                                        startActivity (intent1);
                                    }
                                });

                                cancelConstraint.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogBuilder.dismiss();

                                    }
                                });

                                dialogBuilder.show();

                            } else {
                                Toast.makeText(EmergencyActivity.this,
                                        "Unable to connect to internet",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(EmergencyActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void FetchFireStations(){
        db.collection("fire_station").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        //PoliceStation miss = document.toObject(PoliceStation.class);
                        Hospital miss = document.toObject(Hospital.class);
                        listPoliceStation.add(miss.getLatitude()+ ", ");
                        listPoliceStation.add(miss.getLongitude() + "|");
                        listName.add(miss.getName());
                        nameList.add(miss.getName());
                        listName2.add(miss.getContact());
                        listLat.add(miss.getLatitude());
                        listLong.add(miss.getLongitude());
                        listFireM.add(miss);
                        listCombined.add(miss.getName() + ": " + miss.getContact() + ": " + miss.getLatitude() + ": " + miss.getLongitude());
                    }

                    destinPol = TextUtils.join("", listPoliceStation);
                    polfiltered =destinPol.substring(0,destinPol.length()-1);
                    testTxt.setText(polfiltered);
                    //new PopulateEmergency().execute();

                    Set<String> set = new HashSet<>(nameList);
                    nameList.clear();
                    nameList.addAll(set);

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EmergencyActivity.this, android.R.layout.simple_dropdown_item_1line, nameList);
                    searchEdt.setAdapter(dataAdapter);
                    emergencyAdapter = new EmergencyAdapter(EmergencyActivity.this,listFireM);
                    fireList.setAdapter(emergencyAdapter);

                    fireList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (CheckNetWorkStatus.isNetworkAvailable(EmergencyActivity.this)) {

                                Name2=((TextView) view.findViewById(R.id.txt_police_name_police_list)).getText().toString();
                                latMap=((TextView) view.findViewById(R.id.txt_latitude_police_list)).getText().toString();
                                longMap=((TextView) view.findViewById(R.id.txt_longitude_police_list)).getText().toString();
                                policeContactNumber=((TextView) view.findViewById(R.id.txt_police_contact_list)).getText().toString();

                                LayoutInflater inflater = LayoutInflater.from(EmergencyActivity.this);
                                final View dialogView = inflater.inflate(R.layout.alert_direction_call, null);
                                final AlertDialog dialogBuilder = new AlertDialog.Builder(EmergencyActivity.this).create();
                                dialogBuilder.setView(dialogView);

                                ConstraintLayout directionConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_directions_alert);
                                ConstraintLayout callConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_call_alert);
                                ConstraintLayout cancelConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_cancel_alert);
                                TextView selectNameTxt = (TextView)dialogView.findViewById(R.id.txt_selected_provider);
                                TextView statusTxt = (TextView)dialogView.findViewById(R.id.txt_location_status_dialog);
                                selectNameTxt.setText(Name2);
                                if (locationStatus.contentEquals("Not Captured")){
                                    statusTxt.setBackgroundResource(R.color.red);
                                    statusTxt.setText("Location not Captured!");
                                    statusTxt.startAnimation(animation);
                                }else{
                                    statusTxt.setBackgroundResource(R.color.green);
                                    statusTxt.setText("Location Captured!");
                                    statusTxt.clearAnimation();
                                }

                                directionConstraint.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW).setData(
                                                Uri.parse("https://www.google.com/maps?saddr="+ latMap +"," + longMap + "&daddr=" +
                                                        (latitudeTxt.getText().toString()) + "," + (longitudeTxt.getText().toString())));
                                        startActivity(intent);

                                    }
                                });

                                callConstraint.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent intent1 = new Intent();
                                        intent1.setAction(Intent.ACTION_CALL);
                                        intent1.setData(Uri.parse("tel:" + policeContactNumber));
                                        startActivity (intent1);
                                    }
                                });

                                cancelConstraint.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogBuilder.dismiss();

                                    }
                                });

                                dialogBuilder.show();

                            } else {
                                Toast.makeText(EmergencyActivity.this,
                                        "Unable to connect to internet",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(EmergencyActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                }
            }
        });

    }

    private class PopulateEmergency extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();

            Map<String, String> httpParams = new HashMap<>();
            httpParams.put(KEY_ORIGIN_COORDINATES, myLocation);
            httpParams.put(KEY_DESTINATION_COORDINATES, polfiltered);

            JSONObject jsonObject1 = httpJsonParser.makeHttpRequest(
                    BASE_FINAL_LIST, "POST", httpParams);
            try {
                distanceList = new ArrayList<>();
                success = jsonObject1.getInt(KEY_SUCCESS);
                if(success==1) {

                    JSONArray requests1 = jsonObject1.getJSONArray("data1");
                    JSONObject jsonRespRouteDistance1 = requests1.getJSONObject(0);

                    JSONArray step1 = jsonRespRouteDistance1.getJSONArray("rows");
                    JSONObject step2 = step1.getJSONObject(0);
                    step3 = step2.getJSONArray("elements");

                    for (int i = 0; i < step3.length(); i++) {


                        distance = step3.getJSONObject(i).getJSONObject("distance").get(TAG_DISTANCE).toString();

                        duration = step3.getJSONObject(i).getJSONObject("duration").get(TAG_DURATION).toString();

                        distanceValue = step3.getJSONObject(i).getJSONObject("distance").get(TAG_DISTANCE_VALUE).toString();

                        //JSONObject request = requests.getJSONObject(i);
                        String policeName = listName.get(i);
                        String ambulanceContact = listName2.get(i);
                        String latitudeFinal = listLat.get(i);
                        String longitudeFinal = listLong.get(i);

                        map1 = new HashMap<>();
                        map1.put(KEY_HOSPITAL_NAME, policeName);
                        map1.put(KEY_HOSPITAL_NUMBER, ambulanceContact);
                        map1.put(KEY_HOSPITAL_LATITUDE, latitudeFinal);
                        map1.put(KEY_HOSPITAL_LONGITUDE, longitudeFinal);

                        map1.put(TAG_DISTANCE_VALUE, distanceValue);
                        map1.put(TAG_DISTANCE, distance);
                        distanceList.add(map1);
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


                    map2 = new HashMap<>();
                    map2.put(KEY_HOSPITAL_NAME, "Test Call");
                    map2.put(KEY_HOSPITAL_NUMBER, "000100");
                    map2.put(KEY_HOSPITAL_LATITUDE, Latitude);
                    map2.put(KEY_HOSPITAL_LONGITUDE, Longitude);

                    map2.put(TAG_DISTANCE_VALUE, "5000");
                    map2.put(TAG_DISTANCE, "N/A");

                    class MapComparator implements Comparator<Map<String, String>> {
                        private final String key;

                        public MapComparator(String key){
                            this.key = key;
                        }

                        public int compare(Map<String, String> first,
                                           Map<String, String> second){
                            // TODO: Null checking, both for maps and values
                            String firstValue = first.get(key);
                            String secondValue = second.get(key);
                            return firstValue.compareTo(secondValue);
                        }
                    }
                    Collections.sort(distanceList, new MapComparator(TAG_DISTANCE));

                    baseList.add(map2);
                    for (int i = 0; i < distanceList.size(); i++) {
                        if (Integer.parseInt(distanceList.get(i).get(TAG_DISTANCE_VALUE))<=20000){
                            //listCounter++;
                            baseList.add(distanceList.get(i));
                        }
                    }
                    //Collections.sort(baseList, new MapComparator(TAG_DISTANCE));
                    PopulateList();
                }

            });
        }
    }
    private void PopulateList() {
        ListAdapter adapter = new SimpleAdapter(
                EmergencyActivity.this, distanceList, R.layout.single_ambulance_complete_item,
                new String[]{KEY_HOSPITAL_NAME, KEY_HOSPITAL_NUMBER, TAG_DISTANCE, TAG_DISTANCE_VALUE, KEY_HOSPITAL_LATITUDE, KEY_HOSPITAL_LONGITUDE},
                new int[]{R.id.txt_police_name_police_list, R.id.txt_police_contact_list, R.id.txt_distance_police_list,
                        R.id.txt_time_police_list, R.id.txt_latitude_police_list, R.id.txt_longitude_police_list});
        ambulanceList.setAdapter(adapter);

        //ambulanceList.setVisibility(View.GONE);
        /*completeAmbulanceList.setVisibility(View.VISIBLE);
        descriptionTxt.setText("Providers Within 20 Kms");
        progressTxt.setText("Complete!");
        xRotate.end();

        completeAmbulanceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (permissionAlreadyGranted()) {
                    final String Name2=((TextView) view.findViewById(R.id.txt_police_name_police_list)).getText().toString();
                    latMap=((TextView) view.findViewById(R.id.txt_latitude_police_list)).getText().toString();
                    longMap=((TextView) view.findViewById(R.id.txt_longitude_police_list)).getText().toString();
                    ambulanceContactNumber=((TextView) view.findViewById(R.id.txt_police_contact_list)).getText().toString();

                    LayoutInflater inflater = LayoutInflater.from(MedicalActivity.this);
                    final View dialogView = inflater.inflate(R.layout.alert_responders_call, null);
                    final AlertDialog dialogBuilder = new AlertDialog.Builder(MedicalActivity.this).create();
                    dialogBuilder.setView(dialogView);

                    ConstraintLayout callConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_call_alert);
                    ConstraintLayout cancelConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_cancel_alert);
                    TextView selectNameTxt = (TextView)dialogView.findViewById(R.id.txt_selected_provider);
                    TextView statusTxt = (TextView)dialogView.findViewById(R.id.txt_location_status_dialog);
                    selectNameTxt.setText(Name2);
                    if (locationStatus.contentEquals("Not Captured")){
                        statusTxt.setBackgroundResource(R.color.red);
                        statusTxt.setText("Location not Captured!");
                        statusTxt.startAnimation(animation);
                    }else{
                        statusTxt.setBackgroundResource(R.color.green);
                        statusTxt.setText("Location Captured!");
                        statusTxt.clearAnimation();
                    }


                    callConstraint.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            stationName=Name2;
                            callCounter++;
                            Intent intent1 = new Intent();
                            intent1.setAction(Intent.ACTION_CALL);
                            intent1.setData(Uri.parse("tel:" + ambulanceContactNumber));
                            startActivity (intent1);
                            dialogBuilder.dismiss();

                            callPrompt = "Yes";
                            //new AddIncidence().execute();
                            //PostReportData();
                        }
                    });

                    cancelConstraint.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();

                        }
                    });

                    dialogBuilder.show();
                } else {
                    requestPermission();
                }
            }
        });*/
    }

    private class AddIncidence extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();

            httpParams.put(KEY_ADDRESS, myAddress);
            httpParams.put(KEY_PROVIDER_ID, myid);
            httpParams.put(KEY_TYPE, "Ambulance");
            httpParams.put(KEY_PROVIDER_NAME, stationName);
            httpParams.put(KEY_INCIDENCE_LATITUDE, latMap);
            httpParams.put(KEY_INCIDENCE_LONGITUDE, longMap);
            httpParams.put(KEY_CALLER_ID, myid);
            httpParams.put(KEY_CALLER_PHONE, stationName);

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    ADD_INCIDENCE_URL + "add_incidence.php", "POST", httpParams);
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
                    if (success == 0) {
                        Toast.makeText(EmergencyActivity.this,"Successful",Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(EmergencyActivity.this,"Failed",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    //Location Feature
    private synchronized void setUpGClient() {
        if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, 0, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            getMyLocation();
            googleApiClient.connect();
        } else {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
            if (googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
                googleApiClient.disconnect();
            }
        } else {

        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
    }

    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(EmergencyActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation =                     LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    locationRequest = new LocationRequest();
                    locationRequest.setInterval(20);
                    locationRequest.setFastestInterval(20);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(EmergencyActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(EmergencyActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        googleApiClient.connect();
                        break;
                    case Activity.RESULT_CANCELED:

                        //finish();
                        break;
                }
                break;
        }
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(EmergencyActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            getMyLocation();
        }

    }

    private void CheckGpsStatus() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onLocationChanged(final Location location) {
        mylocation = location;
        if (mylocation != null) {
            Double latitude=mylocation.getLatitude();
            Double longitude=mylocation.getLongitude();
            latitudeTxt.setText(Double.toString(latitude));
            longitudeTxt.setText(Double.toString(longitude));

            Latitude=latitudeTxt.getText().toString();
            Longitude=longitudeTxt.getText().toString();
            //myLocation=" " + Latitude + ", " + Longitude;

            latitudeTxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    counter++;
                    locationStatus="Captured";
                    Latitude=latitudeTxt.getText().toString();
                    Longitude=longitudeTxt.getText().toString();
                    //myLocation=" " + Latitude + ", " + Longitude;
                    //new PopulateEmergency().execute();

                    if(counter==1){
                        //new PopulateEmergency().execute();
                        //myLocation=" " + Latitude + ", " + Longitude;
                    }
                    if (counter==3) {

                    }

                    if (counter>4) {
                        /*if (callCounter>0){
                            UpdateLocation();
                        }*/
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    private void ProcessResult(){

        for(int i=0; i<fullList.size(); i++){
            if(fullList.get(i).getName().contentEquals(selectedStation)){
                stationLat=fullList.get(i).getLatitude();
                stationLong=fullList.get(i).getLongitude();
                stationPhone=fullList.get(i).getContact();
            }
        }
        searchResultCons.setVisibility(View.VISIBLE);
        nameTxt.setText(selectedStation);
    }

    private class AddProvider extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();

            httpParams.put(KEY_PROVIDER_ADDRESS, "");
            httpParams.put(KEY_NAME, Name2);
            httpParams.put(KEY_PHONE, policeContactNumber);
            httpParams.put(KEY_PROVIDER_LATITUDE, latMap);
            httpParams.put(KEY_PROVIDER_LONGITUDE, longMap);
            httpParams.put(KEY_PROVIDER_TYPE, providerType);
            httpParams.put(KEY_STATUS, "Active");

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    ADD_INCIDENCE_URL + "add_provider.php", "POST", httpParams);
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
                    if (success == 0) {
                        Toast.makeText(EmergencyActivity.this,"Provider Added",Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(EmergencyActivity.this,"Provider Adding Failed",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
