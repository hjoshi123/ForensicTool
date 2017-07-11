package com.aditya.forensictool;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViewMessageActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ArrayList<SMSData> sms = new ArrayList<>();
    private ArrayList<SMSData> allMsgList = new ArrayList<>();
    private UsersAdapter adapter = new UsersAdapter(this,allMsgList);
    private RecyclerView recyclerView;
    private String allMessagesAsString, title;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);

        allMessagesAsString = getIntent().getStringExtra("Messages");
        title = getIntent().getStringExtra("Title");
        getSupportActionBar().setTitle(title);

        Log.d("Ac","Hi" + allMessagesAsString);
        Gson gson = new Gson();
        Type type = new TypeToken<List<SMSData>>(){}.getType();
        allMsgList = (ArrayList<SMSData>) gson.fromJson(allMessagesAsString, type);
        //List<SMSData> msglist = gson.fromJson(allMessagesAsString, type);

        for(SMSData eachSMS : allMsgList)
        {
            Log.d("Ac", ""+eachSMS.getThreadID()+" "+eachSMS.getBody());
        }
        adapter = new UsersAdapter(this,allMsgList);
        recyclerView = (RecyclerView) findViewById(R.id.msg_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,R.drawable.decoration_item));

        checkUserPermissions();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //getSMSData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //getSMSData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cursor != null){
            cursor.close();
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
        //getSMSData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // if permission is granted then execute the same function as above
                    //
                    // getSMSData();
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
