package Tables;

import android.provider.BaseColumns;

public class ProgramTable {
    private ProgramTable() {
    }

    public static final class ProgramEntry implements BaseColumns {
        public static final String TABLE_PROGRAMS = "programs";
        public static final String PROG_NAME = "name";
        public static final String PROG_URI = "uri";

    }
}
