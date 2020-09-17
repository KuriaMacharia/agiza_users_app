package com.agiza.center.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    public static final String firstName1 = "firstNameKey";
    public static final String lastName1= "lastNameKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String Password1 = "passwordKey";
    public static final String myid1 = "myidKey";

    TextView nameTxt, emailTxt, phoneTxt, homeTxt;
    EditText emailEdt, phoneEdt;
    ConstraintLayout contactCons, editCons, editDetailsCons;
    Button saveBtn, cancelBtn;
    String Email, Phone, firstName, lastName, Password, myid;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private ProgressDialog pDialog;
    ImageView homeImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        themeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        homeImg=(ImageView) findViewById(R.id.img_home);
        nameTxt=(TextView) findViewById(R.id.txt_name_profile);
        emailTxt=(TextView) findViewById(R.id.txt_email_profile);
        phoneTxt=(TextView) findViewById(R.id.txt_phone_profile);
        emailEdt=(EditText) findViewById(R.id.edt_email_profile);
        phoneEdt=(EditText) findViewById(R.id.edt_phone_profile);
        saveBtn=(Button) findViewById(R.id.btn_save_profile);
        cancelBtn= (Button) findViewById(R.id.btn_cancel_edt_profile);
        contactCons=(ConstraintLayout) findViewById(R.id.constraint_contacts_profile);
        editCons=(ConstraintLayout) findViewById(R.id.constraint_edit_prfile);
        editDetailsCons=(ConstraintLayout) findViewById(R.id.constraint_edit_details_profile);

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Email = sharedpreferences.getString(Email1,"");
        Phone = sharedpreferences.getString(phoneNumber1,"");
        firstName = sharedpreferences.getString(firstName1,"");
        lastName = sharedpreferences.getString(lastName1,"");
        Password = sharedpreferences.getString(Password1,"");
        myid = sharedpreferences.getString(myid1,"");

        nameTxt.setText(firstName + " " + lastName);
        emailTxt.setText(Email);
        phoneTxt.setText(Phone);

        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = sharedpreferences.edit();
                editor.putString(firstName1, firstName);
                editor.putString(lastName1, lastName);
                editor.putString(phoneNumber1, Phone);
                editor.putString(Email1, Email);
                editor.putString(Password1, Password);
                editor.putString(myid1, myid);
                editor.commit();
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
        });

        editCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contactCons.getVisibility()==View.GONE) {
                    contactCons.setVisibility(View.VISIBLE);
                    editDetailsCons.setVisibility(View.GONE);
                }else{
                    contactCons.setVisibility(View.GONE);
                    editDetailsCons.setVisibility(View.VISIBLE);
                    emailEdt.setText(Email);
                    phoneEdt.setText(Phone);
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editDetailsCons.getVisibility()==View.GONE) {
                    contactCons.setVisibility(View.GONE);
                    editDetailsCons.setVisibility(View.VISIBLE);
                }else{
                    contactCons.setVisibility(View.VISIBLE);
                    editDetailsCons.setVisibility(View.GONE);
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pDialog = new ProgressDialog(ProfileActivity.this);
                pDialog.setMessage("Updating Details. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(Email, Password);
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.updateEmail(emailEdt.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    UpdateDetails();
                                                }
                                            }
                                        });
                            }
                        });
            }
        });
    }

    public void UpdateDetails(){
        Map<String, Object> emergency2 = new HashMap<>();
        emergency2.put("email", emailEdt.getText().toString());
        emergency2.put("phone", phoneEdt.getText().toString());


        db.collection("users").document(myid)
                .set(emergency2, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        emailTxt.setText(emailEdt.getText().toString());
                        phoneTxt.setText(phoneEdt.getText().toString());

                        editor = sharedpreferences.edit();
                        editor.putString(phoneNumber1, phoneEdt.getText().toString());
                        editor.putString(Email1, emailEdt.getText().toString());
                        editor.putString(Password1, Password);
                        editor.putString(myid1, myid);
                        editor.commit();

                        contactCons.setVisibility(View.VISIBLE);
                        editDetailsCons.setVisibility(View.GONE);
                        pDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }
}
