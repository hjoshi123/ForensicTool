package com.aditya.forensictool;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private RecyclerView recyclerView;
    private ArrayList<SMSData> sms = new ArrayList<>();
    private ArrayList<SMSData> threadList = new ArrayList<>();
    private ArrayList<String> AllThreads = new ArrayList<>();
    private UsersAdapter adapter = new UsersAdapter(this,threadList);
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_main);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));

        checkUserPermissions();
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSMSData();
        adapter.notifyDataSetChanged();
    }

    private void getSMSData(){
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://sms/inbox");
        cursor = contentResolver.query(uri,null,null,null,null);
        String[] columns = new String[] { "address", "person", "date", "body","type", "thread_id" };
        startManagingCursor(cursor);

        // Read the sms data and store it in the list
        if(cursor.moveToFirst()) {
            for(int i=0; i < cursor.getCount(); i++) {
                SMSData sms = new SMSData();
                sms.setBody(cursor.getString(cursor.getColumnIndex("body")));
                sms.setSenderNumber(cursor.getString(cursor.getColumnIndex("address")));
                String date = cursor.getString(cursor.getColumnIndex(columns[2]));
                String threadID = cursor.getString(cursor.getColumnIndex("thread_id"));
                sms.setThreadID(threadID);
                Long timestamp = Long.parseLong(date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);

                Date finaldate = calendar.getTime();
                String smsDate = finaldate.toString();
                sms.setTimeStamp(smsDate);
                Log.d("SMS", smsDate);
                this.sms.add(sms);

                cursor.moveToNext();
            }
        }

        /* Sent SMS
        ContentResolver contentResolver1 = getContentResolver();
        Uri uri1 = Uri.parse("content://sms/sent");
        Cursor cursor1 = contentResolver.query(uri1,null,null,null,null);
        if(cursor1.moveToFirst()) {
            for(int i=0; i < cursor1.getCount(); i++) {
                SMSData sms = new SMSData();
                sms.setBody(cursor1.getString(cursor1.getColumnIndex("body")));
                sms.setSenderNumber(cursor1.getString(cursor1.getColumnIndex("address")));
                String date = cursor1.getString(cursor1.getColumnIndex(columns[2]));
                String threadID = cursor1.getString(cursor1.getColumnIndex("thread_id"));
                sms.setThreadID(threadID);
                Long timestamp = Long.parseLong(date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);

                Date finaldate = calendar.getTime();
                String smsDate = finaldate.toString();
                sms.setTimeStamp(smsDate);
                Log.d("SMS", smsDate);
                this.sms.add(sms);

                cursor1.moveToNext();
            }
        }
        cursor1.close();
        */

        for (SMSData smsdata: sms) {
            if(AllThreads.contains(smsdata.getThreadID()))
            {

            }
            else
            {
                threadList.add(smsdata);
                AllThreads.add(smsdata.getThreadID());
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSMSData();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cursor != null){
            cursor.close();
        }
    }

    private class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

        //SongInfo is the model name class
        private ArrayList<SMSData> usersList;
        private android.content.Context mContext;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            private TextView ThreadName;


            public ViewHolder(View itemView) {
                super(itemView);

                ThreadName = (TextView) itemView.findViewById(R.id.thread_title);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                //StartActivity
                int position = getAdapterPosition();
                Intent intent = new Intent(MainActivity.this,ViewMessageActivity.class);
                intent.putExtra("Thread_id",sms.get(position).getThreadID());
                startActivity(intent);
            }
        }

        public UsersAdapter(android.content.Context context, ArrayList<SMSData> usersList){
            mContext = context;
            UsersAdapter.this.usersList = usersList;
        }

        private android.content.Context getmContext(){
            return mContext;
        }

        @Override
        public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            //R.layout.sms_thread is the layout xml dealing with how the elements of a single item in RecyclerView should appear
            View view = getLayoutInflater().inflate(R.layout.sms_thread,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(UsersAdapter.ViewHolder holder, int position) {
            SMSData details = UsersAdapter.this.usersList.get(position);

            TextView name = holder.ThreadName;
            name.setText(details.getSenderNumber());

        }

        @Override
        public int getItemCount() {
            return UsersAdapter.this.usersList.size();
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
        getSMSData();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // if permission is granted then execute the same function as above
                    getSMSData();
                } else {
                    // Permission Denied
                    Toast.makeText( this,"Media Permissions necessary" , Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
