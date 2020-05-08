package Tables;

import android.provider.BaseColumns;

public class HistoryApproachesTable {
    private HistoryApproachesTable() {
    }

    public static final class HistoryApproachesEntry implements BaseColumns {
        public static final String TABLE_HISTORY_APPROACHES = "history_approaches";
        public static final String HISTORY_APP_EX_ID = "_excercise_id";
        public static final String HISTORY_APP_WEIGHT = "weight";
        public static final String HISTORY_APP_COUNT = "count";
        public static final String HISTORY_APP_PROG_ID = "_program_id";
        public static final String HISTORY_APP_DATE = "date";

    }
}
