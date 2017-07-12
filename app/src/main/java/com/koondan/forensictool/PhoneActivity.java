package com.koondan.forensictool;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class PhoneActivity extends AppCompatActivity {
    private ArrayList<CallLogData> logsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UsersAdapter adapter = new UsersAdapter(this,logsList);
    private View view;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        getCallDetails(getApplicationContext());

        getSupportActionBar().setTitle("Call Logs");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_call_log);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        adapter.notifyDataSetChanged();
    }

    private void getCallDetails(Context context) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {

            logsList.clear();
            StringBuffer stringBuffer = new StringBuffer();
            Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    null, null, null, CallLog.Calls.DATE + " DESC");
            int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = cursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
            while (cursor.moveToNext()) {
                CallLogData newLog = new CallLogData();
                newLog.number = cursor.getString(number);
                String callType = cursor.getString(type);
                String callDate = cursor.getString(date);
                Date callDayTime = new Date(Long.valueOf(callDate));
                newLog.timeStamp = callDayTime.toString();
                newLog.duration = cursor.getString(duration);
                String dir = null;
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        newLog.type = "OUTGOING";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        newLog.type = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        newLog.type = "MISSED";
                        break;
                    default:
                        newLog.type = "MISSED";
                }
                Log.d("Phone", newLog.toString());
                logsList.add(newLog);
            }
            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }

    private class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

        //SongInfo is the model name class
        private ArrayList<CallLogData> usersList;
        private android.content.Context mContext;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            private TextView PhoneNumber, Duration, Type, TimeStamp;


            public ViewHolder(View itemView) {
                super(itemView);

                PhoneNumber = (TextView) itemView.findViewById(R.id.phone_number);
                Duration = (TextView) itemView.findViewById(R.id.duration);
                Type = (TextView) itemView.findViewById(R.id.type);
                TimeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                //Onclick
            }
        }

        public UsersAdapter(android.content.Context context, ArrayList<CallLogData> usersList){
            mContext = context;
            UsersAdapter.this.usersList = usersList;
        }

        private android.content.Context getmContext(){
            return mContext;
        }

        @Override
        public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            //R.layout.sms_thread is the layout xml dealing with how the elements of a single item in RecyclerView should appear
            View view = getLayoutInflater().inflate(R.layout.call_log_item,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(UsersAdapter.ViewHolder holder, int position) {
            CallLogData details = UsersAdapter.this.usersList.get(position);

            TextView phone = holder.PhoneNumber;
            phone.setText(details.getNumber());

            TextView duration = holder.Duration;
            int sec = Integer.parseInt(details.getDuration());
            int min = sec/60;
            int s = sec%60;
            String res;
            if(min == 0)
                res = ""+s+"s";
            else
                res = ""+min+"min "+s+"s";
            duration.setText(res);

            TextView type = holder.Type;
            type.setText(details.getType());
            String dateTime = details.getTimeStamp();
            dateTime = dateTime.substring(0, dateTime.length() - 14);
            TextView time = holder.TimeStamp;
            time.setText(dateTime);

        }

        @Override
        public int getItemCount() {
            return UsersAdapter.this.usersList.size();
        }
    }
}
