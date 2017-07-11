package com.aditya.forensictool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {
    private Button smsButton;
    private Button callLogsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        smsButton = (Button) findViewById(R.id.sms_button);
        callLogsButton = (Button) findViewById(R.id.call_logs_button);

        smsButton.setOnClickListener(this);
        callLogsButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sms_button:
                startActivity(new Intent(FirstActivity.this,MainActivity.class));
                break;
            case R.id.call_logs_button:
                Toast.makeText(this,"Clicked on Call Logs",Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this,"nothing here",Toast.LENGTH_SHORT).show();
        }
    }
}
