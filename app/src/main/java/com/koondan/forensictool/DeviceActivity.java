package com.koondan.forensictool;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class DeviceActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> deviceInfo = new ArrayList<>();
    private String serial,model,id,manufacturer,type,user,sdkVersion,board,brand,host,base,release;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        listView = (ListView) findViewById(R.id.listview);

        serial = Build.SERIAL;
        model = Build.MODEL;
        id = Build.ID;
        manufacturer = Build.MANUFACTURER;
        brand = Build.BRAND;
        type = Build.TYPE;
        user = Build.USER;
        base = String.valueOf(Build.VERSION_CODES.BASE);
        board = Build.BOARD;
        sdkVersion = Build.VERSION.SDK;
        release = Build.VERSION.RELEASE;


        deviceInfo.add(serial);
        deviceInfo.add(model);
        deviceInfo.add(id);
        deviceInfo.add(manufacturer);
        deviceInfo.add(brand);
        deviceInfo.add(type);
        deviceInfo.add(user);
        deviceInfo.add(base);


    }
}
