package com.raihan.safetyfirst.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.raihan.safetyfirst.BuildConfig;
import com.raihan.safetyfirst.R;
import com.raihan.safetyfirst.database.DatabaseHelper;
import com.raihan.safetyfirst.util.CustomKeyboardHide;
import com.raihan.safetyfirst.util.DialogCustom;
import com.raihan.safetyfirst.util.GlobalVariable;
import com.raihan.safetyfirst.util.SendMailMessage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import kotlin.jvm.internal.Intrinsics;

public class MainActivity extends CustomKeyboardHide implements SensorEventListener {
    GlobalVariable globalVariable;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView address;
    private Button btnClick;
    private Button btnSafe;
    private TextView tv_version;
    private Toolbar toolbar;
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    NavigationView navigationView;
    DrawerLayout drawer;
    DatabaseHelper myDB;

    String currentAddress = "";
    ArrayList<String> emailList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        globalVariable = ((GlobalVariable) getApplicationContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        address = findViewById(R.id.address);
        btnClick = findViewById(R.id.btnClick);
        btnSafe = findViewById(R.id.btnSafe);
        tv_version = findViewById(R.id.tv_version);
        myDB = new DatabaseHelper(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View nevhead = navigationView.getHeaderView(0);
        final TextView namet = nevhead.findViewById(R.id.name);
        final TextView emailt = nevhead.findViewById(R.id.email);
        final TextView mobilet = nevhead.findViewById(R.id.mobile);
        getData();
        getEmailList();

        // getCurrentAddress();
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.profile) {
                    Intent a = new Intent(getApplicationContext(), PersonalInformation.class);
                    startActivity(a);
                } else if (item.getItemId() == R.id.emProfile) {
                    Intent a = new Intent(getApplicationContext(), EmergencyInformation.class);
                    startActivity(a);
                } else if (item.getItemId() == R.id.police) {
                    Intent a = new Intent(getApplicationContext(), PoliceInformation.class);
                    startActivity(a);
                } else if (item.getItemId() == R.id.list) {
                    Intent a = new Intent(getApplicationContext(), ViewList.class);
                    startActivity(a);
                }
                return false;
            }
        });

        btnSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(a);
                btnClick.setVisibility(View.VISIBLE);
                btnSafe.setVisibility(View.GONE);
            }
        });

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLocationEnabled(MainActivity.this)) {
                    openSettings();
                } else {
                    getgps();
                    if (!checkPermissionCall()) {
                        requestPermissionCall();
                    } else {
                        if (globalVariable.getName().equals("") || globalVariable.getPhone().equals("") || globalVariable.getEmailList() == null) {
                            DialogCustom.showErrorMessage(MainActivity.this, "Please Update Personal Contact information", "P");

                        } else if (globalVariable.getHelpTeam().equals("") || globalVariable.getPoliceMobile().equals("") || globalVariable.getEmailList() == null) {
                            DialogCustom.showErrorMessage(MainActivity.this, "Please Update Police Contact information", "PO");

                        } else if (globalVariable.getEmName().equals("") || globalVariable.getEmMobile().equals("") || globalVariable.getEmailList() == null) {
                            DialogCustom.showErrorMessage(MainActivity.this, "Please Update Emergency Contact information", "E");

                        } else {
                            sendSMS(globalVariable.getEmMobile(), globalVariable.getAddress());
                            DialogCustom.showCallDialog(MainActivity.this, globalVariable.getPoliceMobile());
                            //call();
                            sendEmail();
                            btnClick.setVisibility(View.GONE);
                            btnSafe.setVisibility(View.VISIBLE);
                        }

                    }

                }
            }
        });

        namet.setText(globalVariable.getName());
        emailt.setText(globalVariable.getEmail());
        mobilet.setText(globalVariable.getPhone());
        tv_version.setText("App Version:-" + BuildConfig.VERSION_NAME);

        requestPermissionCall();
        getgps();
    }

    private void getCurrentAddress(double latitude, double longitude) {
        try {
            Geocoder geo = new Geocoder(MainActivity.this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty()) {
                address.setText("Waiting for Location");
            } else {
                if (addresses.size() > 0) {
                    currentAddress = addresses.get(0).getFeatureName() + ", " +
                            addresses.get(0).getSubThoroughfare() + " " +
                            addresses.get(0).getThoroughfare() + ", " +
                            addresses.get(0).getSubLocality() + ", " +
                            addresses.get(0).getLocality() + "-" +
                            addresses.get(0).getPostalCode() + ", " +
                            addresses.get(0).getAdminArea() + ", " +
                            addresses.get(0).getCountryName() + ", " +
                            //addresses.get(0).toString() + ", " +
                            addresses.get(0).getAddressLine(0);
                    address.setText(currentAddress);
                    globalVariable.setAddress(currentAddress);
                    globalVariable.setLatitude(String.valueOf(addresses.get(0).getLatitude()));
                    globalVariable.setLongitude(String.valueOf(addresses.get(0).getLongitude()));
                    //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }


    private void getgps() {

        if (!checkPermission()) {
            requestPermission();

        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                getCurrentAddress(location.getLatitude(), location.getLongitude());
                                Log.d("latlong", String.valueOf(location.getLatitude()) + location.getLongitude());

                            } else {
                                Log.d("latlong", "Test");

                            }
                        }
                    });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            Toast.makeText(this, "Allow permission for location access!", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        Intrinsics.checkNotNullParameter(permissions, "permissions");
        Intrinsics.checkNotNullParameter(grantResults, "grantResults");
        if (requestCode == 2296) {
            boolean permission = false;
            boolean var8 = false;
            if (grantResults.length != 0) {
                try {
                    permission = grantResults[1] == 0;
                    if (!permission) {
                        Toast.makeText(this, "Allow permission for Location access!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean checkPermission() {
        boolean permission;

        int result = ContextCompat.checkSelfPermission((Context) this, "android.permission.ACCESS_FINE_LOCATION");
        int result1 = ContextCompat.checkSelfPermission((Context) this, "android.permission.ACCESS_COARSE_LOCATION");
        permission = result == 0 && result1 == 0;


        return permission;
    }

    private void requestPermission() {
        try {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        } catch (Exception var6) {
            Intent intent = new Intent();
            intent.setAction("android.settings.ACTION_LOCATION_SOURCE_SETTINGS");
            this.startActivityForResult(intent, 2296);
            Log.e("Errorper", String.valueOf(var6.getMessage()));
        }


    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    public void sendSMS(String phoneNo, String msg) {
        if (phoneNo.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Phone no not found!", Toast.LENGTH_LONG).show();
        } else {
            try {
                String message = "Dear " + globalVariable.getEmName() + "," + "\n" + "\n" + "I am in a danger. please help. Come and rescue me asap. "
                        //  + "\n" + "My Name is: " + globalVariable.getName() + ". "
                        //  + "\n" + "My Phone no is: " + globalVariable.getPhone() + ". "
                        + "\n" + "My Current Location is: " + globalVariable.getMapurl() + DialogCustom.replacecommaDouble(globalVariable.getLatitude()) + "," + DialogCustom.replacecommaDouble(globalVariable.getLongitude());
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, globalVariable.getName(), message, null, null);
                Toast.makeText(getApplicationContext(), "Message Sent",
                        Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }

    }

    private void call() {
        //String number = "+8801816028491";
        String number = globalVariable.getHelpTeam();
        String uri = "tel:" + number.trim();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }


    private boolean checkPermissionCall() {
        boolean permission;

        int result = ContextCompat.checkSelfPermission((Context) this, "android.permission.CALL_PHONE");
        int result1 = ContextCompat.checkSelfPermission((Context) this, "android.permission.SEND_SMS");
        permission = result == 0 && result1 == 0;

        return permission;
    }

    private void requestPermissionCall() {

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);

    }

    @Override
    public void onSensorChanged(SensorEvent se) {
        float x = se.values[0];
        float y = se.values[1];
        float z = se.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter

        if (mAccel > 8) {
            Toast.makeText(getApplicationContext(),
                    "You have shaken your phone", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
            if (mAccel > 15 && btnSafe.getVisibility() == View.GONE) {
                if (!isLocationEnabled(MainActivity.this)) {
                    openSettings();
                } else {
                    getgps();
                    if (!checkPermissionCall()) {
                        requestPermissionCall();
                    } else {
                        if (globalVariable.getName().equals("") || globalVariable.getPhone().equals("") || globalVariable.getEmailList() == null) {
                            DialogCustom.showErrorMessage(MainActivity.this, "Please Update Personal Contact information", "P");

                        } else if (globalVariable.getHelpTeam().equals("") || globalVariable.getPoliceMobile().equals("") || globalVariable.getEmailList() == null) {
                            DialogCustom.showErrorMessage(MainActivity.this, "Please Update Police Contact information", "PO");

                        } else if (globalVariable.getEmName().equals("") || globalVariable.getEmMobile().equals("") || globalVariable.getEmailList() == null) {
                            DialogCustom.showErrorMessage(MainActivity.this, "Please Update Emergency Contact information", "E");

                        } else {
                            sendSMS(globalVariable.getEmMobile(), globalVariable.getAddress());
                            DialogCustom.showCallDialog(MainActivity.this, globalVariable.getPoliceMobile());
                            //call();
                            sendEmail();
                            btnClick.setVisibility(View.GONE);
                            btnSafe.setVisibility(View.VISIBLE);
                        }
                    }

                }

            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }


    private void sendEmail() {
        String email1 = globalVariable.getEmail();
        String subject = "Help Message";

        String message = "Dear " + globalVariable.getHelpTeam() + "," + "\n" + "\n" + "I am in a danger. Please Help me immediately. "
                + "\n" + "My Name is: " + globalVariable.getName() + ". "
                + "\n" + "My Phone no is: " + globalVariable.getPhone() + ". "
                + "\n" + "My Current latitude is: " + globalVariable.getLatitude() + ". "
                + "\n" + "My longitude is: " + globalVariable.getLongitude() + ". "
                + "\n" + "My Current Location is: " + globalVariable.getAddress() + ". "
                + "\n" + "My Current Location map url is: " + globalVariable.getMapurl() + globalVariable.getLatitude() + "," + globalVariable.getLongitude()
                + "\n" + "\n" + "Thanks & Regards"
                + "\n" + "Name: " + globalVariable.getName()
                + "\n" + "Mobile: " + globalVariable.getPhone();

        SendMailMessage sm = new SendMailMessage(this, email1, subject, message, globalVariable.getEmailList());
        sm.execute();

    }

    private void getData() {
        // Cursor cursor = myDB.searchData("P");
        Cursor cursor = myDB.fetch();
        if (cursor.getCount() > 0) {
            //    Toast.makeText(getApplicationContext(), cursor.getString(4), Toast.LENGTH_SHORT).show();
            if (cursor.getString(4).equals("P")) {
                globalVariable.setName(cursor.getString(1));
                globalVariable.setEmail(cursor.getString(2));
                globalVariable.setPhone(cursor.getString(3));
                //Toast.makeText(getApplicationContext(), cursor.getString(1), Toast.LENGTH_SHORT).show();
            }

            if (cursor.getString(4).equals("PO")) {
                globalVariable.setHelpTeam(cursor.getString(1));
                globalVariable.setPoliceEmail(cursor.getString(2));
                globalVariable.setPoliceMobile(cursor.getString(3));
                //Toast.makeText(getApplicationContext(), cursor.getString(1), Toast.LENGTH_SHORT).show();
            }

            if (cursor.getString(4).equals("E")) {
                //  globalVariable.setEmName(cursor.getString(1));
                //  globalVariable.setEmEmail(cursor.getString(2));
                //  globalVariable.setEmMobile(cursor.getString(3));
                //Toast.makeText(getApplicationContext(), cursor.getString(1), Toast.LENGTH_SHORT).show();
            }

            while (cursor.moveToNext()) {
                if (cursor.getString(4).equals("P")) {
                    globalVariable.setName(cursor.getString(1));
                    globalVariable.setEmail(cursor.getString(2));
                    globalVariable.setPhone(cursor.getString(3));
                    //Toast.makeText(getApplicationContext(), cursor.getString(1), Toast.LENGTH_SHORT).show();
                }

                if (cursor.getString(4).equals("PO")) {
                    globalVariable.setHelpTeam(cursor.getString(1));
                    globalVariable.setPoliceEmail(cursor.getString(2));
                    globalVariable.setPoliceMobile(cursor.getString(3));
                    Log.d("police", cursor.getString(1));
                    Log.d("police", cursor.getString(2));
                    Log.d("police", cursor.getString(3));
                    Log.d("police", cursor.getString(4));
                    //Toast.makeText(getApplicationContext(), cursor.getString(1), Toast.LENGTH_SHORT).show();
                }

                if (cursor.getString(4).equals("E")) {
                    globalVariable.setEmName(cursor.getString(1));
                    globalVariable.setEmEmail(cursor.getString(2));
                    globalVariable.setEmMobile(cursor.getString(3));
                    //Toast.makeText(getApplicationContext(), cursor.getString(1), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void getEmailList() {
        Cursor cursor = myDB.fetch();
        if (cursor.getCount() > 0) {
            // Log.d("cursor-->", cursor.getString(0));
            emailList.add(cursor.getString(2));
            while (cursor.moveToNext()) {
                emailList.add(cursor.getString(2));
                Log.d("cursor-->", cursor.getString(2));
            }
            globalVariable.setEmailList(emailList);
        }
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }
}

