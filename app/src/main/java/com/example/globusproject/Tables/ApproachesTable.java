package com.example.globusproject.Tables;

import android.provider.BaseColumns;

public class ApproachesTable {
    private ApproachesTable() {
    }

    public static final class ApproachesEntry implements BaseColumns {
        public static final String TABLE_APPROACHES = "approaches";
        public static final String APP_EX_ID = "_excercises_id";
        public static final String APP_WEIGHT = "weight";
        public static final String APP_COUNT = "count";
        public static final String APP_PROG_ID = "_program_id";
        public static final String APP_DATE = "date";
        public static final String APP_IS_CURRENT = "is_current";

    }
}
