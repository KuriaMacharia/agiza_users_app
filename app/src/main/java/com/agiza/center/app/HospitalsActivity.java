package com.agiza.center.app;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import com.agiza.center.app.Helper.CheckNetWorkStatus;
import com.agiza.center.app.Helper.HttpJsonParser;
import com.google.android.gms.location.LocationListener;

import android.net.Uri;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
import java.util.List;
import java.util.Map;


public class HospitalsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String KEY_ORIGIN_COORDINATES = "origin_matrix";
    private static final String KEY_DESTINATION_COORDINATES = "destination_matrix";
    static final String KEY_LATITUDE = "latitude";
    static final String KEY_LONGITUDE = "longitude";

    static final String KEY_HOSPITAL_NUMBER = "contact";
    static final String KEY_HOSPITAL_NAME = "name";
    static final String KEY_HOSPITAL_LATITUDE = "latitude";
    static final String KEY_HOSPITAL_LONGITUDE = "longitude";

    private static final String BASE_URL_POLICE = "http://www.palecocenter.co.ke/kush/police/fetch_all_police.php";
    private static final String BASE_FINAL_LIST = "https://centerforty.com/apps/final_police_list.php";
    private static final String BASE_INCIDENCE_REGISTER = "http://www.palecocenter.co.ke/kush/police/register_incidence.php";
    static final String TAG_DURATION = "text";
    static final String TAG_DISTANCE = "text";
    static final String TAG_DISTANCE_VALUE = "value";


    private static final String KEY_SUCCESS = "success";

    private static final String KEY_DATA = "data";
    private static final String KEY_DATA1 = "data1";
    private static final String BASE_URL = "http://www.palecocenter.co.ke/kush/volunteer/";

    String[] county = {"---select---", "Mombasa", "Kwale", "Kilifi", "Tana River", "Lamu", "Taita Taveta", "Garissa", "Wajir", "Mandera",
            "Marsabit", "Isiolo", "Meru", "Tharaka Nithi", "Embu", "Kitui", "Machakos", "Makueni", "Nyandarua", "Nyeri", "Kirinyaga",
            "Murang'a", "Kiambu", "Turkana", "West Pokot", "Samburu", "Trans Nzoia", "Uasin Gishu", "Elgeyo/Marakwet", "Nandi", "Baringo",
            "Laikipia", "Nakuru", "Narok", "Kajiado", "Kericho", "Bomet", "Kakamega", "Vihiga", "Bungoma", "Busia", "Siaya", "Kisumu",
            "Homa Bay", "Migori", "Kisii", "Nyamira", "Nairobi City"
    };

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String firstName1 = "firstNameKey";
    public static final String lastName1= "lastNameKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String Password1 = "passwordKey";
    public static final String myid1 = "myidKey";

    String firstName, lastName, Phone, Email, Password, myid, visitType;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    String destin, distance, duration, testDis, testDur, filtered, filtered1, filtered2, filtered3,
            destinPol, polfiltered, polfiltered1, polfiltered2, polfiltered3;

    String distanceValue, policeContact;
    String latMap, longMap, policeContactNumber;

    TextView latitudeTxt, longitudeTxt, countTxt, locationTxt, hospitalsTxt;
    String Latitude, Longitude, locationStatus;
    int textCount;
    ListView policeList, initialList;

    private ArrayList<HashMap<String, String>> distanceListPolice;
    private ArrayList<HashMap<String, String>> distanceList, baseList;
    HashMap<String, String> map1, map2;
    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    LocationRequest locationRequest;
    int success, counter;
    Animation animation;
    JSONArray step3;
    Activity activity;
    LayoutInflater inflater = null;
    FirebaseFirestore db;
    private ArrayList<String> listPoliceStation, listName, listName2, listLat, listLong;
    PoliceStation miss;
    ArrayList<PoliceStation> listInitialHospital;
    ImageView homeImg;
    private ProgressDialog pDialog;

    Animation animSequence;
    private AnimatorSet mSetRightOut, mSetLeftIn, xRotate;
    ImageView gpsSearchImg, gpsOffimg, locationCapturedImg, listImg;
    TextView progressTxt, locationProgressTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_hospitals);

        db = FirebaseFirestore.getInstance();
        listPoliceStation= new ArrayList<String>();
        listName= new ArrayList<String>();
        listName2= new ArrayList<String>();
        listLat= new ArrayList<String>();
        listLong= new ArrayList<String>();
        listInitialHospital= new ArrayList<PoliceStation>();
        baseList = new ArrayList<>();

        pDialog = new ProgressDialog(HospitalsActivity.this);
        pDialog.setMessage("Fetching Hospitals. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Password = sharedpreferences.getString(Password1,"");
        Email = sharedpreferences.getString(Email1,"");
        Phone = sharedpreferences.getString(phoneNumber1,"");
        firstName = sharedpreferences.getString(firstName1,"");
        lastName = sharedpreferences.getString(lastName1,"");
        myid = sharedpreferences.getString(myid1,"");

        visitType="Hospital";
        locationStatus="Not Captured";

        homeImg=(ImageView) findViewById(R.id.img_home);
        latitudeTxt = (TextView) findViewById(R.id.txt_latitude_fire);
        longitudeTxt = (TextView) findViewById(R.id.txt_longitude_fire);
        countTxt = (TextView) findViewById(R.id.txt_change_count_fire);
        locationTxt = (TextView) findViewById(R.id.txt_latitude_longitude_police_emergency);
        hospitalsTxt = (TextView) findViewById(R.id.txt_police_coordinates_emergency);

        policeList = (ListView) findViewById(R.id.list_police_stations_police);
        initialList = (ListView) findViewById(R.id.list_initial_hospital_list);

        listImg = (ImageView) findViewById(R.id.img_list_progress_bottom);
        progressTxt=(TextView) findViewById(R.id.txt_progress_bottom);
        locationProgressTxt=(TextView) findViewById(R.id.txt_location_progress_bottom);
        gpsSearchImg = (ImageView) findViewById(R.id.img_location_searching);
        locationCapturedImg = (ImageView) findViewById(R.id.img_location_captured);
        gpsOffimg = (ImageView) findViewById(R.id.img_gps_off);

        animSequence = AnimationUtils.loadAnimation(HospitalsActivity.this,R.anim.rotate );
        gpsSearchImg.startAnimation(animSequence);

        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(HospitalsActivity.this, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(HospitalsActivity.this, R.animator.in_animation);
        xRotate = (AnimatorSet) AnimatorInflater.loadAnimator(HospitalsActivity.this, R.animator.horizontal_rotate);
        progressTxt.setText("Initiating Hospitals List...");

        xRotate.setTarget(listImg);
        xRotate.start();

        animation = new AlphaAnimation((float) 0, 1);
        animation.setDuration(400);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        setUpGClient();


        FetchAllStations();

        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HospitalsActivity.this, HomeActivity.class));
            }
        });
    }

    private void FetchAllStations(){
        db.collection("hospitals").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        miss = document.toObject(PoliceStation.class);
                        listInitialHospital.add(miss);
                        listPoliceStation.add(miss.getLatitude()+ ", ");
                        listPoliceStation.add(miss.getLongitude() + "|");
                        listName.add(miss.getName());
                        listName2.add(miss.getContact());
                        listLat.add(miss.getLatitude());
                        listLong.add(miss.getLongitude());
                    }
                    HospitalsAdapter ria = new HospitalsAdapter(HospitalsActivity.this,listInitialHospital);
                    initialList.setAdapter(ria);
                    progressTxt.setText("Fetching Distance...");
                    pDialog.dismiss();

                    destinPol = TextUtils.join("", listPoliceStation);
                    polfiltered =destinPol.substring(0,destinPol.length()-1);
                    hospitalsTxt.setText(polfiltered);

                    initialList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            TextView nameTxt = (TextView)view.findViewById(R.id.txt_police_name_police_list);
                            final TextView contactTxt1 = (TextView)view.findViewById(R.id.txt_police_contact_list);
                            final TextView latitudeTxt1 = (TextView)view.findViewById(R.id.txt_latitude_police_list);
                            final TextView longitudeTxt1 = (TextView)view.findViewById(R.id.txt_longitude_police_list);

                            LayoutInflater inflater = LayoutInflater.from(HospitalsActivity.this);
                            final View dialogView = inflater.inflate(R.layout.alert_direction_call, null);
                            final AlertDialog dialogBuilder = new AlertDialog.Builder(HospitalsActivity.this).create();
                            dialogBuilder.setView(dialogView);

                            ConstraintLayout directionConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_directions_alert);
                            ConstraintLayout callConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_call_alert);
                            ConstraintLayout cancelConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_cancel_alert);
                            TextView selectNameTxt = (TextView)dialogView.findViewById(R.id.txt_selected_provider);
                            TextView statusTxt = (TextView)dialogView.findViewById(R.id.txt_location_status_dialog);
                            selectNameTxt.setText(nameTxt.getText().toString());
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
                                                    (latitudeTxt1.getText().toString()) + "," + (longitudeTxt1.getText().toString())));
                                    startActivity(intent);
                                }
                            });

                            callConstraint.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent1 = new Intent();
                                    intent1.setAction(Intent.ACTION_CALL);
                                    intent1.setData(Uri.parse("tel:" + contactTxt1.getText().toString()));
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
                        }
                    });

                } else {
                    pDialog.dismiss();
                    Toast.makeText(HospitalsActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        getMyLocation();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
            googleApiClient.disconnect();
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
                int permissionLocation = ContextCompat.checkSelfPermission(HospitalsActivity.this,
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
                                            .checkSelfPermission(HospitalsActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);

                                        progressTxt.setText("Configuring List...");
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(HospitalsActivity.this,
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
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
        }
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(HospitalsActivity.this,
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(HospitalsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }
    @Override
    public void onLocationChanged(final Location location) {
        mylocation = location;
        if (mylocation != null) {
            Double latitude=mylocation.getLatitude();
            Double longitude=mylocation.getLongitude();
            latitudeTxt.setText(Double.toString(latitude));
            longitudeTxt.setText(Double.toString(longitude));

            latitudeTxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    counter++;
                    locationStatus="Captured";
                    textCount=latitudeTxt.getText().length();
                    countTxt.setText(String.valueOf(textCount));

                    Latitude=latitudeTxt.getText().toString();
                    Longitude=longitudeTxt.getText().toString();

                    if(counter==1){
                        locationCapturedImg.setVisibility(View.VISIBLE);
                        mSetRightOut.setTarget(gpsSearchImg);
                        mSetLeftIn.setTarget(locationCapturedImg);
                        mSetRightOut.start();
                        mSetLeftIn.start();
                        progressTxt.setText("Compiling Distance List...");
                        locationProgressTxt.setText("Located!");
                        locationProgressTxt.setTextColor(getResources().getColor(R.color.green));
                    }

                    if (counter==3) {
                        new PopulateEmergency().execute();
                    }
                    locationTxt.setText(" " + Latitude + ", " + Longitude);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

        }
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
            httpParams.put(KEY_ORIGIN_COORDINATES, locationTxt.getText().toString());
            httpParams.put(KEY_DESTINATION_COORDINATES, hospitalsTxt.getText().toString());


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

                        distanceValue = step3.getJSONObject(i).getJSONObject("duration").get(TAG_DURATION).toString();

                        //JSONObject request = requests.getJSONObject(i);
                        String policeName = listName.get(i);
                        policeContact = listName2.get(i);
                        String latitudeFinal = listLat.get(i);
                        String longitudeFinal = listLong.get(i);

                        map1 = new HashMap<>();
                        map1.put(KEY_HOSPITAL_NAME, policeName);
                        map1.put(KEY_HOSPITAL_NUMBER, policeContact);
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

                    map2.put(TAG_DISTANCE_VALUE, "N/A");
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
                        baseList.add(distanceList.get(i));
                    }

                    ListAdapter adapter = new SimpleAdapter(
                            HospitalsActivity.this, baseList, R.layout.single_hospital_item,
                            new String[]{KEY_HOSPITAL_NAME, KEY_HOSPITAL_NUMBER, TAG_DISTANCE, TAG_DISTANCE_VALUE, KEY_HOSPITAL_LATITUDE, KEY_HOSPITAL_LONGITUDE},
                            new int[]{R.id.txt_police_name_police_list, R.id.txt_police_contact_list, R.id.txt_distance_police_list,
                                    R.id.txt_time_police_list, R.id.txt_latitude_police_list, R.id.txt_longitude_police_list});
                    policeList.setAdapter(adapter);

                    initialList.setVisibility(View.GONE);
                    policeList.setVisibility(View.VISIBLE);
                    progressTxt.setText("Complete!");
                    xRotate.end();

                    policeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (CheckNetWorkStatus.isNetworkAvailable(HospitalsActivity.this)) {

                                String Name2=((TextView) view.findViewById(R.id.txt_police_name_police_list)).getText().toString();
                                latMap=((TextView) view.findViewById(R.id.txt_latitude_police_list)).getText().toString();
                                longMap=((TextView) view.findViewById(R.id.txt_longitude_police_list)).getText().toString();
                                policeContactNumber=((TextView) view.findViewById(R.id.txt_police_contact_list)).getText().toString();

                                LayoutInflater inflater = LayoutInflater.from(HospitalsActivity.this);
                                final View dialogView = inflater.inflate(R.layout.alert_direction_call, null);
                                final AlertDialog dialogBuilder = new AlertDialog.Builder(HospitalsActivity.this).create();
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
                                Toast.makeText(HospitalsActivity.this,
                                        "Unable to connect to internet",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            });
        }
    }
}

