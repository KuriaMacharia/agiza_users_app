package com.agiza.center.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AboutActivity extends AppCompatActivity {
    ImageView homeImg;
    TextView versionTxt, facebookTxt, twitterTxt, instagramTxt, checkUpdatesTxt;
    String theVersion, Facebook, Twitter, Instagram, Mantra;
    FirebaseFirestore db;
    Info miss;
    ArrayList<Info> listInfo;

    Context context;
    PackageManager packageManager ;
    String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_about);

        db = FirebaseFirestore.getInstance();
        listInfo= new ArrayList<Info>();

        context = getApplicationContext();
        packageManager = context.getPackageManager();
        packageName = context.getPackageName();

        Facebook="Facebook";
        Instagram="Instagram";
        Twitter="Twitter";

        versionTxt = (TextView) findViewById(R.id.txt_version_about);
        facebookTxt = (TextView) findViewById(R.id.txt_facebook);
        twitterTxt = (TextView) findViewById(R.id.txt_twiiter);
        instagramTxt = (TextView) findViewById(R.id.txt_instagram);
        checkUpdatesTxt = (TextView) findViewById(R.id.txt_check_updates);

        versionTxt.setText("Version 1.1.4");

        homeImg = (ImageView) findViewById(R.id.img_home);
        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AboutActivity.this, HomeActivity.class));
            }
        });

        facebookTxt.setText(Facebook + ": Agiza Resc ");
        twitterTxt.setText(Twitter +": @agizarescue ");
        instagramTxt.setText(Instagram+ ": Agiza Rescue");

        facebookTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOpenFacebookIntent(context);
            }
        });

        instagramTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOpenInstagramIntent(context);
            }
        });

        twitterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOpenTwitterIntent(context);
            }
        });

        checkUpdatesTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUpdateChecker appUpdateChecker=new AppUpdateChecker(AboutActivity.this);  //pass the activity in constructure
                appUpdateChecker.checkForUpdate(true);
            }
        });

    }

    public static Intent getOpenFacebookIntent(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.facebook.android", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/agiza.resc.1"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/agiza.resc.1"));
        }
    }

    public static Intent getOpenInstagramIntent(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.instagram.android", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("instagram://page/agizarescue"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,Uri.parse("https://instagram.com/agizarescue"));
        }
    }

    public static Intent getOpenTwitterIntent(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://page/agizarescue"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,Uri.parse("https://twitter.com/agizarescue"));
        }
    }

    private void FetchVersion(){
        /*try {
            theVersion = packageManager.getPackageInfo(packageName, 0).versionName;
            versionTxt.setText("Version " + theVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/
    }
}