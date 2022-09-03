package com.raihan.safetyfirst.view;

import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.raihan.safetyfirst.R;
import com.raihan.safetyfirst.database.DatabaseHelper;
import com.raihan.safetyfirst.util.CustomKeyboardHide;
import com.raihan.safetyfirst.util.GlobalVariable;

public class PersonalInformation extends CustomKeyboardHide {
    GlobalVariable globalVariable;
    private Toolbar toolbar;
    private EditText name_value;
    private EditText mobile_value;
    private EditText email_value;
    private Button btnSubmit;
    DatabaseHelper myDB;
    String oldValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Personal Information");
        globalVariable = ((GlobalVariable) getApplicationContext());
        myDB = new DatabaseHelper(this);
        name_value = findViewById(R.id.name_value);
        mobile_value = findViewById(R.id.mobile_value);
        email_value = findViewById(R.id.email_value);
        btnSubmit = findViewById(R.id.btnSubmit);
        getData();

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

    private void getData() {
        // Cursor cursor = myDB.searchData("P");
        Cursor cursor = myDB.fetch();
        if (cursor.getCount() > 0) {
            //    Toast.makeText(getApplicationContext(), cursor.getString(4), Toast.LENGTH_SHORT).show();
            if (cursor.getString(4).equals("P")) {
                oldValue = cursor.getString(4);
                globalVariable.setName(cursor.getString(1));
                globalVariable.setEmail(cursor.getString(2));
                globalVariable.setPhone(cursor.getString(3));
                //Toast.makeText(getApplicationContext(), cursor.getString(1), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void addPersonalInfo() {
        String name = name_value.getText().toString().trim();
        String email = email_value.getText().toString().trim();
        String phone = mobile_value.getText().toString().trim();
        String flag = "P";
        if (oldValue.equals(flag)) {
            Toast.makeText(PersonalInformation.this, "Information already updated.", Toast.LENGTH_SHORT).show();

        } else {
            boolean insertData = myDB.insertData(name, email, phone, flag);
            globalVariable.setName(name);
            globalVariable.setEmail(email);
            globalVariable.setPhone(phone);
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
}