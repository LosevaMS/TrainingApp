package Tables;

import android.provider.BaseColumns;

public class HistoryTable {
    private HistoryTable() {
    }
    public static final class HistoryEntry implements BaseColumns {
        public static final String TABLE_HISTORY = "history";
        public static final String HISTORY_PROG_ID = "prog_id";
       // public static final String HISTORY_EX_ID = "ex_id";
        public static final String HISTORY_PROG_NAME = "prog_name";
        public static final String HISTORY_DATE = "date";
    }
}
