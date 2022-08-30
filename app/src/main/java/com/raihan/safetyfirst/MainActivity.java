package com.raihan.safetyfirst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import kotlin.jvm.internal.Intrinsics;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    GlobalVariable globalVariable;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView address;
    private Button btnClick;
    private Toolbar toolbar;
    String currentAddress = "";
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    NavigationView navigationView;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        globalVariable = ((GlobalVariable) getApplicationContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        address = findViewById(R.id.address);
        btnClick = findViewById(R.id.btnClick);
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
                }
                return false;
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
                        call();
                        sendEmail();
                        sendSMS(globalVariable.getPoliceMobile(), globalVariable.getAddress());
                    }

                }
            }
        });

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
                            addresses.get(0).getLocality() + ", " +
                            addresses.get(0).getAdminArea() + ", " +
                            addresses.get(0).getCountryName() + ", " +
                            //addresses.get(0).toString() + ", " +
                            addresses.get(0).getPostalCode();
                    address.setText(currentAddress);
                    globalVariable.setAddress(currentAddress);
                    //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }

    /*
        private void checkPermission2() {
            if (checkPermission()) {
                // If this check succeeds, proceed with normal processing.
                // Otherwise, prompt user to get valid Play Services APK.
                if (!AppUtils.isLocationEnabled(MainActivity.this)) {
                    // notify user
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setMessage("Location not enabled!");
                    dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub

                        }
                    });
                    dialog.show();
                }
                // buildGoogleApiClient();
            } else {
                requestPermission();
            }

        }

    */
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
        if (requestCode == 2296 && Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
            Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_LONG).show();
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
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
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
        boolean var10000;

        int result = ContextCompat.checkSelfPermission((Context) this, "android.permission.CALL_PHONE");
        int result1 = ContextCompat.checkSelfPermission((Context) this, "android.permission.SEND_SMS");
        var10000 = result == 0 && result1 == 0;

        return var10000;
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
            if (mAccel > 10) {
                getgps();
                call();
                sendEmail();
                sendSMS(globalVariable.getPoliceMobile(), globalVariable.getAddress());
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
        String subject = "Welcome Message to ";

        String message = "Dear " + globalVariable.getHelpTeam() + "," + "\n" + "\n" + "I am in a danger. Please Help me immediately. "
                + "\n" + "My Name is: " + globalVariable.getName() + ". "
                + "\n" + "My Phone no is: " + globalVariable.getPhone() + ". "
                + "\n" + "My Current Location is: " + globalVariable.getAddress() + ". "
                + "\n" + "\n" + "Thanks & Regards"
                + "\n" + "Name: " + globalVariable.getName()
                + "\n" + "Mobile: " + globalVariable.getPhone();

        SendMailMessage sm = new SendMailMessage(this, email1, subject, message);
        sm.execute();

    }
}

