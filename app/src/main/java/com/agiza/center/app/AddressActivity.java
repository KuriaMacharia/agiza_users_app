package com.agiza.center.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static java.util.Arrays.asList;

public class AddressActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DATA = "data";
    private static final String KEY_ADDRESS_NAME = "name";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    private static final String KEY_MIN_LATITUDE = "min_lat";
    private static final String KEY_MIN_LONGITUDE = "min_long";
    private static final String KEY_MAX_LATITUDE = "max_lat";
    private static final String KEY_MAX_LONGITUDE = "max_long";
    private static final String KEY_SUCCESS = "success";

    private static final String BASE_URL = "http://anwani.net/seya/";

    private final String COLLECTION_KEY = "address";
    ListView addressList, neighborList;
    private ArrayList<Address> listAddress, searchList;
    private AddressAdapter addressAdapter;

    ConstraintLayout findmeContraint, constraintCaptured, neighborCons, cancelCons, saveCons, shareCons, resultsCons,
                        cancelSearchCons, directionSearchCons;
    TextView coordinatestxt, resultsTxt, failureTxt, searchResultsTxt;
    FirebaseFirestore db;
    ImageView searchImg;
    EditText searchEdt;

    private Location mylocation;
    ImageView homeImg;

    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private static final int PERMISSION_REQUEST_CODE = 200;
    LocationRequest locationRequest;
    Resources.Theme myTheme;
    Context context;
    LocationManager locationManager;
    boolean GpsStatus = false;
    FirebaseApp app;

    Double lat1, lat2, lon1, lon2, reLat1, reLat2, reLong1, reLong2;
    List<String> listDistance;
    ArrayList<String> listAllDistance, listReDistance;
    List<String> list1;
    List<String> list2;
    List<String> list3;
    ArrayList<String> list4;
    ArrayList<Double> lisLat, resultLat, latitudeList;
    ArrayList<Double> lisLon, resultLong, longitudeList;
    ArrayAdapter<String> leftList;
    Double diffMax, latitude;
    int mCounter, success;
    String fullAddress, Latitude, Longitude, absoluteLat, reLatitude, reLongitude, searchLat, searchLong, myLat, myLong,
            myAddress, myAddressName, myLatitude, myLongitude;
    Double angleRadius, distanceRadius, maxLat, minLat, maxLong, minLong, curLat1, bsLong, curLat, curLong, endLat, endLong;
    List<Address> longFilterList, latFilterList;
    List<Address> commonList;
    ArrayList<Address> filteredList, newList;
    ArrayList<HashMap<String, String>> distanceListRe, distanceListReNe, duplicateList;
    private ProgressDialog pDialog, startDialog;
    URL lin;

    SharedPreferences.Editor editor;
    SharedPreferences pref;
    int themeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //themeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_address);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:441824650755:android:e2f631f1ac9053e5a65036") // Required for Analytics.
                .setApiKey("AIzaSyAPHHGyhjmI9-CYais0soE4HDzxrSey6kk") // Required for Auth.
                .setDatabaseUrl("https://anwani-d49f0.firebaseio.com/")
                .setProjectId("anwani-d49f0")// Required for RTDB.
                .build();
        FirebaseApp.initializeApp(this /* Context */, options, "secondary");
        app = FirebaseApp.getInstance("secondary");
        db = FirebaseFirestore.getInstance(app);

        /*if(!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.getInstance().delete();

        }else{
            app = FirebaseApp.getInstance("secondary");
            db = FirebaseFirestore.getInstance(app);
        }

        if(FirebaseApp.getInstance("secondary").getName().isEmpty()){
            app = FirebaseApp.getInstance("secondary");
            db = FirebaseFirestore.getInstance(app);
        }*/

        startDialog = new ProgressDialog(AddressActivity.this);
        startDialog.setMessage("Preparing platform. Please wait...");
        startDialog.setIndeterminate(false);
        startDialog.setCancelable(false);
        startDialog.show();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        context = getApplicationContext();
        setUpGClient();
        CheckGpsStatus();
        listAddress = new ArrayList<Address>();
        searchList=new ArrayList<Address>();
        commonList = new ArrayList<Address>();
        filteredList = new ArrayList<Address>();
        newList = new ArrayList<Address>();
        longFilterList = new ArrayList<>();
        latFilterList = new ArrayList<>();
        distanceListRe = new ArrayList<>();
        distanceListReNe=new ArrayList<>();
        duplicateList=new ArrayList<>();

        listDistance = new ArrayList<String>();
        listAllDistance = new ArrayList<String>();
        listReDistance = new ArrayList<String>();
        lisLat =new ArrayList<Double>();
        lisLon =new ArrayList<Double>();
        resultLat =new ArrayList<Double>();
        resultLong =new ArrayList<Double>();
        latitudeList =new ArrayList<Double>();
        longitudeList =new ArrayList<Double>();

        list1 = new ArrayList<>();
        list2 =new ArrayList<>();
        list3 =new ArrayList<>();
        list4 =new ArrayList<>();

        listAddress = new ArrayList<Address>();
        addressList=(ListView) findViewById(R.id.list_address_address);
        neighborList=(ListView) findViewById(R.id.list_neighbor_address);
        coordinatestxt=(TextView) findViewById(R.id.txt_coordinates_address);
        findmeContraint=(ConstraintLayout) findViewById(R.id.constraint_check_address);
        constraintCaptured=(ConstraintLayout) findViewById(R.id.constraint_address_captured);
        neighborCons=(ConstraintLayout) findViewById(R.id.constraint_neighbor);
        cancelCons=(ConstraintLayout) findViewById(R.id.constraint_cancel_address);
        saveCons=(ConstraintLayout) findViewById(R.id.constraint_save_address);
        shareCons=(ConstraintLayout) findViewById(R.id.constraint_share_address);
        resultsTxt=(TextView) findViewById(R.id.txt_captured_address);
        failureTxt=(TextView) findViewById(R.id.txt_failed_to_capture);
        searchEdt=(EditText) findViewById(R.id.edt_search_address);
        searchImg=(ImageView) findViewById(R.id.img_search);
        resultsCons=(ConstraintLayout) findViewById(R.id.constraint_result_search);
        cancelSearchCons=(ConstraintLayout) findViewById(R.id.constraint_search_cancel);
        directionSearchCons=(ConstraintLayout) findViewById(R.id.constraint_direction_search);
        searchResultsTxt=(TextView) findViewById(R.id.txt_search_results);

        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                    pDialog = new ProgressDialog(AddressActivity.this);
                    pDialog.setMessage("Searching. Please wait...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    failureTxt.setVisibility(View.GONE);
                    constraintCaptured.setVisibility(View.GONE);
                    neighborCons.setVisibility(View.GONE);

                    db.collection(COLLECTION_KEY).whereEqualTo("fulladdress", searchEdt.getText().toString())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document : task.getResult()) {
                                    Address miss = document.toObject(Address.class);
                                    searchList.add(miss);
                                    searchResultsTxt.setText(searchList.get(0).getFulladdress());
                                    searchLat=String.valueOf(Double.parseDouble(searchList.get(0).getLatitude())-100);
                                    searchLong=searchList.get(0).getLongitude();

                                    findmeContraint.setVisibility(View.GONE);
                                    resultsCons.setVisibility(View.VISIBLE);
                                    failureTxt.setVisibility(View.GONE);
                                    pDialog.dismiss();
                                }

                            } else {
                                failureTxt.setVisibility(View.VISIBLE);
                                failureTxt.setText("Address not found!");
                                pDialog.dismiss();
                            }
                        }
                    });

                } else {
                    Toast.makeText(AddressActivity.this,"Unable to connect to internet",Toast.LENGTH_LONG).show();
                }
            }
        });

        findmeContraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(distanceListRe.size()>0){
                    distanceListRe.clear();
                }

                if (permissionAlreadyGranted()) {
                    CheckGpsStatus();
                    if (GpsStatus) {
                        if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                            googleApiClient.connect();
                            StartScan();

                            pDialog = new ProgressDialog(AddressActivity.this);
                            pDialog.setMessage("Initiating Scan. Please wait...");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(false);
                            pDialog.show();

                            failureTxt.setVisibility(View.GONE);
                            constraintCaptured.setVisibility(View.GONE);
                            neighborCons.setVisibility(View.GONE);

                            if (commonList.size()>0){
                                commonList.clear();
                            }

                        } else {
                            Toast.makeText(AddressActivity.this,"Unable to connect to internet",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AddressActivity.this, "Please Enable GPS First", Toast.LENGTH_LONG).show();
                    }
                }else{
                    requestPermission();
                }

            }
        });

        saveCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                contactIntent
                        .putExtra(ContactsContract.Intents.Insert.NAME, "Home Address")
                        .putExtra(ContactsContract.Intents.Insert.PHONE, resultsTxt.getText().toString());

                startActivityForResult(contactIntent, 1);
            }
        });

        shareCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                String uri = "http://maps.google.com/maps?daddr=" + String.valueOf(Double.parseDouble(reLatitude)-100)+","+reLongitude;

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String ShareSub = resultsTxt.getText().toString();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, resultsTxt.getText().toString() + "\n" +uri);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        cancelCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                constraintCaptured.setVisibility(View.GONE);
                neighborCons.setVisibility(View.GONE);
            }
        });

        cancelSearchCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultsCons.setVisibility(View.GONE);
                findmeContraint.setVisibility(View.VISIBLE);
            }
        });

        directionSearchCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(
                        Uri.parse("https://www.google.com/maps?saddr="+ myLat +"," + myLong + "&daddr=" +
                                (searchLat + "," + searchLong)));
                startActivity(intent);
            }
        });

        db.collection(COLLECTION_KEY).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Address miss = document.toObject(Address.class);
                        listAddress.add(miss);
                    }

                    addressAdapter = new AddressAdapter(AddressActivity.this, listAddress);
                    addressList.setAdapter(addressAdapter);
                } else {
                    Toast.makeText(AddressActivity.this, "Error fetching document", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {

        try{
            Intent mStartActivity = new Intent(context, HomeActivity.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
            System.exit(0);
        }catch (Exception e){
            e.printStackTrace();
        }

        super.onBackPressed();
    }


    private synchronized void setUpGClient() {
        if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    //.enableAutoManage(this, 0, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            getMyLocation();
            //googleApiClient.connect();
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
                int permissionLocation = ContextCompat.checkSelfPermission(AddressActivity.this,
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
                                            .checkSelfPermission(AddressActivity.this,
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
                                        status.startResolutionForResult(AddressActivity.this,
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
        int permissionLocation = ContextCompat.checkSelfPermission(AddressActivity.this,
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

                    if (locationAccepted && callAccepted){

                    }
                    //Toast.makeText(HomeActivity.this, "", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(HomeActivity.this, "Permission Denied, You cannot access location data and call.", Toast.LENGTH_SHORT).show();
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
        new AlertDialog.Builder(AddressActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void CheckGpsStatus() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onLocationChanged(final Location location) {
        mylocation = location;
        myLat= String.valueOf(location.getLatitude());
        myLong= String.valueOf(location.getLongitude());

        if (mylocation != null) {
            latitude = mylocation.getLatitude();
            coordinatestxt.setText(Double.toString(latitude));
            startDialog.dismiss();
        }
    }

    private void StartScan(){

        coordinatestxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //pDialog.setMessage("Capturing your location. Please keep the Phone still...");
                mCounter++;

                lisLat.add((mylocation.getLatitude() * -1));
                lisLon.add(mylocation.getLongitude());

                latitudeList.add(mylocation.getLatitude());
                longitudeList.add(mylocation.getLongitude());

                for (int a = 1; a < lisLat.size(); a++) {

                    lat1 = lisLat.get(a);
                    lon1 = lisLon.get(a);
                    lat2 = lisLat.get(a - 1);
                    lon2 = lisLon.get(a - 1);

                    Location loc1 = new Location("");
                    loc1.setLatitude(lat1);
                    loc1.setLongitude(lon1);

                    Location loc2 = new Location("");
                    loc2.setLatitude(lat2);
                    loc2.setLongitude(lon2);

                    float distanceInMeters = loc1.distanceTo(loc2);

                    listAllDistance.add(String.valueOf(distanceInMeters));

                    if (listAllDistance.size() > 22) {
                        listDistance = listAllDistance.subList(2, 20);

                        Set<String> set = new HashSet<>(listDistance);
                        listDistance.clear();
                        listDistance.addAll(set);
                        listDistance.remove("0.0");
                        Collections.sort(listDistance);

                        if (listDistance.size() > 1) {
                            diffMax = Double.valueOf(listDistance.get(listDistance.size() - 1));

                            //if (diffMax >= 0.7) {listAllDistance.clear();}

                            if(diffMax>50){
                                listAllDistance.clear();
                                pDialog.setMessage("Capturing your location. Please keep the Phone still... (10%)");
                            }else if(diffMax>25 && diffMax <51){
                                listAllDistance.clear();
                                pDialog.setMessage("Capturing your location. Please keep the Phone still... (30%)");
                            }else if(diffMax>5 && diffMax <26){
                                listAllDistance.clear();
                                pDialog.setMessage("Capturing your location. Please keep the Phone still... (50%)");
                            }else if(diffMax>1 && diffMax <6){
                                listAllDistance.clear();
                                pDialog.setMessage("Capturing your location. Please keep the Phone still... (70%)");
                                pDialog.setCancelable(true);
                            }
                            else if (diffMax < 1.1) {

                                googleApiClient.disconnect();
                                Latitude = String.valueOf(latitudeList.get(latitudeList.size() - 2));
                                Longitude = String.valueOf(longitudeList.get(longitudeList.size() - 2));
                                CalculateDomain();
                                pDialog.setMessage("Location sucessfully captured. Fetching addresses...");
                                listAllDistance.clear();
                                pDialog.setCancelable(true);

                            } else {

                            }
                        }
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void CalculateDomain(){
        absoluteLat= String.valueOf(Double.parseDouble(Latitude));

        curLat=Double.parseDouble(absoluteLat);
        curLat1=Double.parseDouble(Latitude);
        curLong=Double.parseDouble(Longitude);

        angleRadius= 0.002/ ( 111 * Math.cos(curLat1));

        minLat = curLat - angleRadius;
        maxLat = curLat + angleRadius;
        minLong = curLong - angleRadius;
        maxLong = curLong + angleRadius;

        new FindAddress().execute();

        //ScanAddress();
    }

    private void ScanAddress() {

        db.collection(COLLECTION_KEY)
                .whereGreaterThanOrEqualTo("longitude", String.valueOf(minLong)).whereLessThanOrEqualTo("longitude", String.valueOf(maxLong))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Address miss = document.toObject(Address.class);
                        longFilterList.add(miss);
                        if (longFilterList.size() > 0) {
                            pDialog.setMessage("Configuring addresses...");
                            for (int j = 0; j < longFilterList.size(); j++) {
                                if (Double.valueOf(longFilterList.get(j).getLatitude()) > minLat
                                        && Double.valueOf(longFilterList.get(j).getLatitude()) < maxLat) {

                                    commonList.add(longFilterList.get(j));

                                    int cc = 0;
                                    while (cc < longFilterList.size() - 1) {
                                        cc++;
                                        if (cc == longFilterList.size() - 1) {
                                            FilterList();
                                        }
                                    }
                                }
                            }
                            FilterList();
                        } else {
                            failureTxt.setVisibility(View.VISIBLE);
                            failureTxt.setText("Address not found. Try again and standing still on the outside of the gate.");
                            pDialog.dismiss();
                        }

                    }

                } else {
                    Toast.makeText(AddressActivity.this, "Error fetching document", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void FilterList(){

        if(commonList.size()==1) {
            resultsTxt.setText(commonList.get(0).getFulladdress());
            reLatitude=commonList.get(0).getLatitude();
            reLongitude=commonList.get(0).getLongitude();
            constraintCaptured.setVisibility(View.VISIBLE);
            neighborCons.setVisibility(View.GONE);
            pDialog.dismiss();

        }else if(commonList.size()>1){

            for (int a = 0; a < commonList.size(); a++) {

                reLat1 = Double.valueOf(commonList.get(a).getLatitude())-100;
                reLong1 = Double.valueOf(commonList.get(a).getLongitude());

                Location loc1 = new Location("");
                loc1.setLatitude(curLat1);
                loc1.setLongitude(curLong);

                Location loc2 = new Location("");
                loc2.setLatitude(reLat1);
                loc2.setLongitude(reLong1);

                float distanceInMeters = loc1.distanceTo(loc2);
                listReDistance.add(String.valueOf(distanceInMeters));

                HashMap<String, String> map = new HashMap<>();
                map.put("fulladdress", commonList.get(a).getFulladdress());
                map.put("latitude", String.valueOf(Double.valueOf(commonList.get(a).getLatitude())));
                map.put("longitude", String.valueOf(Double.valueOf(commonList.get(a).getLongitude())));
                map.put("distance", String.valueOf(listReDistance.get(a)));
                distanceListRe.add(map);
                SortList();

            }

        }else{
            failureTxt.setVisibility(View.VISIBLE);
            failureTxt.setText("Address not found. Try again and standing still on the outside of the gate.");
            pDialog.dismiss();

        }
    }

    public void SortList(){

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
        Collections.sort(distanceListRe, new MapComparator("distance"));


        String reFulladdress = distanceListRe.get(0).get("fulladdress");
        reLatitude = distanceListRe.get(0).get("latitude");
        reLongitude = distanceListRe.get(0).get("longitude");



        HashSet hs = new HashSet();
        hs.addAll(commonList); // demoArrayList= name of arrayList from which u want to remove duplicates
        commonList.clear();
        commonList.addAll(hs);

        AddressAdapter addressListAdapter = new AddressAdapter(AddressActivity.this, commonList);
        //neighborList.setAdapter(addressListAdapter);

        neighborList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String latMap=((TextView) view.findViewById(R.id.txt_latitude_item)).getText().toString();
                final String longMap=((TextView) view.findViewById(R.id.txt_longitude_item)).getText().toString();
                final String addressNumber=((TextView) view.findViewById(R.id.txt_address_number)).getText().toString();

                resultsTxt.setText(commonList.get(i).getFulladdress());
                reLatitude=commonList.get(i).getLatitude();
                reLongitude=commonList.get(i).getLongitude();

            }
        });
        resultsTxt.setText(reFulladdress);
        constraintCaptured.setVisibility(View.VISIBLE);
        neighborCons.setVisibility(View.VISIBLE);
        pDialog.dismiss();

    }

    private class FindAddress extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();

            httpParams.put(KEY_MIN_LATITUDE, String.valueOf(minLat));
            httpParams.put(KEY_MIN_LONGITUDE, String.valueOf(minLong));
            httpParams.put(KEY_MAX_LATITUDE, String.valueOf(maxLat));
            httpParams.put(KEY_MAX_LONGITUDE, String.valueOf(maxLong));

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "search_address.php", "GET", httpParams);

            try {
                success = jsonObject.getInt(KEY_SUCCESS);
                JSONObject businesses;
                if (success == 1) {
                    //Parse the JSON response
                    businesses = jsonObject.getJSONObject(KEY_DATA);

                    myAddressName = businesses.getString(KEY_ADDRESS_NAME);
                    myAddress = businesses.getString(KEY_ADDRESS);
                    myLatitude = businesses.getString(KEY_LATITUDE);
                    myLongitude=businesses.getString(KEY_LONGITUDE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    if (success == 1) {
                        resultsTxt.setText(myAddress);

                        constraintCaptured.setVisibility(View.VISIBLE);
                        neighborCons.setVisibility(View.VISIBLE);
                        pDialog.dismiss();
                    } else {

                        failureTxt.setVisibility(View.VISIBLE);
                        failureTxt.setText("Address not found. Try again and standing still on the outside of the gate.");
                        pDialog.dismiss();

                    }
                }
            });
        }
    }

}
