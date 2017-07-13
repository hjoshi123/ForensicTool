package com.koondan.forensictool.Storage;

import android.provider.BaseColumns;

/**
 * Created by Koondan on 07/07/17.
 */

public final class PhoneContract {

    public static final class PhoneEntry implements BaseColumns {
        public static final String TABLE_NAME = "call_logs";
        public static final String COLUMN_SENDER_ADDRESS = "phone_number";
        public static final String COLUMN_MSG_TYPE = "type";
        public static final String COLUMN_MSG_DATE = "date";
        public static final String COLUMN_MSG_DURATION = "duration";

    }
}
