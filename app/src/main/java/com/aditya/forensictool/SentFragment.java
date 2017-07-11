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
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class SentFragment extends Fragment {

    private ArrayList<SMSData> sms = new ArrayList<>();
    private ArrayList<SMSData> threadList = new ArrayList<>();
    private ArrayList<String> AllThreads = new ArrayList<>();
    private Cursor cursor1;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private RecyclerView recyclerView;
    private UsersAdapter adapter = new UsersAdapter(getActivity(),threadList);

    public SentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sent, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_sent);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext()));

        checkUserPermissions();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        checkUserPermissions();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(cursor1 != null){
            cursor1.close();
        }
    }

    private void getSMSData() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri uri1 = Uri.parse("content://sms/sent");
        cursor1 = contentResolver.query(uri1,null,null,null,null);
        if(cursor1.moveToFirst()) {
            for(int i=0; i < cursor1.getCount(); i++) {
                SMSData sms = new SMSData();
                sms.setBody(cursor1.getString(cursor1.getColumnIndex("body")));
                sms.setSenderNumber(cursor1.getString(cursor1.getColumnIndex("address")));
                String date = cursor1.getString(cursor1.getColumnIndex("date"));
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

        for(SMSData smsdata: sms) {
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
                Intent intent = new Intent(getActivity(),ViewSentMessagesActivity.class);
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
            View view = getActivity().getLayoutInflater().inflate(R.layout.sms_thread,parent,false);
            UsersAdapter.ViewHolder viewHolder = new UsersAdapter.ViewHolder(view);

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
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) !=
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
                    Toast.makeText(getActivity(),"Media Permissions necessary" , Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
