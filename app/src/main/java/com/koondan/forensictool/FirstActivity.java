package com.koondan.forensictool;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.sergiocasero.revealfab.RevealFAB;


public class FirstActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private RevealFAB smsButton, callLogsButton;
    private Intent smsIntent;
    private Intent phoneIntent;
    View firstView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        smsIntent = new Intent(FirstActivity.this, MainActivity.class);

        phoneIntent = new Intent(FirstActivity.this, MainActivity.class); //Change class here after completing phone


        smsButton = (RevealFAB) findViewById(R.id.sms_button);
        callLogsButton = (RevealFAB) findViewById(R.id.call_logs_button);

        smsButton.setIntent(smsIntent);
        callLogsButton.setIntent(phoneIntent);

        smsButton.setOnClickListener(new RevealFAB.OnClickListener() {
            @Override
            public void onClick(RevealFAB button, View v) {
                checkUserPermissions(button);
            }
        });

        callLogsButton.setOnClickListener(new RevealFAB.OnClickListener() {
            @Override
            public void onClick(RevealFAB button, View v) {
                checkUserPermissions(button);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        smsButton.onResume();
        callLogsButton.onResume();
    }



    //    @Override
//    public void onClick(View v) {
//        switch(v.getId()){
//            case R.id.sms_button:
//                checkUserPermissions(smsButton);
//                break;
//            case R.id.call_logs_button:
//                Toast.makeText(this,"Milestone 3",Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                Toast.makeText(this,"Nothing Here",Toast.LENGTH_SHORT).show();
//        }
//    }

    private void checkUserPermissions(RevealFAB revealFAB){
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

        revealFAB.startActivityWithAnimation();

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
