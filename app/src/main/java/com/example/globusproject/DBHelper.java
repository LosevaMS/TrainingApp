package com.example.globusproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBHelper  extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "training";

    public static final class ProgramEntry implements BaseColumns{
    public static final String TABLE_PROGRAMS = "programs";
    public static final String PROG_ID = "_id";
    public static final String PROG_NAME = "name";
    }

    public static final String TABLE_EXERCISES = "exercises";
    public static final String EX_ID = "_id";
    public static final String EX_NAME = "name";
    public static final String EX_PROG_ID = "_program_id";

    public static final String TABLE_APPROACHES = "approaches";
    public static final String APP_ID = "_id";
    public static final String APP_EX_ID = "_excercises_id";
    public static final String APP_WEIGHT = "weight";
    public static final String APP_COUNT = "count";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ProgramEntry.TABLE_PROGRAMS + "(" + ProgramEntry.PROG_ID
                + " integer primary key AUTOINCREMENT," + ProgramEntry.PROG_NAME + " text" + ")");

        db.execSQL("create table " + TABLE_EXERCISES + "(" + EX_ID
                + " integer primary key," + EX_NAME + " text," + EX_PROG_ID + " integer,"
                + "foreign key("+ EX_PROG_ID+") references "+ ProgramEntry.TABLE_PROGRAMS+"("+ ProgramEntry.PROG_ID+")" + ")");

        db.execSQL("create table " + TABLE_APPROACHES + "(" + APP_ID
                + " integer primary key,"  + APP_WEIGHT + " integer," + APP_COUNT + " integer,"+ APP_EX_ID + " integer,"
                + "foreign key("+ APP_EX_ID+") references "+TABLE_EXERCISES+"("+EX_ID+")" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_APPROACHES);
        db.execSQL("drop table if exists " + TABLE_EXERCISES);
        db.execSQL("drop table if exists " + ProgramEntry.TABLE_PROGRAMS);

        onCreate(db);

    }
}