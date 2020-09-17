package com.agiza.center.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.agiza.center.app.Helper.HttpJsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RequestsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String myid1 = "myidKey";

    String Phone, Email, myid;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    private static final String KEY_SUCCESS = "success";

    private static final String KEY_ACCOUNT_USED = "account_phone";
    private static final String KEY_ACCOUNT_EMAIL = "account_email";
    private static final String KEY_ACCOUNT_ID = "account_id";

    private static final String KEY_REQUEST_TYPE = "request_type";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PlOT = "plot";
    private static final String KEY_ADDRESS_TYPE = "address_type";
    private static final String KEY_ADDRESS_CATEGORY = "address_category";

    private static final String KEY_BUILDING_NAME = "building_name";
    private static final String KEY_FLOOR = "floor";
    private static final String KEY_HOUSE_NUMBER = "house_number";
    private static final String KEY_REMOVE_REASON = "reason";

    private static final String BASE_URL = "http://www.anwani.net/seya/";

    String[] category = {"---Select---", "Residence", "Finance","Government Office", "Education", "Hospitals", "Religion","Transport","Other"};

    String[] government ={"---Select---","Police","Huduma Center", "Administration Office","Court","Prison","Government Institution", "Other Address Type"};
    String[] finance ={"---Select---","Business Premises", "Mixed Use", "Offices","Hotel","Lodging","Mall","Petrol Station", "Other Address Type"};
    String[] residence = {"---Select---","Single Residence","Apartment Building","Informal Settlement","Community Setup","Farm","Other Address Type"};
    String[] education = {"---Select---","Pre-school","Nursery School","Primary School","Secondary School","Technical School","College","University","Other Address Type"};
    String[] hospitals ={"---Select---","Public Hospitals", "Private Hospitals","Public Clinic","Private Clinic","Laboratory","Other Address Type"};
    String[] religion = {"---Select---","Church","Mosque","Temple","Other Address Type"};
    String[] transport ={"---Select---","Airport","Train Station","Bus Station","Port","Other Address Type"};
    String[] other = {"---Select---","Other Address Type"};
    String[] selCat= { };

    ConstraintLayout nameCons, contactCons, plotCons, categoryCons, typeCons, removeCons, buildingCons, floorCons, houseCons;
    TextView addressTxt, requestTypeTxt;
    EditText nameEdt, contactEdt, plotEdt, reasonEdt, buildingNameEdt, floorEdt, houseNumberEdt;
    Spinner categorySpin, typeSpin;
    ImageView cancelImg, homeImg;
    CheckBox removeBox;
    Button sendBtn;
    String propertyCategory, propertyType;
    ProgressDialog pDialog;
    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Email = sharedpreferences.getString(Email1,"");
        Phone = sharedpreferences.getString(phoneNumber1,"");
        myid = sharedpreferences.getString(myid1,"");

        addressTxt=(TextView) findViewById(R.id.txt_address_request);
        requestTypeTxt=(TextView) findViewById(R.id.txt_request_type_request);
        cancelImg=(ImageView) findViewById(R.id.img_cancel_request);
        homeImg=(ImageView) findViewById(R.id.img_home);

        removeBox=(CheckBox) findViewById(R.id.check_remove_request);
        sendBtn=(Button) findViewById(R.id.btn_send_request_request);

        nameEdt=(EditText) findViewById(R.id.edt_name_request);
        contactEdt=(EditText) findViewById(R.id.edt_phone_request);
        plotEdt=(EditText) findViewById(R.id.edt_plot_number_request);
        reasonEdt=(EditText) findViewById(R.id.edt_reason_request);
        buildingNameEdt=(EditText) findViewById(R.id.edt_building_name_request);
        floorEdt=(EditText) findViewById(R.id.edt_floor_request);
        houseNumberEdt=(EditText) findViewById(R.id.edt_house_number_request);

        categorySpin=(Spinner) findViewById(R.id.spin_category_request);
        typeSpin=(Spinner) findViewById(R.id.spin_type_request);

        typeCons=(ConstraintLayout) findViewById(R.id.cons_property_type_request);
        nameCons=(ConstraintLayout) findViewById(R.id.cons_name_request);
        contactCons=(ConstraintLayout) findViewById(R.id.cons_phone_request);
        plotCons=(ConstraintLayout) findViewById(R.id.cons_plot_request);
        categoryCons=(ConstraintLayout) findViewById(R.id.cons_category_request);
        removeCons=(ConstraintLayout) findViewById(R.id.cons_remove_request);
        buildingCons=(ConstraintLayout) findViewById(R.id.cons_building_request);
        floorCons=(ConstraintLayout) findViewById(R.id.cons_floor_request);
        houseCons=(ConstraintLayout) findViewById(R.id.cons_house_request);

        categorySpin.setOnItemSelectedListener(this);
        ArrayAdapter cty = new ArrayAdapter(RequestsActivity.this, android.R.layout.simple_spinner_item, category);
        cty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpin.setAdapter(cty);

        Bundle loca=getIntent().getExtras();
        if (loca != null) {
            requestTypeTxt.setText(loca.getCharSequence("type"));
            addressTxt.setText (loca.getCharSequence("address"));
        }

        if(requestTypeTxt.getText().toString().contentEquals("Own Address Request")){
            nameCons.setVisibility(View.VISIBLE);
            contactCons.setVisibility(View.VISIBLE);
            plotCons.setVisibility(View.VISIBLE);
            sendBtn.setVisibility(View.VISIBLE);

        }else if(requestTypeTxt.getText().toString().contentEquals("Sub-Address Request")){
            nameCons.setVisibility(View.VISIBLE);
            contactCons.setVisibility(View.VISIBLE);
            buildingCons.setVisibility(View.VISIBLE);
            floorCons.setVisibility(View.VISIBLE);
            houseCons.setVisibility(View.VISIBLE);
            sendBtn.setVisibility(View.VISIBLE);

        }else if(requestTypeTxt.getText().toString().contentEquals("Verification Request")){
            nameCons.setVisibility(View.VISIBLE);
            contactCons.setVisibility(View.VISIBLE);
            plotCons.setVisibility(View.VISIBLE);
            categoryCons.setVisibility(View.VISIBLE);
            sendBtn.setVisibility(View.VISIBLE);


        }else if(requestTypeTxt.getText().toString().contentEquals("Transfer Request")){
            nameCons.setVisibility(View.VISIBLE);
            contactCons.setVisibility(View.VISIBLE);
            sendBtn.setVisibility(View.VISIBLE);


        }else if(requestTypeTxt.getText().toString().contentEquals("Update Request")){
            nameCons.setVisibility(View.VISIBLE);
            contactCons.setVisibility(View.VISIBLE);
            plotCons.setVisibility(View.VISIBLE);
            categoryCons.setVisibility(View.VISIBLE);
            sendBtn.setVisibility(View.VISIBLE);

        }else if(requestTypeTxt.getText().toString().contentEquals("Remove Request")){
            nameCons.setVisibility(View.VISIBLE);
            contactCons.setVisibility(View.VISIBLE);
            removeCons.setVisibility(View.VISIBLE);
            sendBtn.setVisibility(View.VISIBLE);

        }else{

        }


        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RequestsActivity.this, PlacesActivity.class));
            }
        });

        homeImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(RequestsActivity.this, HomeActivity.class));
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestTypeTxt.getText().toString().contentEquals("Remove Request")){
                    if(removeBox.isChecked()){
                        new AddRequest().execute();
                    }else{
                        Toast.makeText(RequestsActivity.this, "Error! Not checked the Remove Box.", Toast.LENGTH_LONG).show();
                    }
                }else{
                    new AddRequest().execute();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        propertyCategory = String.valueOf(categorySpin.getSelectedItem());

        if(!propertyCategory.isEmpty()){

            if(propertyCategory.contentEquals("Residence")) {
                selCat=residence;
                typeCons.setVisibility(View.VISIBLE);
            }else if(propertyCategory.contentEquals("Finance")){
                selCat=finance;
                typeCons.setVisibility(View.VISIBLE);
            }else if(propertyCategory.contentEquals("Government Office")){
                selCat=government;
                typeCons.setVisibility(View.VISIBLE);
            }else if(propertyCategory.contentEquals("Education")){
                selCat=education;
                typeCons.setVisibility(View.VISIBLE);
            }else if(propertyCategory.contentEquals("Hospitals")){
                selCat=hospitals;
                typeCons.setVisibility(View.VISIBLE);
            }else if(propertyCategory.contentEquals("Religion")){
                selCat=religion;
                typeCons.setVisibility(View.VISIBLE);
            }else if(propertyCategory.contentEquals("Transport")){
                selCat=transport;
                typeCons.setVisibility(View.VISIBLE);
            }else if(propertyCategory.contentEquals("Other")){
                selCat=other;
                typeCons.setVisibility(View.VISIBLE);
            }else{
                typeCons.setVisibility(View.GONE);
            }

            ArrayAdapter scsc = new ArrayAdapter(RequestsActivity.this, android.R.layout.simple_spinner_item, selCat);
            scsc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            typeSpin.setAdapter(scsc);

            typeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    propertyType = String.valueOf(typeSpin.getSelectedItem());
                    if(!propertyType.contentEquals("---Select---")) {
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

    private class AddRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(RequestsActivity.this, R.style.mydialog);
            pDialog.setMessage("Sending. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();

            httpParams.put(KEY_ACCOUNT_USED, Phone);
            httpParams.put(KEY_ACCOUNT_EMAIL, Email);
            httpParams.put(KEY_ACCOUNT_ID, myid);

            httpParams.put(KEY_ADDRESS, addressTxt.getText().toString());
            httpParams.put(KEY_REQUEST_TYPE, requestTypeTxt.getText().toString());
            httpParams.put(KEY_NAME, nameEdt.getText().toString());
            httpParams.put(KEY_PHONE, contactEdt.getText().toString());
            httpParams.put(KEY_PlOT, plotEdt.getText().toString());
            httpParams.put(KEY_ADDRESS_TYPE, propertyType);
            httpParams.put(KEY_ADDRESS_CATEGORY, propertyCategory);

            httpParams.put(KEY_BUILDING_NAME, buildingNameEdt.getText().toString());
            httpParams.put(KEY_FLOOR, floorEdt.getText().toString());
            httpParams.put(KEY_HOUSE_NUMBER, houseNumberEdt.getText().toString());

            httpParams.put(KEY_REMOVE_REASON, reasonEdt.getText().toString());

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "address_request.php", "POST", httpParams);
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
                        Toast.makeText(RequestsActivity.this,"Request Sent",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RequestsActivity.this, PlacesActivity.class));
                    } else {
                        Toast.makeText(RequestsActivity.this,"Request Sending Failed",Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                    }
                }
            });
        }
    }
}
