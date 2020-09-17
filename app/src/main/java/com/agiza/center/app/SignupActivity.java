package com.agiza.center.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agiza.center.app.Helper.CheckNetWorkStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    Button backLoginBtn, signupBtnS;
    EditText firstNameTxtS, lastNameTxtS, phoneEdtS, emailEdtS, passwordEdtS, confirmPasswordEdt;
    TextView termsTxt;
    CheckBox termsCheck;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    String Phone, Email, Password, passwordSignUp, repeatPassword, firstName, lastName, emailString, passwordString;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private ProgressDialog pDialog;
    String myid, msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        firstNameTxtS = (EditText) findViewById(R.id.edt_first_name_signup);
        lastNameTxtS = (EditText) findViewById(R.id.edt_last_name_signup);
        phoneEdtS = (EditText) findViewById(R.id.edt_phone_signup);
        emailEdtS = (EditText) findViewById(R.id.edt_email_signup);
        passwordEdtS = (EditText) findViewById(R.id.edt_password_signup);
        confirmPasswordEdt = (EditText) findViewById(R.id.edt_confirm_password_signup);

        signupBtnS = (Button) findViewById(R.id.btn_signup_signup);
        termsCheck = (CheckBox) findViewById(R.id.check_terms_signup);
        termsTxt = (TextView) findViewById(R.id.txt_terms_signup);

        backLoginBtn = (Button) findViewById(R.id.btn_back_login);

        backLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        termsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, TermsActivity.class));
            }
        });
        signupBtnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetWorkStatus.isNetworkAvailable(SignupActivity.this)) {

                    if (passwordEdtS.getText().toString().contentEquals(confirmPasswordEdt.getText().toString())) {

                        if (termsCheck.isChecked()) {
                            pDialog = new ProgressDialog(SignupActivity.this);
                            pDialog.setMessage("Registering. Please wait...");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(false);
                            pDialog.show();

                            mAuth.createUserWithEmailAndPassword(emailEdtS.getText().toString(), passwordEdtS.getText().toString())
                                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {

                                                Date currentDate = new Date();
                                                Map<String, Object> emergency = new HashMap<>();
                                                emergency.put("firstname", firstNameTxtS.getText().toString());
                                                emergency.put("lastname", lastNameTxtS.getText().toString());
                                                emergency.put("phone", phoneEdtS.getText().toString());
                                                emergency.put("email", emailEdtS.getText().toString());
                                                emergency.put("date", currentDate);
                                                emergency.put("myid", "");


                                                db.collection("users")
                                                        .add(emergency)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                myid = documentReference.getId();
                                                                UpdateId();
                                                                emailString = emailEdtS.getText().toString();
                                                                passwordString = passwordEdtS.getText().toString();

                                                                Toast.makeText(SignupActivity.this, "Registration Successful.",
                                                                        Toast.LENGTH_SHORT).show();

                                                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));

                                                                pDialog.dismiss();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(SignupActivity.this, "Registration was not Successful.",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                //Log.w(TAG, msg);
                                                Toast.makeText(SignupActivity.this, "Registration was not Successful.",
                                                        Toast.LENGTH_SHORT).show();
                                                // UpdateID(null);
                                                pDialog.dismiss();
                                            }

                                            // ...
                                        }
                                    });


                        } else {
                            Toast.makeText(SignupActivity.this, "Agree to terms and conditions.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        passwordEdtS.setError("The two passwords must match!");
                        confirmPasswordEdt.setText("");
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "Unable to connect to internet", Toast.LENGTH_LONG).show();
                }
            }

        });

    }

    private void UpdateId(){
        Map<String, Object> emergency2 = new HashMap<>();
        emergency2.put("myid", myid);


        db.collection("users").document(myid)
                .set(emergency2, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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
