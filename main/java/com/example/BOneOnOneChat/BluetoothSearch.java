package com.example.BOneOnOneChat;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class BluetoothSearch extends AppCompatActivity {

    private LottieAnimationView searchButton;
    @SuppressLint("StaticFieldLeak")
    protected static ProgressBar progressBar;
    private   float androidVersion;
    private ArrayList<String> allDevicesName;
    private  AlertDialog.Builder builder;
    private ListView listView;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice[] allDevices;
    private int index,width=100;
    private TextView status;
    private HashSet<String> set;

    @Override
    protected void onStart() {
        super.onStart();
        deviceDiscoverble();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_search);

        // android version
        String change = Build.VERSION.RELEASE;
        change = change.substring(0, 2);
        androidVersion = Float.parseFloat(change);
        allDevicesName=new ArrayList<>();
        allDevices=new BluetoothDevice[70];
        set=new HashSet<>();

        IntentFilter fill= new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothDeviceListener , fill);
        IntentFilter fill1= new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothDeviceListener , fill1);


        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        listView=findViewById(R.id.listHome);
        searchButton=findViewById(R.id.img);
        progressBar=findViewById(R.id.progress);       progressBar.setVisibility(View.VISIBLE);
        ImageButton searchExit = findViewById(R.id.back);
        status=findViewById(R.id.textView);


        builder = new AlertDialog.Builder(this);
        bluetoothAdapter.startDiscovery();
        listView.setDivider(null);




        //search button
        searchButton.setOnClickListener(v -> {
            listView.setDividerHeight(20);
            status.setText(" ");
            set.clear();
            index=0;
            allDevicesName.clear();

            if(androidVersion<=9) {
                deviceDiscoverble();
                progressBar.setVisibility(View.VISIBLE);
                bluetoothAdapter.startDiscovery();
            }
            else{
                if(!checkLocation()) checkPermission();
                else{
                    bluetoothAdapter.startDiscovery();
                    progressBar.setVisibility(View.VISIBLE);
                    deviceDiscoverble();
                }
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            //if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)
                bluetoothAdapter.enable();
            BluetoothDevice dv=allDevices[position];
            //Log.d("salaar",allDevicesName.get(position));
            Intent i=new Intent(BluetoothSearch.this,BluetoothChatting.class);
            String name=(dv.getName()==null)? allDevicesName.get(position) : dv.getName();
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.putExtra("device_name",name);
            i.putExtra("device_address",dv.getAddress());
            i.putExtra("option",dv);
            i.putExtra("decider","search");
            startActivity(i);
        });

        searchExit.setOnClickListener(v -> {
            bluetoothAdapter.cancelDiscovery();
            Intent intent=new Intent(getBaseContext(),MainActivity.class);
            startActivity(intent);

        });

    }



    @SuppressLint("MissingPermission")



    //search button width heigth set
    private void setButton_w_h(int operator) {

        ViewGroup.LayoutParams layoutParams = searchButton.getLayoutParams();

        if(operator==0 && width==100) {
            layoutParams.width = searchButton.getWidth() - 300;
            layoutParams.height = searchButton.getHeight() -300;
            width-=10;
            searchButton.pauseAnimation();
        }
        else if(operator==6 && width==90){
            layoutParams.width = searchButton.getWidth() + 300;
            layoutParams.height = searchButton.getHeight() + 300;
            width+=10;
            searchButton.playAnimation();

        }

        searchButton.requestLayout();
    }

    private Boolean checkLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void deviceDiscoverble() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120); // Set the duration in seconds
        startActivity(discoverableIntent);
    }

    // location enable check
    @SuppressLint("MissingPermission")
    protected void checkPermission() {




            // location isn't enabled
            builder.setTitle("Permission")
                    .setMessage("For Android 10 or greater version location is necessary to scan for other devices. ")
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Do something when the OK button is clicked
                        bluetoothAdapter.cancelDiscovery();
                        progressBar.setVisibility(View.GONE);
                        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // Do something when the Cancel button is clicked
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();


    }BroadcastReceiver bluetoothDeviceListener=new BroadcastReceiver() {
        @SuppressLint({"MissingPermission", "SetTextI18n"})
        @Override
        public void onReceive(Context context, Intent intent) {

            String action= intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!set.contains(device.getAddress())&& device.getName().length()>0){
                    allDevices[index] = device;

                allDevicesName.add(device.getName());
                set.add(device.getAddress());
            }





            }
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                int[] pic =new int[allDevicesName.size()];
                Arrays.fill(pic,R.drawable.ic_baseline_person_24);
                if(allDevicesName.size()==0) {setButton_w_h(6);status.setText("no friends found"); Toast.makeText(getBaseContext(), "no device were found", Toast.LENGTH_SHORT).show();}
                else{ setButton_w_h(0);status.setText("("+allDevicesName.size()+")"+" friends  found");}
                progressBar.setVisibility(View.GONE);
                geek gk = new geek(BluetoothSearch.this, allDevicesName.toArray(new String[0]), pic);
                listView.setAdapter(gk);
            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        bluetoothAdapter.cancelDiscovery();
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
    }
}




