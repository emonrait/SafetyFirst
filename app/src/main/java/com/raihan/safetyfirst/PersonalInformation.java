package com.raihan.safetyfirst;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.raihan.safetyfirst.database.DatabaseHelper;

public class PersonalInformation extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText name_value;
    private EditText mobile_value;
    private EditText email_value;
    private Button btnSubmit;
    DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Personal Information");

        myDB = new DatabaseHelper(this);
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
        String name = name_value.getText().toString().trim();
        String email = email_value.getText().toString().trim();
        String phone = mobile_value.getText().toString().trim();
        String flag = "P";
        boolean insertData = myDB.insertData(name, email, phone, flag);
        if (insertData == true) {
            name_value.setText("");
            email_value.setText("");
            mobile_value.setText("");
            Toast.makeText(PersonalInformation.this, "Data Insert successful.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(PersonalInformation.this, "Data Insert unsuccessful.", Toast.LENGTH_SHORT).show();
        }
    }
}