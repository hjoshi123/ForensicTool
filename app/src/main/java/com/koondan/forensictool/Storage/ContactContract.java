package com.koondan.forensictool.Storage;

import android.provider.BaseColumns;

/**
 * Created by Koondan on 07/07/17.
 */

public final class ContactContract {

    public static final class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "contact";

        public static final String COLUMN_SENDER_ADDRESS = "phone_number";
        public static final String COLUMN_CONTACT_NAME = "contact_name";

    }
}
