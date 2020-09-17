package com.agiza.center.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    SharedPreferences pref;
    public static final String themename1 = "themenameKey";
    String languages []={"English", "Kiswahili"};
    Spinner languageSpin;
    View whiteImg, goldImg, blueImg;
    ImageView homeImg;
    int themeName;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        themeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_settings);

        whiteImg=(View) findViewById(R.id.img_white_theme);
        goldImg=(View) findViewById(R.id.img_gold_theme);
        blueImg=(View) findViewById(R.id.img_blue_theme);
        languageSpin=(Spinner) findViewById(R.id.spin_language_home);
        homeImg=(ImageView) findViewById(R.id.img_home);

        languageSpin.setOnItemSelectedListener(SettingsActivity.this);
        ArrayAdapter<String> scsc = new ArrayAdapter<String>(this, R.layout.spinner_item, languages);
        scsc.setDropDownViewResource(R.layout.spinner_item);
        languageSpin.setAdapter(scsc);

        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
            }
        });

        goldImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themeUtils.changeToTheme(SettingsActivity.this, themeUtils.GOLD);
                editor = pref.edit();
                editor.putInt("theme", 2);
                editor.commit();
                try {
                    themeName=getPackageManager().getActivityInfo(getComponentName(), 0).getThemeResource();
                    startActivity(new Intent(SettingsActivity.this, HomeActivity.class));

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        blueImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themeUtils.changeToTheme(SettingsActivity.this, themeUtils.BLACK);

                editor = pref.edit();
                editor.putInt("theme", 1);
                editor.commit();

                try {
                    themeName=getPackageManager().getActivityInfo(getComponentName(), 0).getThemeResource();

                    startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        whiteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themeUtils.changeToTheme(SettingsActivity.this, themeUtils.LIGHT);

                editor = pref.edit();
                editor.putInt("theme", 0);
                editor.commit();
                try {
                    themeName=getPackageManager().getActivityInfo(getComponentName(), 0).getThemeResource();
                    startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
