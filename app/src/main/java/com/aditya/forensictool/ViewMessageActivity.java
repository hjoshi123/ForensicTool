package com.aditya.forensictool;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ViewMessageActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ArrayList<SMSData> sms = new ArrayList<>();
    private UsersAdapter adapter = new UsersAdapter(this,sms);
    private RecyclerView recyclerView;
    private String threadId;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);

        threadId = getIntent().getStringExtra("Thread_id");
        Log.d("Ac","Hi" + threadId);

        recyclerView = (RecyclerView) findViewById(R.id.msg_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));

        checkUserPermissions();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSMSData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSMSData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cursor != null){
            cursor.close();
        }
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
                String threadID = cursor.getString(cursor.getColumnIndex("thread_id"));
                if(this.threadId.equals(threadID)){
                    sms.setBody(cursor.getString(cursor.getColumnIndex("body")));
                    String date = cursor.getString(cursor.getColumnIndex("date"));
                    Long timestamp = Long.parseLong(date);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timestamp);

                    Date finaldate = calendar.getTime();
                    DateFormat df = SimpleDateFormat.getDateInstance();

                    sms.setTimeStamp(df.format(finaldate));
                }


                this.sms.add(sms);

                cursor.moveToNext();
            }
        }
    }

    private class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

        private ArrayList<SMSData> usersList;
        private android.content.Context mContext;

        public class ViewHolder extends RecyclerView.ViewHolder{

            private TextView body;
            private TextView date;

            public ViewHolder(View itemView) {
                super(itemView);

                body = (TextView) itemView.findViewById(R.id.body);
                date = (TextView) itemView.findViewById(R.id.date);
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
            View view = getLayoutInflater().inflate(R.layout.list_item_thread,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(UsersAdapter.ViewHolder holder, int position) {
            SMSData details = UsersAdapter.this.usersList.get(position);

            TextView body = holder.body;
            body.setText(details.getBody());

            TextView date = holder.date;
            date.setText(details.getTimeStamp());
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
}
