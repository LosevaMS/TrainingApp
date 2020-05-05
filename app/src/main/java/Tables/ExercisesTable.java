package Tables;

import android.provider.BaseColumns;

public class ExercisesTable {
    private ExercisesTable() {
    }

    public static final class ExercisesEntry implements BaseColumns {
        public static final String TABLE_EXERCISES = "exercises";
        public static final String EX_NAME = "name";
        public static final String EX_PROG_ID = "_program_id";
        public static final String EX_URI = "uri";
    }
}