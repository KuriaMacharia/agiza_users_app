package com.agiza.center.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.agiza.center.app.Helper.CheckNetWorkStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String themename1 = "themenameKey";
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String firstName1 = "firstNameKey";
    public static final String lastName1= "lastNameKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String Password1 = "passwordKey";
    public static final String myid1 = "myidKey";
    public static final String logintype1 = "logintypeKey";

    private static final String TAG = "LoginActivity";
    EditText phoneEdt, passwordEdt;
    CheckBox termsCheck;
    TextView forgotTxt, signUpTxt, animTxt;
    Button signupBtn, loginBtn;
    private FirebaseAuth mAuth;
    String Phone, Email, Password, passwordSignUp, repeatPassword, firstName, lastName, emailString, passwordString;
    FirebaseFirestore db;
    String myid, msg;
    ArrayList<Users> listUser;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private ProgressDialog pDialog;
    private SignInButton googleSignInButton;
    private static final String TAG1 = "AndroidClarified";
    private GoogleSignInClient googleSignInClient;
    ConstraintLayout facebookCons;
    CallbackManager callbackManager;
    LoginButton loginButton;
    private static final String EMAIL = "email";
    String loginType;
    ImageView logoOneImg, logoTwoImg;
    int myAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeUtils.onActivityCreateSetTheme(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        listUser = new ArrayList<Users>();

        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        logoOneImg=(ImageView) findViewById(R.id.img_logo_a);
        logoTwoImg=(ImageView) findViewById(R.id.img_logo_b);
        animTxt=(TextView) findViewById(R.id.txt_anim_count);
        myAnim=0;

        facebookCons=(ConstraintLayout) findViewById(R.id.constraint_facebook_login);
        phoneEdt = (EditText) findViewById(R.id.edt_phone_login);
        passwordEdt = (EditText) findViewById(R.id.edt_password_login);
        forgotTxt = (TextView) findViewById(R.id.txt_forgot_password);
        loginBtn = (Button) findViewById(R.id.btn_login);
        signUpTxt= (TextView) findViewById(R.id.txt_sign_up);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (sharedpreferences.getBoolean("logged", false)) {
            GoToMainActivityAuto();
        }

        ObjectAnimator animation = ObjectAnimator.ofFloat(logoOneImg, "rotationY", 90f, 270f);
        animation.setDuration(1600);
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

        forgotTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetWorkStatus.isNetworkAvailable(LoginActivity.this)) {
                    startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Unable to connect to internet", Toast.LENGTH_LONG).show();
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetWorkStatus.isNetworkAvailable(LoginActivity.this)) {
                    loginType = "Agiza Account";
                    if (phoneEdt.getText().toString().isEmpty() ||
                            passwordEdt.getText().toString().isEmpty()) {
                        passwordEdt.setError("No details entered!");
                        Toast.makeText(LoginActivity.this, "Please enter a valid email and password!", Toast.LENGTH_LONG).show();

                    } else {
                        pDialog = new ProgressDialog(LoginActivity.this);
                        pDialog.setMessage("Logging in. Please wait...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(false);
                        pDialog.show();

                        mAuth.signInWithEmailAndPassword(phoneEdt.getText().toString(), passwordEdt.getText().toString())
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            //updateUI(user);
                                            Email = user.getEmail();
                                            GoToMainActivity();
                                        } else {
                                            pDialog.dismiss();
                                            passwordEdt.setError("Incorrect Email or Password!");
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                            //Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                            updateUI(null);
                                        }

                                    }
                                });
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Unable to connect to internet", Toast.LENGTH_LONG).show();
                }

            }
        });

        googleSignInButton = findViewById(R.id.sign_in_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("500658507200-4jpsnvarudd27a52eirhdlp5lcl7ks0u.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginType = "Google Account";
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginType = "Facebook Account";
                AccessToken accessToken = loginResult.getAccessToken();
                useLoginInformation(accessToken);
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        onLoggedIn(account);
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
        firstName = googleSignInAccount.getDisplayName();
        Email = googleSignInAccount.getEmail();
        ProcessAll();
    }

    private void useLoginInformation(AccessToken accessToken) {
        /**
         Creating the GraphRequest to fetch user details
         1st Param - AccessToken
         2nd Param - Callback (which will be invoked once the request is successful)
         **/
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    firstName = object.getString("name");
                    Email = object.getString("email");
                    String image = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    ProcessAll();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (alreadyloggedAccount != null) {
            //Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
            onLoggedIn(alreadyloggedAccount);
        } else {
            //Log.d(TAG, "Not logged in");
        }

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            useLoginInformation(accessToken);
        }

    }

    public  void updateUI(FirebaseUser user){

    }

    public void GoToMainActivity(){

        db.collection("users").whereEqualTo("email", phoneEdt.getText().toString()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Users miss;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                miss=document.toObject(Users.class);
                                listUser.add(miss);

                                firstName = listUser.get(0).getFirstname();
                                lastName = listUser.get(0).getLastname();
                                Email = listUser.get(0).getEmail();
                                Phone = listUser.get(0).getPhone();
                                Password = passwordEdt.getText().toString();
                                myid = document.getId();

                                ProcessAll();
                            }
                        } else {
                        }
                    }
                });

    }
    public void ProcessAll(){
        editor = sharedpreferences.edit();
        editor.putString(firstName1, firstName);
        editor.putString(lastName1, lastName);
        editor.putString(phoneNumber1, Phone);
        editor.putString(Email1, Email);
        editor.putString(Password1, Password);
        editor.putString(myid1, myid);
        editor.putString(logintype1, loginType);

        editor.commit();

        sharedpreferences.edit().putBoolean("logged",true).apply();
        startActivity(new Intent(this,HomeActivity.class));

    }
    public void GoToMainActivityAuto(){
        sharedpreferences.edit().putBoolean("logged",true).apply();
        startActivity(new Intent(this,HomeActivity.class));

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }
}
