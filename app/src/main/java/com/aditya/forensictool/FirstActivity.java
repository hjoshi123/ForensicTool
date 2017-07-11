package com.aditya.forensictool;

import android.Manifest;
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
    private FloatingActionButton smsButton, callLogsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        smsButton = (FloatingActionButton) findViewById(R.id.sms_button);
        callLogsButton = (FloatingActionButton) findViewById(R.id.call_logs_button);

        smsButton.setOnClickListener(this);
        callLogsButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sms_button:
                checkUserPermissions();
                break;
            case R.id.call_logs_button:
                Toast.makeText(this,"Milestone 3",Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this,"Nothing Here",Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserPermissions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                Manifest.permission.READ_SMS},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }

        //if SDK is lesser than 23 then execute some function
        startActivity(new Intent(FirstActivity.this,MainActivity.class));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // if permission is granted then execute the same function as above
                    startActivity(new Intent(FirstActivity.this,MainActivity.class));
                } else {
                    // Permission Denied
                    Toast.makeText(this,"Media Permissions necessary" , Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
