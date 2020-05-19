package Tables;

import android.provider.BaseColumns;

public class HistoryExercisesTable {
    private HistoryExercisesTable() {
    }

    public static final class HistoryExercisesEntry implements BaseColumns {
        public static final String TABLE_HISTORY_EXERCISES = "history_exercises";
        public static final String HISTORY_PROG_ID = "prog_id";
        public static final String HISTORY_EX_NAME = "ex_name";
        public static final String HISTORY_EX_URI = "ex_uri";
        //public static final String HISTORY_EX_DATE = "ex_date";
    }
}
