package com.koondan.forensictool.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.koondan.forensictool.Data.ContactData;
import com.koondan.forensictool.DividerItemDecoration;
import com.koondan.forensictool.R;
import com.koondan.forensictool.Storage.ContactContract;
import com.koondan.forensictool.Storage.ContactHelperMethod;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ArrayList<ContactData> contacts = new ArrayList<>();
    private ArrayList<String> numbers = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressDialog mProgressDialog;
    private UsersAdapter adapter;
    private Cursor cur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_contacts);
        adapter = new UsersAdapter(this, contacts);
        checkUserPermissions();
        getSupportActionBar().setTitle("Contacts");
        mProgressDialog = new ProgressDialog(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));


        adapter.notifyDataSetChanged();

    }

    private void getContacts() {
        Log.d("Called", "contacts");
        Thread insertOp = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.setMessage("Saving Data....");
                        mProgressDialog.setIndeterminate(true);
                        mProgressDialog.show();
                    }
                });
                contacts.clear();
                numbers.clear();
                ContentResolver cr = getContentResolver();
                cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        ContactData data = new ContactData();
                        data.userName = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String phone = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phone = phone.replaceAll("\\s+", "");
                        data.phoneNumber = phone;
                        if (numbers.contains(phone)) {
                            //Do nothing
                        } else {
                            contacts.add(data);
                            numbers.add(phone);
                            putContacttoDatabase(data, getApplicationContext());
                        }

                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.cancel();
                        Toast.makeText(getApplicationContext(), "Contact List saved in Forensic directory in Device Storage", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        insertOp.start();

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cur != null) {
            cur.close();
        }
    }

    private void checkUserPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.READ_CONTACTS},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
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
                    Toast.makeText(this, "Media Permissions necessary", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

        private ArrayList<ContactData> usersList;
        private android.content.Context mContext;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView phoneNumberTextView, userNameTextView;


            public ViewHolder(View itemView) {
                super(itemView);

                userNameTextView = (TextView) itemView.findViewById(R.id.user_name);
                phoneNumberTextView = (TextView) itemView.findViewById(R.id.phone_number);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                //Onclick
            }
        }

        public UsersAdapter(android.content.Context context, ArrayList<ContactData> usersList) {
            mContext = context;
            UsersAdapter.this.usersList = usersList;
        }

        private android.content.Context getmContext() {
            return mContext;
        }

        @Override
        public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            //R.layout.sms_thread is the layout xml dealing with how the elements of a single item in RecyclerView should appear
            View view = getLayoutInflater().inflate(R.layout.contacts_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(UsersAdapter.ViewHolder holder, int position) {
            ContactData contactData = UsersAdapter.this.usersList.get(position);

            TextView number = holder.phoneNumberTextView;
            number.setText(contactData.phoneNumber);

            TextView name = holder.userNameTextView;
            name.setText(contactData.userName);

        }

        @Override
        public int getItemCount() {
            return UsersAdapter.this.usersList.size();
        }
    }


    private void putContacttoDatabase(ContactData contact, Context context) {
        ContactHelperMethod helper = new ContactHelperMethod(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ContactContract.ContactEntry.COLUMN_SENDER_ADDRESS, contact.getPhoneNumber());
        values.put(ContactContract.ContactEntry.COLUMN_CONTACT_NAME, contact.getUserName());

        long id = db.insertWithOnConflict(ContactContract.ContactEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        if (id == -1) {
            Toast.makeText(context, "Data not saved\nPlease Try again", Toast.LENGTH_SHORT).show();
            return;
        }
        db.close();
    }
}
