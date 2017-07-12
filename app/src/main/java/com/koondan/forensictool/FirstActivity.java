package com.koondan.forensictool;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private FloatingActionButton smsButton, callLogsButton, contactButton, deviceButton;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.READ_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        smsButton = (FloatingActionButton) findViewById(R.id.sms_button);
        callLogsButton = (FloatingActionButton) findViewById(R.id.call_logs_button);
        deviceButton = (FloatingActionButton) findViewById(R.id.device_info_button);
        contactButton = (FloatingActionButton) findViewById(R.id.contacts_button);

        smsButton.setOnClickListener(this);
        callLogsButton.setOnClickListener(this);
        deviceButton.setOnClickListener(this);
        contactButton.setOnClickListener(this);

        if ( Build.VERSION.SDK_INT >= 23){
            if(!hasPermissions(this, PERMISSIONS)){
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }

        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sms_button:
                checkUserPermissionsSMS();
                break;
            case R.id.call_logs_button:
                checkUserPermissionsPhone();
                break;
            case R.id.contacts_button:
                checkUserPermissionsContacts();
                break;
            case R.id.device_info_button:
                startActivity(new Intent(FirstActivity.this,DeviceActivity.class));
                break;
            default:
                Toast.makeText(this,"Nothing Here",Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserPermissionsSMS(){
        if ( Build.VERSION.SDK_INT >= 23){
            if(!hasPermissions(this, PERMISSIONS)){
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
            if(hasPermissions(this, PERMISSIONS)){
                startActivity(new Intent(FirstActivity.this,MainActivity.class));

            }
        }
        else {
            //if SDK is lesser than 23 then execute some function
            startActivity(new Intent(FirstActivity.this, MainActivity.class));
        }

    }

    private void checkUserPermissionsPhone(){
        if ( Build.VERSION.SDK_INT >= 23){
            if(!hasPermissions(this, PERMISSIONS)){
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
            if(hasPermissions(this, PERMISSIONS)){
                startActivity(new Intent(FirstActivity.this,PhoneActivity.class));

            }
        }
        else {
            //if SDK is lesser than 23 then execute some function
            startActivity(new Intent(FirstActivity.this, PhoneActivity.class));
        }

    }

    private void checkUserPermissionsContacts(){
        if ( Build.VERSION.SDK_INT >= 23){
            if(!hasPermissions(this, PERMISSIONS)){
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
            if(hasPermissions(this, PERMISSIONS)){
                startActivity(new Intent(FirstActivity.this,ContactActivity.class));

            }
        }
        else {
            //if SDK is lesser than 23 then execute some function
            startActivity(new Intent(FirstActivity.this, ContactActivity.class));
        }

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_CODE_ASK_PERMISSIONS:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // if permission is granted then execute the same function as above
//                    startActivity(new Intent(FirstActivity.this,MainActivity.class));
//                } else {
//                    // Permission Denied
//                    Toast.makeText(this,"Media Permissions necessary" , Toast.LENGTH_SHORT)
//                            .show();
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
