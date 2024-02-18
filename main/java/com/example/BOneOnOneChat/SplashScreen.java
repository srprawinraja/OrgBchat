package com.example.BOneOnOneChat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;


@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        new Handler().postDelayed(() -> {
            if (!mBluetoothAdapter.isEnabled()) {
              //  if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    mBluetoothAdapter.enable();
               // }

            }
            SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
            String myString = sharedPreferences.getString("name", null);

            Intent i;
            if(myString==null){
                i = new Intent(SplashScreen.this, Name.class);
            }
            else{
                i = new Intent(SplashScreen.this, MainActivity.class);
            }
            startActivity(i);
            finish();

        },1000);
    }
}