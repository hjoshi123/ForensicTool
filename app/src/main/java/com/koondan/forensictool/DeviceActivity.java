package com.koondan.forensictool;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
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

        serial = "Serial Number\n" +Build.SERIAL;
        model = "Model Name\n" + Build.MODEL;
        id = "Device ID\n" + Build.ID;
        manufacturer = "Manufacturer\n" + Build.MANUFACTURER;
        brand = "Brand\n" + Build.BRAND;
        type = "Type\n" + Build.TYPE;
        user = "User\n" + Build.USER;
        base = "Base Number\n" + String.valueOf(Build.VERSION_CODES.BASE);
        board = "Board\n" + Build.BOARD;
        sdkVersion = "SDK Version\n" + Build.VERSION.SDK;
        release = "Android Release Version Number\n" + Build.VERSION.RELEASE;


        deviceInfo.add(serial);
        deviceInfo.add(model);
        deviceInfo.add(id);
        deviceInfo.add(manufacturer);
        deviceInfo.add(brand);
        deviceInfo.add(type);
        deviceInfo.add(user);
        deviceInfo.add(base);
        deviceInfo.add(board);
        deviceInfo.add(sdkVersion);
        deviceInfo.add(release);

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,deviceInfo);
        listView.setAdapter(adapter);
    }
}
