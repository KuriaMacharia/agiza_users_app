package com.agiza.center.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class KovidActivity extends AppCompatActivity {

    ConstraintLayout contactsCons;
    ImageView homeImg;
    ListView contactList;

    FirebaseFirestore db;
    ArrayList<Kovid> listContacts;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_kovin);

        db = FirebaseFirestore.getInstance();
        listContacts= new ArrayList<Kovid>();

        contactsCons=(ConstraintLayout) findViewById(R.id.cons_kovi_emergency_contacts);
        contactList=(ListView) findViewById(R.id.list_contact_centers);
        homeImg=(ImageView) findViewById(R.id.img_home_kovid);

        pDialog = new ProgressDialog(KovidActivity.this);
        pDialog.setMessage("Fetching Contacts. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(KovidActivity.this, HomeActivity.class));
            }
        });

        db.collection("contact_center").orderBy("name")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Kovid miss = document.toObject(Kovid.class);
                        listContacts.add(miss);
                    }
                    KovidAdapter ria = new KovidAdapter(KovidActivity.this,listContacts);
                    contactList.setAdapter(ria);
                    pDialog.dismiss();

                    contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            TextView nameTxt = (TextView)view.findViewById(R.id.txt_police_name_police_list);
                            final TextView contactTxt1 = (TextView)view.findViewById(R.id.txt_phone_kovid_item);
                            final TextView countyTxt1 = (TextView)view.findViewById(R.id.txt_county_kovid_item);

                            LayoutInflater inflater = LayoutInflater.from(KovidActivity.this);
                            final View dialogView = inflater.inflate(R.layout.alert_kovid_call, null);
                            final AlertDialog dialogBuilder = new AlertDialog.Builder(KovidActivity.this).create();
                            dialogBuilder.setView(dialogView);

                            ConstraintLayout callConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_call_alert);
                            ConstraintLayout cancelConstraint=(ConstraintLayout) dialogView. findViewById(R.id.constraint_cancel_alert);
                            TextView selectNameTxt = (TextView)dialogView.findViewById(R.id.txt_selected_provider);
                            TextView statusTxt = (TextView)dialogView.findViewById(R.id.txt_location_status_dialog);
                            selectNameTxt.setText(nameTxt.getText().toString());


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
                    Toast.makeText(KovidActivity.this, "Error Fetching Contacts", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
