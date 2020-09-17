package com.agiza.center.app;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {

    TextView sendTxt;
    EditText phoneEdt;
    ConstraintLayout passResetConstraint;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        phoneEdt=(EditText) findViewById(R.id.edt_phone_forgot);
        sendTxt=(TextView) findViewById(R.id.txt_send_reset_code);
        passResetConstraint=(ConstraintLayout) findViewById(R.id.constraint_password_reset);

        mAuth = FirebaseAuth.getInstance();

        sendTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = phoneEdt.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter your email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotActivity.this, "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ForgotActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(ForgotActivity.this, "Fail to send reset password email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });



    }
}
