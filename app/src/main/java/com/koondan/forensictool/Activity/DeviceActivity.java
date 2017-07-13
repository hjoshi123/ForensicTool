package com.koondan.forensictool.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.koondan.forensictool.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import github.nisrulz.easydeviceinfo.base.EasyDeviceMod;
import github.nisrulz.easydeviceinfo.base.EasyDisplayMod;
import github.nisrulz.easydeviceinfo.base.EasySimMod;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class DeviceActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ListView listView;
    private ArrayList<String> deviceInfo = new ArrayList<>();
    private String serial, model, id, manufacturer, type, user, sdkVersion, board, brand, base, release,
            imsi, simSerialNumber, carrier, noOfActiveSim, country, displayRes, screenDensity,
            screenSize, IMEI, OSCodename, bootLoader, phoneNumber;
    private EasySimMod easySimMod;
    private EasyDisplayMod easyDisplayMod;
    private EasyDeviceMod easyDeviceMod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        getSupportActionBar().setTitle("Device Info");

        listView = (ListView) findViewById(R.id.listview);
        easySimMod = new EasySimMod(this);
        easyDisplayMod = new EasyDisplayMod(this);
        easyDeviceMod = new EasyDeviceMod(this);

        checkUserPermissions();

    }

    void getDeviceInfo() {
        deviceInfo.clear();
        serial = "Serial Number\n" + Build.SERIAL;
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
        OSCodename = "OS Name\n" + easyDeviceMod.getOSCodename();
        imsi = "IMSI Number\n" + easySimMod.getIMSI();
        simSerialNumber = "Sim Serial Number\n" + easySimMod.getSIMSerial();
        carrier = "Carrier\n" + easySimMod.getCarrier();
        noOfActiveSim = "Number of Active Sim\n" + String.valueOf(easySimMod.getNumberOfActiveSim());
        country = "Country\n" + easySimMod.getCountry();
        displayRes = "Display Resolution\n" + easyDisplayMod.getResolution();
        screenDensity = "Screen Density\n" + easyDisplayMod.getDensity();
        screenSize = "Screen Size\n" + String.valueOf(easyDisplayMod.getPhysicalSize());
        IMEI = "IMEI\n" + easyDeviceMod.getIMEI();
        phoneNumber = "Phone Number\n" + easyDeviceMod.getPhoneNo();
        bootLoader = "BootLoader\n" + easyDeviceMod.getBootloader();


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
        deviceInfo.add(OSCodename);
        deviceInfo.add(release);
        deviceInfo.add(imsi);
        deviceInfo.add(simSerialNumber);
        deviceInfo.add(carrier);
        deviceInfo.add(country);
        deviceInfo.add(noOfActiveSim);
        deviceInfo.add(displayRes);
        deviceInfo.add(screenDensity);
        deviceInfo.add(screenSize);
        deviceInfo.add(bootLoader);
        deviceInfo.add(phoneNumber);
        deviceInfo.add(IMEI);

        FileOutputStream deleteFile = null;

        final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Forensic/");
        try {
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.e("ALERT", "could not create the directories");
                }
            }

            final File myFile = new File(dir, "device_info" + ".txt");

            if (!myFile.exists()) {
                myFile.createNewFile();
            }
            //Overwriting content in previous file if any
            String header = "Device Info\n";
            deleteFile = new FileOutputStream(myFile);
            deleteFile.write(header.getBytes());
            deleteFile.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        for (String info : deviceInfo) {
            writeToFile(info);
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceInfo);
        listView.setAdapter(adapter);
    }

    public void writeToFile(String body) {
        FileOutputStream fos = null;


        try {
            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Forensic/");

            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.e("ALERT", "could not create the directories");
                }
            }

            final File myFile = new File(dir, "device_info" + ".txt");

            if (!myFile.exists()) {
                myFile.createNewFile();
            }
            body = body.replaceAll("\\n", " : ");
            body = body + "\n";


            //Appending content to clean file
            Boolean append = true;
            fos = new FileOutputStream(myFile, append);

            fos.write(body.getBytes());
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void checkUserPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.READ_PHONE_STATE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        //if SDK is lesser than 23 then execute some function
        getDeviceInfo();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // if permission is granted then execute the same function as above
                    getDeviceInfo();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Media Permissions necessary", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
