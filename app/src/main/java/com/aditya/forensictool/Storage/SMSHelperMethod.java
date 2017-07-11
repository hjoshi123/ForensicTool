package com.aditya.forensictool.Storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.aditya.forensictool.Storage.SMSContract.SMSEntry;

/**
 * Created by HemantJ on 07/07/17.
 */

public class SMSHelperMethod extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "messages.db";

    private final static int DATABASE_VERSION = 1;

    public SMSHelperMethod(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + SMSEntry.TABLE_NAME + " ("
                + SMSEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SMSEntry.COLUMN_SENDER_ADDRESS + " TEXT NOT NULL, "
                + SMSEntry.COLUMN_MSG_BODY + " TEXT NOT NULL, "
                + SMSEntry.COLUMN_MSG_DATE + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
