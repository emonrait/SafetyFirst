package com.raihan.safetyfirst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.raihan.safetyfirst.database.DatabaseHelper;
import com.raihan.safetyfirst.util.GlobalVariable;

import java.util.ArrayList;
import java.util.Objects;

public class ViewList extends AppCompatActivity {
    GlobalVariable globalVariable;
    DatabaseHelper myDB;
    ArrayList<String> spinerList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    private Toolbar toolbar;
    private Spinner id_value;
    private EditText update_name_value;
    private EditText update_mobile_value;
    private EditText update_email_address_value;
    private EditText update_info_type_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        globalVariable = ((GlobalVariable) getApplicationContext());
        myDB = new DatabaseHelper(this);

        toolbar = findViewById(R.id.toolbar);
        id_value = findViewById(R.id.id_value);
        update_name_value = findViewById(R.id.update_name_value);
        update_mobile_value = findViewById(R.id.update_mobile_value);
        update_email_address_value = findViewById(R.id.update_email_address_value);
        update_info_type_value = findViewById(R.id.update_info_type_value);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.info_view_list);

        id_value.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!id_value.getSelectedItem().toString().trim().isEmpty()) {
                    update_name_value.setText("");
                    update_email_address_value.setText("");
                    update_mobile_value.setText("");
                    update_info_type_value.setText("");
                    getPersonInfo();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        getData();
        //getPersonInfo();
    }

    private void getData() {
        // Cursor cursor = myDB.searchData("P");
        Cursor cursor = myDB.fetch();
        if (cursor.getCount() > 0) {
            // Log.d("cursor-->", cursor.getString(0));
            spinerList.add(cursor.getString(0));
            while (cursor.moveToNext()) {
                spinerList.add(cursor.getString(0));
                Log.d("cursor-->", cursor.getString(0));
            }

            adapter = new ArrayAdapter<String>(ViewList.this, android.R.layout.simple_spinner_dropdown_item, spinerList);

            id_value.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
    }

    private void getPersonInfo() {
        Cursor cursor = myDB.searchData(id_value.getSelectedItem().toString());
        if (cursor.getCount() > 0) {
            try {
                while (cursor.moveToNext()) {
                    Log.d("valuetest-->", String.valueOf(cursor.getString(1)));
                    String name = cursor.getString(1);
                    String email = cursor.getString(2);
                    String mobile = cursor.getString(3);
                    String flag = cursor.getString(4);
                    String valueflg = "";
                    if (flag.equals("P")) {
                        valueflg = "Personal Info";
                    } else if (flag.equals("PO")) {
                        valueflg = "Police Info";
                    } else if (flag.equals("E")) {
                        valueflg = "Emergency Info";
                    }
                    update_name_value.setText(name);
                    update_email_address_value.setText(email);
                    update_mobile_value.setText(mobile);
                    update_info_type_value.setText(valueflg);
                }

            } catch (Exception e) {
                Log.d("valuetest-->", String.valueOf(e.getMessage()));
            }
        }
    }

}