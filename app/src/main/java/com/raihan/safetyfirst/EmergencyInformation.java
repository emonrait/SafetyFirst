package com.raihan.safetyfirst;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

public class EmergencyInformation extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText name_value;
    private EditText mobile_value;
    private EditText email_value;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_information);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Emergency Information");

        name_value = findViewById(R.id.name_value);
        mobile_value = findViewById(R.id.mobile_value);
        email_value = findViewById(R.id.email_value);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name_value.getText().toString().isEmpty()) {
                    name_value.requestFocus();
                    name_value.setError("Name Required!");
                } else if (mobile_value.getText().toString().isEmpty()) {
                    mobile_value.requestFocus();
                    mobile_value.setError("Mobile No Required!");
                } else if (email_value.getText().toString().isEmpty()) {
                    email_value.requestFocus();
                    email_value.setError("Email Address Required!");
                } else {
                    addPersonalInfo();
                }
            }
        });


    }

    private void addPersonalInfo() {

    }
}
