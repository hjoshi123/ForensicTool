package com.koondan.forensictool.Storage;

import android.provider.BaseColumns;

/**
 * Created by Koondan on 07/07/17.
 */

public final class SMSContract {

    public static final class SMSEntry implements BaseColumns {
        public static final String TABLE_NAME = "sms";

        public static final String COLUMN_SENDER_ADDRESS = "phone_number";
        public static final String COLUMN_MSG_BODY = "body";
        public static final String COLUMN_MSG_DATE = "date";
        public static final String COLUMN_MSG_THREAD = "thread_id";

    }
}
