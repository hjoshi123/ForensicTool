package com.koondan.forensictool.Storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.koondan.forensictool.Storage.ContactContract.ContactEntry;

import java.io.File;

/**
 * Created by Koondan on 07/07/17.
 */

public class ContactHelperMethod extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "contacts.db";
    private final static String FILE_DIR = "Forensic";
    private final static int DATABASE_VERSION = 1;

    public ContactHelperMethod(Context context){
        super(context, Environment.getExternalStorageDirectory()
                + File.separator + FILE_DIR
                + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + ContactEntry.TABLE_NAME + " ("
                + ContactEntry.COLUMN_SENDER_ADDRESS + " TEXT NOT NULL, "
                + ContactEntry.COLUMN_CONTACT_NAME + " TEXT PRIMARY KEY NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
