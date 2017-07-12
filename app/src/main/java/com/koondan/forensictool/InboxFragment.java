package com.koondan.forensictool;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.koondan.forensictool.Storage.SMSContract.SMSEntry;
import com.koondan.forensictool.Storage.SMSHelperMethodInbox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {

    private ArrayList<SMSData> smsList = new ArrayList<>();
    private ArrayList<SMSData> threadList = new ArrayList<>();
    private ArrayList<String> AllThreads = new ArrayList<>();
    private Cursor cursor;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private RecyclerView recyclerView;
    private UsersAdapter adapter = new UsersAdapter(getActivity(),threadList);
    private View view;
    private ProgressDialog mProgressDialog;

    public InboxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(view == null){
            view = inflater.inflate(R.layout.fragment_inbox, container, false);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_inbox);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext()));
            mProgressDialog = new ProgressDialog(getActivity());
        }
        getSMSData();
        return view;

    }

    private void getSMSData() {
        Thread insertOp = new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.setMessage("Saving Data....");
                        mProgressDialog.setIndeterminate(true);
                        mProgressDialog.show();
                    }
                });

                smsList.clear();
                ContentResolver contentResolver = getActivity().getContentResolver();
                Uri uri = Uri.parse("content://sms/inbox");
                cursor = contentResolver.query(uri, null, null, null, null);
                String[] columns = new String[]{"address", "person", "date", "body", "type", "thread_id"};
                getActivity().startManagingCursor(cursor);

                // Read the sms data and store it in the list
                if (cursor.moveToFirst()) {
                    for (int i = 0; i < cursor.getCount(); i++) {
                        final SMSData sms = new SMSData();
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
                        InboxFragment.this.smsList.add(sms);
                        putSMStoDatabase(sms,getContext());

                        cursor.moveToNext();
                    }
                }

                for(SMSData smsdata: smsList) {
                    if(AllThreads.contains(smsdata.getThreadID()))
                    {

                    }
                    else
                    {
                        threadList.add(smsdata);
                        AllThreads.add(smsdata.getThreadID());
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.cancel();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        insertOp.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
                Gson gson = new Gson();
                int position = getAdapterPosition();
                String Thread_ID = threadList.get(position).getThreadID();
                ArrayList<SMSData> inboxSMS = new ArrayList<>();
                for (SMSData eachSMS : smsList)
                {
                    if(eachSMS.getThreadID().equals(Thread_ID))
                        inboxSMS.add(eachSMS);
                }

                String inboxSMSsent = gson.toJson(inboxSMS);

                //StartActivity

                Intent intent = new Intent(getActivity(),ViewMessageActivity.class);
                intent.putExtra("Messages",inboxSMSsent);
                intent.putExtra("Title", threadList.get(position).getSenderNumber());
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

    private void putSMStoDatabase(SMSData sms, Context context){
        SMSHelperMethodInbox helper = new SMSHelperMethodInbox(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(SMSEntry.COLUMN_SENDER_ADDRESS,sms.getSenderNumber());
        values.put(SMSEntry.COLUMN_MSG_BODY,sms.getBody());
        values.put(SMSEntry.COLUMN_MSG_THREAD,sms.getThreadID());
        values.put(SMSEntry.COLUMN_MSG_DATE,sms.getTimeStamp());

        long id = db.insert(SMSEntry.TABLE_NAME,null,values);

        if(id == -1){
            Toast.makeText(getActivity(),"Data not saved\nPlease Try again",Toast.LENGTH_SHORT).show();
            return;
        }
        db.close();
    }

}
