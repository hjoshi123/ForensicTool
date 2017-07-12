package com.koondan.forensictool.Storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.koondan.forensictool.Storage.SMSContract.SMSEntry;

import java.io.File;

/**
 * Created by Koondan on 07/07/17.
 */

public class SMSHelperMethodInbox extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "inbox_sms.db";
    private final static String FILE_DIR = "Forensic";
    private final static int DATABASE_VERSION = 1;

    public SMSHelperMethodInbox(Context context){
        super(context, Environment.getExternalStorageDirectory()
                + File.separator + FILE_DIR
                + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + SMSEntry.TABLE_NAME + " ("
                + SMSEntry.COLUMN_SENDER_ADDRESS + " TEXT NOT NULL, "
                + SMSEntry.COLUMN_MSG_THREAD + " TEXT NOT NULL, "
                + SMSEntry.COLUMN_MSG_BODY + " TEXT NOT NULL, "
                + SMSEntry.COLUMN_MSG_DATE + " TEXT PRIMARY KEY NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
