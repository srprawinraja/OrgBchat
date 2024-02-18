package com.example.BOneOnOneChat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
    private Intent i;
    private ArrayList <String[]> userDetail=new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private boolean isLocationEnabled;
    private Float  androidVersion;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBluetoothAdapter.isEnabled()){
           // if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)
            mBluetoothAdapter.enable();
        }
        LottieAnimationView arrw = findViewById(R.id.img);
        ImageView img = findViewById(R.id.emoji);
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        mBluetoothAdapter.setName(sharedPreferences.getString("name", ""));
        String change = Build.VERSION.RELEASE;
        change = change.substring(0, 2);
        androidVersion = Float.valueOf(change);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ListView listNew = findViewById(R.id.listnew);
        listNew.setDivider(null);
        listNew.setDividerHeight(60);
        ImageButton bluetoothPermission = findViewById(R.id.blue);
        ImageButton setting = findViewById(R.id.imageButton);
        SqDatabase sqDatabase = new SqDatabase(MainActivity.this);
        userDetail=sqDatabase.retrieve();
        if(userDetail.size()!=0) {
            arrw.setVisibility(View.INVISIBLE);
            img.setVisibility(View.INVISIBLE);
        }
            String[] A=new String[userDetail.size()];
            Geek1 geek1=new Geek1(this,A,userDetail);
            listNew.setAdapter(geek1);



            setting.setOnClickListener(v -> {
            i = new Intent(getApplicationContext(), Setting.class);
            startActivity(i);
        });

        bluetoothPermission.setOnClickListener(v -> {

            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
            if(androidVersion>=12){
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_ADVERTISE,Manifest.permission.BLUETOOTH_CONNECT},1);

            }
            else if (androidVersion > 9) {
                requestpermission();
            } else {
              //  requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
                i = new Intent(getApplicationContext(), BluetoothSearch.class);
                startActivity(i);
            }

        });
        listNew.setOnItemClickListener((parent, view, position, id) -> {

            if(!mBluetoothAdapter.isEnabled()) Toast.makeText(this, "enable bluetooth", Toast.LENGTH_SHORT).show();
            else {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(userDetail.get(position)[1]);

                Intent i = new Intent(this, BluetoothChatting.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                i.putExtra("device_name", (device.getName()==null)? userDetail.get(position)[2] : device.getName());
                i.putExtra("device_address", userDetail.get(position)[1]);
                i.putExtra("option", device);
                i.putExtra("decider", "main");
                startActivity(i);
            }
        });


    }

    private void requestpermission() {

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                boolean isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isLocationEnabled) {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        i = new Intent(getApplicationContext(), BluetoothSearch.class);
                        startActivity(i);
                    }
                    else{
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                    }
                }
                else{
                   checkLocationServicesEnabled();
                }

        }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 || requestCode==1) {


           if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               checkLocationServicesEnabled();
            } else {


               AlertDialog.Builder builder = new AlertDialog.Builder(this);
               builder.setTitle("Permission")
                       .setMessage("For Android 10 or greater version location is necessary to scan for other devices. ")
                       .setPositiveButton("OK", (dialog, which) -> {
                           // Do something when the OK button is clicked
                           requestpermission();
                       })
                       .setNegativeButton("Cancel", (dialog, which) -> {
                           // Do something when the Cancel button is clicked
                       });

               AlertDialog alertDialog = builder.create();
               alertDialog.show();
                // Permission denied


            }
        }
        if(requestCode==200){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                i = new Intent(getApplicationContext(), BluetoothSearch.class);

                startActivity(i);
            }
        }

    }

    private void checkLocationServicesEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Location services are not enabled, prompt the user to enable them
             isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


            if (!isLocationEnabled) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permissions")
                        .setMessage("For Android 10 or greater version location is necessary to scan for other devices.enable your location ")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Do something when the OK button is clicked
                            isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                            if(isLocationEnabled){
                                i=new Intent(getApplicationContext(),BluetoothSearch.class);
                                startActivity(i);
                            }else {
                                Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(settingsIntent);
                                isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                                if(isLocationEnabled){
                                    i=new Intent(getApplicationContext(),BluetoothSearch.class);
                                    startActivity(i);
                                }
                            }

                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // Do something when the Cancel button is clicked
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            else{
                i=new Intent(getApplicationContext(),BluetoothSearch.class);
                startActivity(i);
            }
        }
    }



    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();

        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String myString = sharedPreferences.getString("oldName", "");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.setName(myString);

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}



