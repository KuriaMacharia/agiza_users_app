package com.agiza.center.app;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.agiza.center.app.Helper.CheckNetWorkStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;

public class MedicalActivity extends AppCompatActivity implements OtherAmbulanceAdapter.OtherAmbulanceCallBack,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    static final String KEY_PROVIDER_ID = "provider_id";
    static final String KEY_TYPE = "type";
    static final String KEY_PROVIDER_NAME = "provider_name";
    static final String KEY_ADDRESS = "address";
    static final String KEY_INCIDENCE_LATITUDE = "latitude";
    static final String KEY_INCIDENCE_LONGITUDE = "longitude";
    static final String KEY_CALLER_ID = "caller_id";
    static final String KEY_CALLER_PHONE = "caller_phone_number";

    private static final String ADD_INCIDENCE_URL = "http://www.agizakenya.com/agiza/";

    private static final String KEY_ORIGIN_COORDINATES = "origin_matrix";
    private static final String KEY_DESTINATION_COORDINATES = "destination_matrix";

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

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String firstName1 = "firstNameKey";
    public static final String lastName1= "lastNameKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String Password1 = "passwordKey";
    public static final String myid1 = "myidKey";

    String firstName, lastName, Phone, Email, Password, myid, visitType, callPrompt, Latitude, Longitude, stationName, stationId,
            myAddress;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    ConstraintLayout callConstraint, contactKovCons;
    TextView nearestTxt, latitudeTxt, longitudeTxt, mainProviderTxt;

    FirebaseFirestore db;
    Ambulance miss;
    private ArrayList<String> listAmbulance, listAmbulanceN;
    private ArrayList<String> listAmbulanceStation, listName, listName2, listLat, listLong;
    String destin, distance, duration, testDis, testDur, filtered, filtered1, filtered2, filtered3,
            destinPol, polfiltered, polfiltered1, polfiltered2, polfiltered3, myLocation;
    String distanceValue, ambulanceContact;
    private ArrayList<HashMap<String, String>> distanceList, baseList;
    JSONArray step3;
    HashMap<String, String> map1, map2;
    String latMap, longMap, ambulanceContactNumber;

    String mainNumber, phoneNumber, locationStatus;
    ListView ambulanceList, completeAmbulanceList;
    ArrayList<Ambulance> listOtherAmbulance;
    int success, counter, callCounter, listCounter;
    private Location mylocation;
    ImageView homeImg;

    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    LocationRequest locationRequest;
    Resources.Theme myTheme;
    Context context;
    LocationManager locationManager;
    boolean GpsStatus = false;
    Animation animation;
    Animation animSequence;
    private AnimatorSet mSetRightOut, mSetLeftIn, xRotate;
    ImageView gpsSearchImg, gpsOffimg, locationCapturedImg, listImg;
    TextView progressTxt, locationProgressTxt, descriptionTxt;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_medical);
        myTheme = this.getTheme();

        db = FirebaseFirestore.getInstance();
        listAmbulance = new ArrayList<String>();
        listAmbulanceN = new ArrayList<String>();
        listOtherAmbulance = new ArrayList<Ambulance>();

        listAmbulanceStation= new ArrayList<String>();
        listName= new ArrayList<String>();
        listName2= new ArrayList<String>();
        listLat= new ArrayList<String>();
        listLong= new ArrayList<String>();
        baseList = new ArrayList<>();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        context = getApplicationContext();

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Password = sharedpreferences.getString(Password1, "");
        Email = sharedpreferences.getString(Email1, "");
        Phone = sharedpreferences.getString(phoneNumber1, "");
        firstName = sharedpreferences.getString(firstName1, "");
        lastName = sharedpreferences.getString(lastName1, "");
        myid = sharedpreferences.getString(myid1, "");

        visitType = "Medical";
        locationStatus="Not Captured";

        contactKovCons = (ConstraintLayout) findViewById(R.id.cons_kovid_contacts);
        descriptionTxt=(TextView) findViewById(R.id.txt_list_ambulance_description);
        callConstraint = (ConstraintLayout) findViewById(R.id.constraint_call_main_ambulance);
        nearestTxt = (TextView) findViewById(R.id.txt_nearest_hospital_medical);
        ambulanceList = (ListView) findViewById(R.id.list_other_ambulances);
        completeAmbulanceList= (ListView) findViewById(R.id.list_complete_ambulance);
        homeImg = (ImageView) findViewById(R.id.img_home);

        listImg = (ImageView) findViewById(R.id.img_list_progress_bottom);
        progressTxt=(TextView) findViewById(R.id.txt_progress_bottom);
        locationProgressTxt=(TextView) findViewById(R.id.txt_location_progress_bottom);
        gpsSearchImg = (ImageView) findViewById(R.id.img_location_searching);
        locationCapturedImg = (ImageView) findViewById(R.id.img_location_captured);
        gpsOffimg = (ImageView) findViewById(R.id.img_gps_off);

        animSequence = AnimationUtils.loadAnimation(MedicalActivity.this,R.anim.rotate );
        gpsSearchImg.startAnimation(animSequence);

        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(MedicalActivity.this, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(MedicalActivity.this, R.animator.in_animation);
        xRotate = (AnimatorSet) AnimatorInflater.loadAnimator(MedicalActivity.this, R.animator.horizontal_rotate);
        progressTxt.setText("Initiating Ambulance List...");

        xRotate.setTarget(listImg);
        xRotate.start();

        latitudeTxt = (TextView) findViewById(R.id.txt_latitude_medical);
        longitudeTxt = (TextView) findViewById(R.id.txt_longitude_medical);
        mainProviderTxt = (TextView) findViewById(R.id.txt_selected_ambulance_medical);

        animation = new AlphaAnimation((float) 0, 1);
        animation.setDuration(400);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);

        setUpGClient();
        CheckGpsStatus();

        if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
            FetchAmbulance();
            FetchOtherAmbulance();
        } else {
            Toast.makeText(MedicalActivity.this,
                    "No Internet Connection!",
                    Toast.LENGTH_LONG).show();
        }


        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MedicalActivity.this, HomeActivity.class));
            }
        });

        contactKovCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MedicalActivity.this, KovidActivity.class));
            }
        });

        nearestTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionAlreadyGranted()) {
                    CheckGpsStatus();
                    if (GpsStatus) {
                        if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                            startActivity(new Intent(MedicalActivity.this, HospitalsActivity.class));
                        } else {
                            Toast.makeText(MedicalActivity.this,
                                    "Unable to connect to internet",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        startActivity(new Intent(MedicalActivity.this, MedicalActivity.class));
                        Toast.makeText(MedicalActivity.this, "Please Enable GPS First", Toast.LENGTH_LONG).show();
                    }
                } else {
                    requestPermission();
                }
            }
        });

        callConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionAlreadyGranted()) {
                        LayoutInflater inflater = LayoutInflater.from(MedicalActivity.this);
                        final View dialogView = inflater.inflate(R.layout.alert_responders_call, null);
                        final AlertDialog dialogBuilder = new AlertDialog.Builder(MedicalActivity.this).create();
                        dialogBuilder.setView(dialogView);

                        ConstraintLayout callConstraint = (ConstraintLayout) dialogView.findViewById(R.id.constraint_call_alert);
                        ConstraintLayout cancelConstraint = (ConstraintLayout) dialogView.findViewById(R.id.constraint_cancel_alert);

                        TextView selectNameTxt = (TextView)dialogView.findViewById(R.id.txt_selected_provider);
                        TextView statusTxt = (TextView)dialogView.findViewById(R.id.txt_location_status_dialog);
                        selectNameTxt.setText(mainProviderTxt.getText().toString());
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
                                callCounter++;

                                Intent intent1 = new Intent();
                                intent1.setAction(Intent.ACTION_CALL);
                                intent1.setData(Uri.parse("tel:" + mainNumber));
                                startActivity(intent1);
                                dialogBuilder.dismiss();

                                callPrompt = "Yes";
                                PostReportData();
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

        });
    }


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
                int permissionLocation = ContextCompat.checkSelfPermission(MedicalActivity.this,
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
                                            .checkSelfPermission(MedicalActivity.this,
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
                                        status.startResolutionForResult(MedicalActivity.this,
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
                        //startActivity(new Intent(MedicalActivity.this, MedicalActivity.class));
                        break;
                    case Activity.RESULT_CANCELED:
                        gpsOffimg.setVisibility(View.VISIBLE);
                        mSetRightOut.setTarget(gpsSearchImg);
                        mSetLeftIn.setTarget(gpsOffimg);
                        mSetRightOut.start();
                        mSetLeftIn.start();
                        locationProgressTxt.setText("GPS Off");
                        locationProgressTxt.setTextColor(getResources().getColor(R.color.gpsoff));

                        //finish();
                        break;
                }
                break;
        }
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(MedicalActivity.this,
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

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MedicalActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }*/
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
                    Latitude=latitudeTxt.getText().toString();
                    Longitude=longitudeTxt.getText().toString();

                    if(counter==1){
                        locationCapturedImg.setVisibility(View.VISIBLE);
                        mSetRightOut.setTarget(gpsSearchImg);
                        mSetLeftIn.setTarget(locationCapturedImg);
                        mSetRightOut.start();
                        mSetLeftIn.start();
                        locationProgressTxt.setText("Located!");
                        locationProgressTxt.setTextColor(getResources().getColor(R.color.green));
                    }
                    if (counter==3) {
                        new PopulateEmergency().execute();
                        myLocation=" " + Latitude + ", " + Longitude;
                        progressTxt.setText("Compiling Distance List...");

                    }

                    if (counter>4) {
                        if (callCounter>0){
                                UpdateLocation();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

        }
    }


    private void FetchAmbulance(){
        db.collection("ambulance").whereEqualTo("category", "main").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        miss = document.toObject(Ambulance.class);
                        listAmbulance.add(miss.getContact());
                        listAmbulanceN.add(miss.getName());
                    }
                    mainNumber =listAmbulance.get(0);
                    stationName=listAmbulance.get(0);

                } else {
                    Toast.makeText(MedicalActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void FetchOtherAmbulance(){
        db.collection("ambulance").whereEqualTo("category", "sub").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        miss = document.toObject(Ambulance.class);
                        listOtherAmbulance.add(miss);
                        listAmbulanceStation.add(miss.getLatitude()+ ", ");
                        listAmbulanceStation.add(miss.getLongitude() + "|");
                        listName.add(miss.getName());
                        listName2.add(miss.getContact());
                        listLat.add(miss.getLatitude());
                        listLong.add(miss.getLongitude());
                    }
                    destinPol = TextUtils.join("", listAmbulanceStation);
                    polfiltered =destinPol.substring(0,destinPol.length()-1);

                    OtherAmbulanceAdapter ria = new OtherAmbulanceAdapter(MedicalActivity.this,listOtherAmbulance);
                    ambulanceList.setAdapter(ria);
                    descriptionTxt.setText("All Ambulance Providers");
                    progressTxt.setText("Fetching Distance...");

                    ambulanceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            Toast.makeText(MedicalActivity.this, "List not ready yet. Please wait a few seconds...", Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    Toast.makeText(MedicalActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void CallAmbulance(int position) {

    }

    private void PostReportData(){
        Date currentDate = new Date();
        Map<String, Object> emergency = new HashMap<>();
        emergency.put("victimnumber", Phone);
        emergency.put("stationcode", stationName);
        emergency.put("latitude", Latitude);
        emergency.put("longitude", Longitude);
        emergency.put("time", currentDate);
        emergency.put("callertype", "GPS Enabled");
        emergency.put("incidencetype", "");
        emergency.put("status", "Open");
        emergency.put("myid", "");


        db.collection("emergency_medical")
                .add(emergency)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        myid=documentReference.getId();
                        UpdateId();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }

    private void UpdateId(){
        Map<String, Object> emergency2 = new HashMap<>();
        emergency2.put("myid", myid);


        db.collection("emergency_medical").document(myid)
                .set(emergency2, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void UpdateLocation(){
        Map<String, Object> emergency3 = new HashMap<>();
        emergency3.put("latitude", Latitude);
        emergency3.put("longitude", Longitude);


        db.collection("emergency_medical").document(myid)
                .set(emergency3, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CALL_PHONE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean callAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && callAccepted)
                        Toast.makeText(MedicalActivity.this, "", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(MedicalActivity.this, "Permission Denied, You cannot access location data and call.", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{ACCESS_FINE_LOCATION, CALL_PHONE},
                                                        PERMISSION_REQUEST_CODE);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }
                break;
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(MedicalActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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
                        ambulanceContact = listName2.get(i);
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
                            listCounter++;
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
                    MedicalActivity.this, baseList, R.layout.single_ambulance_complete_item,
                    new String[]{KEY_HOSPITAL_NAME, KEY_HOSPITAL_NUMBER, TAG_DISTANCE, TAG_DISTANCE_VALUE, KEY_HOSPITAL_LATITUDE, KEY_HOSPITAL_LONGITUDE},
                    new int[]{R.id.txt_police_name_police_list, R.id.txt_police_contact_list, R.id.txt_distance_police_list,
                            R.id.txt_time_police_list, R.id.txt_latitude_police_list, R.id.txt_longitude_police_list});
            completeAmbulanceList.setAdapter(adapter);

            ambulanceList.setVisibility(View.GONE);
            completeAmbulanceList.setVisibility(View.VISIBLE);
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
                                new AddIncidence().execute();
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
            });
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
                        Toast.makeText(MedicalActivity.this,"Successful",Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(MedicalActivity.this,"Failed",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
