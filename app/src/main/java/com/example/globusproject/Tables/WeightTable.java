package com.example.globusproject.Tables;

import android.provider.BaseColumns;

public class WeightTable {
    private WeightTable() {
    }

    public static final class WeightEntry implements BaseColumns {
        public static final String TABLE_WEIGHT = "table_weight";
        public static final String WEIGHT = "weight";
        public static final String DATE = "date";

    }
}
