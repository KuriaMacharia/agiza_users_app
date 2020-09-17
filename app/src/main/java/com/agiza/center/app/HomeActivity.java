package com.agiza.center.app;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.center.agiza.MyView;
import com.center.emergency.Emergency;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.agiza.center.app.Helper.CheckNetWorkStatus;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        LocationListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    String languages []={"English", "Kiswahili"};

    public static final String themename1 = "themenameKey";

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String firstName1 = "firstNameKey";
    public static final String lastName1= "lastNameKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String Password1 = "passwordKey";
    public static final String myid1 = "myidKey";
    public static final String logintype1 = "logintypeKey";

    String firstName, lastName, Phone, Email, Password, myid, loginType;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    ImageView logoOneImg, logoTwoImg;

    private static final String TAG = "HomeActivity";

    Spinner languageSpin;
    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private int CALL_CODE = 3;
    private int LOCATION_CODE = 4;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private View view;
    Context context;
    LocationManager locationManager;
    boolean GpsStatus = false;
    LocationRequest locationRequest;
    Button ambulanceBtn, fireBtn, policeBtn;
    ConstraintLayout findmeConstraint;
    TextView accountTxt, partnersTxt, settingsTxt, aboutTxt, contactTxt, questionsTxt, logoutTxt, usernameTxt, emailTxt, helpTxt,
                fblogoutTxt, googlelogoutTxt, kovidTxt, animTxt, locationsTxt;

    boolean isForceUpdate = false;
    private GoogleSignInClient googleSignInClient;
    GridView homeGrid;
    ArrayList gridList;
    int myAnim;
    Emergency eView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_home);

        AppUpdateChecker appUpdateChecker=new AppUpdateChecker(this);  //pass the activity in constructure
        appUpdateChecker.checkForUpdate(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("500658507200-4jpsnvarudd27a52eirhdlp5lcl7ks0u.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView titleTxt =(TextView)toolbar.findViewById(R.id.txt_toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        //getSupportActionBar().setTitle("HOME");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_white_24dp));

        gridList= new ArrayList<>();
        homeGrid=(GridView) findViewById(R.id.grid_home);
        gridList.add(new Item("Contacts",R.drawable.contacts));
        gridList.add(new Item("Shopping",R.drawable.shop));
        gridList.add(new Item("Address",R.drawable.address_finder));
        gridList.add(new Item("Emergency",R.drawable.siren));
        gridList.add(new Item("Explore",R.drawable.explore_2));
        gridList.add(new Item("Favorites",R.drawable.places));

        GridAdapter myAdapter=new GridAdapter(this,R.layout.grid_content,gridList);
        homeGrid.setAdapter(myAdapter);

        ambulanceBtn=(Button) findViewById(R.id.btn_ambulance_main);
        policeBtn=(Button) findViewById(R.id.btn_police_main);
        fireBtn=(Button) findViewById(R.id.btn_fire_main);
        findmeConstraint=(ConstraintLayout) findViewById(R.id.constraint_check_address);
        logoOneImg=(ImageView) findViewById(R.id.img_logo_a);
        logoTwoImg=(ImageView) findViewById(R.id.img_logo_b);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        context = getApplicationContext();
        myAnim=0;

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Password = sharedpreferences.getString(Password1,"");
        Email = sharedpreferences.getString(Email1,"");
        Phone = sharedpreferences.getString(phoneNumber1,"");
        firstName = sharedpreferences.getString(firstName1,"");
        lastName = sharedpreferences.getString(lastName1,"");
        myid = sharedpreferences.getString(myid1,"");
        loginType = sharedpreferences.getString(logintype1,"");

        setUpGClient();
        CheckGpsStatus();

        if (permissionAlreadyGranted()) {

        }else{
            requestPermission();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView=navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        usernameTxt=(TextView) headerView.findViewById(R.id.txt_username_main);
        emailTxt=(TextView) headerView.findViewById(R.id.txt_email_main);
        accountTxt=(TextView) headerView.findViewById(R.id.txt_account_nav);
        partnersTxt=(TextView) headerView.findViewById(R.id.txt_partners_nav);
        kovidTxt=(TextView) headerView.findViewById(R.id.txt_kovid_nav);

        settingsTxt=(TextView) headerView.findViewById(R.id.txt_settings_nav);
        aboutTxt=(TextView) headerView.findViewById(R.id.txt_about_us_nav);
        contactTxt=(TextView) headerView.findViewById(R.id.txt_contact_us_nav);
        questionsTxt=(TextView) headerView.findViewById(R.id.txt_questions_nav);
        helpTxt=(TextView) headerView.findViewById(R.id.txt_help_nav);
        logoutTxt=(TextView) headerView.findViewById(R.id.txt_logout_nav);
        googlelogoutTxt=(TextView) headerView.findViewById(R.id.txt_google_logout);
        fblogoutTxt=(TextView) headerView.findViewById(R.id.txt_facebook_logout);
        animTxt=(TextView) findViewById(R.id.txt_anim_count);
        locationsTxt=(TextView) findViewById(R.id.txt_locations_home);
        usernameTxt.setText(" "+ firstName + " " + lastName + " ");
        emailTxt.setText(" "+ Email + " ");
        locationsTxt.setSelected(true);

        if(loginType.contentEquals("Google Account")){
            googlelogoutTxt.setVisibility(View.VISIBLE);
            logoutTxt.setVisibility(View.GONE);
            fblogoutTxt.setVisibility(View.GONE);
        }
        if(loginType.contentEquals("Facebook Account")){
            googlelogoutTxt.setVisibility(View.GONE);
            logoutTxt.setVisibility(View.GONE);
            fblogoutTxt.setVisibility(View.VISIBLE);
        }
        if(loginType.contentEquals("Agiza Account")){
            googlelogoutTxt.setVisibility(View.GONE);
            logoutTxt.setVisibility(View.VISIBLE);
            fblogoutTxt.setVisibility(View.GONE);
        }

        ObjectAnimator animation = ObjectAnimator.ofFloat(logoOneImg, "rotationY", 270f, 90f);
        animation.setDuration(2600);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();

        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                    myAnim++;
                    animTxt.setText(String.valueOf(myAnim));
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

            }
        });

        animTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(Integer.valueOf(animTxt.getText().toString())% 2 == 0){
                    logoOneImg.setImageResource(R.drawable.polygon_logo3);
                }else{
                    logoOneImg.setImageResource(R.drawable.kenya_flag_2);
                }
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult().getToken();
                        String msg = getString(R.string.fcm_token, token);
                        Log.d(TAG, msg);

                    }
                });

        homeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        startActivity(new Intent(HomeActivity.this, BookActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(HomeActivity.this, ShoppingActivity.class));
                        break;
                    case 2:
                        View v = new MyView(HomeActivity.this);
                        setContentView(v);
                        break;
                    case 3:
                        //startActivity(new Intent(HomeActivity.this, EmergencyActivity.class));

                        eView=new Emergency(HomeActivity.this);
                        eView.show();
                        break;
                    case 4:
                        startActivity(new Intent(HomeActivity.this, ExploreActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(HomeActivity.this, PlacesActivity.class));
                        break;
                }
            }
        });

        logoutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });

        googlelogoutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                        editor = sharedpreferences.edit();
                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        }
                    });
            }
        });

        fblogoutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();

                sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });

        accountTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                    if (loginType.contentEquals("Agiza Account")) {
                        startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                        }else{
                        Toast.makeText(HomeActivity.this,
                                "Unavailable! Only available when you login with your Agiza Account.",
                                Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(HomeActivity.this,
                                "Unable to connect to internet",
                                Toast.LENGTH_LONG).show();
                    }
                }
        });

        contactTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                    startActivity(new Intent(HomeActivity.this, ContactActivity.class));
                }else {
                    Toast.makeText(HomeActivity.this,
                            "Unable to connect to internet",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        kovidTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                    startActivity(new Intent(HomeActivity.this, KovidActivity.class));
                }else {
                    Toast.makeText(HomeActivity.this,
                            "Unable to connect to internet",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        settingsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });

        helpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(HomeActivity.this, HelpActivity.class));
            }
        });

        questionsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, QuestionsActivity.class));
            }
        });

        aboutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                    startActivity(new Intent(HomeActivity.this, AboutActivity.class));
                }else {
                    Toast.makeText(HomeActivity.this,
                            "Unable to connect to internet",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        partnersTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                    startActivity(new Intent(HomeActivity.this, PartnersActivity.class));
                }else {
                    Toast.makeText(HomeActivity.this,
                            "Unable to connect to internet",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        ambulanceBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AnimatorSet reducer = (AnimatorSet) AnimatorInflater.loadAnimator(HomeActivity.this,R.animator.reduce_size);
                        reducer.setTarget(view);
                        reducer.start();
                        break;

                    case MotionEvent.ACTION_UP:
                        AnimatorSet regainer = (AnimatorSet) AnimatorInflater.loadAnimator(HomeActivity.this,R.animator.regain_size);
                        regainer.setTarget(view);
                        regainer.start();
                        startActivity(new Intent(HomeActivity.this, MedicalActivity.class));
                        break;
                }
                return true;
            }
        });

        policeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AnimatorSet reducer = (AnimatorSet) AnimatorInflater.loadAnimator(HomeActivity.this, R.animator.reduce_size);
                        reducer.setTarget(view);
                        reducer.start();
                        break;

                    case MotionEvent.ACTION_UP:
                        AnimatorSet regainer = (AnimatorSet) AnimatorInflater.loadAnimator(HomeActivity.this, R.animator.regain_size);
                        regainer.setTarget(view);
                        regainer.start();
                        startActivity(new Intent(HomeActivity.this, EstateActivity.class));
                        break;
                }
                return true;
            }

        });

        fireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionAlreadyGranted()) {
                    CheckGpsStatus();
                    if (GpsStatus) {
                        if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                            startActivity(new Intent(HomeActivity.this, FireActivity.class));
                        } else {
                            Toast.makeText(HomeActivity.this,
                                    "Unable to connect to internet",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                        Toast.makeText(HomeActivity.this, "Please Enable GPS First", Toast.LENGTH_LONG).show();
                    }
                }else{
                    requestPermission();
                }
            }
        });

        findmeConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionAlreadyGranted()) {
                    CheckGpsStatus();
                    if (GpsStatus) {
                        if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                            startActivity(new Intent(HomeActivity.this, AddressActivity.class));

                        } else {
                            Toast.makeText(HomeActivity.this,
                                    "Unable to connect to internet",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Please Enable GPS First", Toast.LENGTH_LONG).show();
                    }
                }else{
                    requestPermission();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            if (loginType.contentEquals("Agiza Account")) {
                editor = sharedpreferences.edit();
                editor.putString(firstName1, firstName);
                editor.putString(lastName1, lastName);
                editor.putString(phoneNumber1, Phone);
                editor.putString(Email1, Email);
                editor.putString(Password1, Password);
                editor.putString(myid1, myid);
                editor.commit();

                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }else{
                Toast.makeText(HomeActivity.this,
                        "Unavailable! Only available when you login with your Agiza Account.",
                        Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                //.enableAutoManage(HomeActivity.this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
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
                        //finish();

                        break;
                }
                break;
        }
    }

    private void CheckGpsStatus() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(HomeActivity.this,
                        ACCESS_FINE_LOCATION);
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
                                            .checkSelfPermission(HomeActivity.this,
                                                    ACCESS_FINE_LOCATION);
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
                                        status.startResolutionForResult(HomeActivity.this,
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
    public void onLocationChanged(Location location) {

    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(HomeActivity.this,
                ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(ACCESS_FINE_LOCATION);
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
        new AlertDialog.Builder(HomeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}

