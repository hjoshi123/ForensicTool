package com.koondan.forensictool;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ArrayList<ContactData> contacts = new ArrayList<>();
    private RecyclerView recyclerView;
    private UsersAdapter adapter = new UsersAdapter(this,contacts);
    private Cursor cur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_contacts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));

        checkUserPermissions();
        adapter.notifyDataSetChanged();

    }

    private void getContacts(){
        ContentResolver cr = getContentResolver();
        cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if(cur.getCount() > 0){
            while(cur.moveToNext()){
                ContactData data = new ContactData();
                data.userName = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                data.phoneNumber = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(data);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cur != null){
            cur.close();
        }
    }

    private void checkUserPermissions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                Manifest.permission.READ_CONTACTS},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }

        //if SDK is lesser than 23 then execute some function
        getContacts();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // if permission is granted then execute the same function as above
                    getContacts();
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

    private class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

        //SongInfo is the model name class
        private ArrayList<ContactData> usersList;
        private android.content.Context mContext;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            private TextView phoneNumber,userName;


            public ViewHolder(View itemView) {
                super(itemView);

                userName = (TextView) findViewById(R.id.username);
                phoneNumber = (TextView) findViewById(R.id.phoneNumber);
            }

            @Override
            public void onClick(View v) {
                //Onclick
            }
        }

        public UsersAdapter(android.content.Context context, ArrayList<ContactData> usersList){
            mContext = context;
            UsersAdapter.this.usersList = usersList;
        }

        private android.content.Context getmContext(){
            return mContext;
        }

        @Override
        public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            //R.layout.sms_thread is the layout xml dealing with how the elements of a single item in RecyclerView should appear
            View view = getLayoutInflater().inflate(R.layout.contacts_item,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(UsersAdapter.ViewHolder holder, int position) {
            ContactData contactData = UsersAdapter.this.usersList.get(position);

            TextView number = holder.phoneNumber;
            number.setText(contactData.phoneNumber);

            TextView name = holder.userName;
            name.setText(contactData.userName);

        }

        @Override
        public int getItemCount() {
            return UsersAdapter.this.usersList.size();
        }
    }
}
