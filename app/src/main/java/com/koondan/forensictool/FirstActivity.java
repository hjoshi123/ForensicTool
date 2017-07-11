package com.koondan.forensictool;

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

import me.yugy.github.reveallayout.RevealLayout;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private FloatingActionButton smsButton, callLogsButton;
    private RevealLayout mRevealLayout;
    private View mRevealView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        smsButton = (FloatingActionButton) findViewById(R.id.sms_button);
        callLogsButton = (FloatingActionButton) findViewById(R.id.call_logs_button);
        mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
        mRevealView = findViewById(R.id.reveal_view);
        smsButton.setOnClickListener(this);
        callLogsButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sms_button:
                checkUserPermissions(smsButton);
                break;
            case R.id.call_logs_button:
                Toast.makeText(this,"Milestone 3",Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this,"Nothing Here",Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserPermissions(FloatingActionButton mFab){
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
        onClickAnimation(mFab);
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

    private void onClickAnimation (final FloatingActionButton mFab)
    {
        mFab.setClickable(false); // Avoid naughty guys clicking FAB again and again...
        int[] location = new int[2];
        mFab.getLocationOnScreen(location);
        location[0] += mFab.getWidth() / 2;
        location[1] += mFab.getHeight() / 2;

        final Intent intent = new Intent(FirstActivity.this, MainActivity.class);

        mRevealView.setVisibility(View.VISIBLE);
        mRevealLayout.setVisibility(View.VISIBLE);

        mRevealLayout.show(location[0], location[1]); // Expand from center of FAB. Actually, it just plays reveal animation.
        mFab.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                /**
                 * Without using R.anim.hold, the screen will flash because of transition
                 * of Activities.
                 */
                overridePendingTransition(0, R.anim.hold);
            }
        }, 600); // 600 is default duration of reveal animation in RevealLayout
        mFab.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFab.setClickable(true);
                mRevealLayout.setVisibility(View.INVISIBLE);
                //mViewToReveal.setVisibility(View.INVISIBLE);
            }
        }, 960);
    }
}
