package com.example.globusproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Tables.ApproachesTable.*;
import Tables.ExercisesTable.*;
import Tables.ProgramTable.*;

import static Tables.ApproachesTable.ApproachesEntry.APP_COUNT;
import static Tables.ApproachesTable.ApproachesEntry.APP_EX_ID;
import static Tables.ApproachesTable.ApproachesEntry.APP_WEIGHT;
import static Tables.ApproachesTable.ApproachesEntry.TABLE_APPROACHES;
import static Tables.ExercisesTable.ExercisesEntry.EX_NAME;
import static Tables.ExercisesTable.ExercisesEntry.EX_PROG_ID;
import static Tables.ExercisesTable.ExercisesEntry.TABLE_EXERCISES;
import static android.provider.UserDictionary.Words.APP_ID;

public class DBHelper  extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "training";



    /*public static final String TABLE_EXERCISES = "exercises";
    public static final String EX_ID = "_id";
    public static final String EX_NAME = "name";
    public static final String EX_PROG_ID = "_program_id";

    public static final String TABLE_APPROACHES = "approaches";
    public static final String APP_ID = "_id";
    public static final String APP_EX_ID = "_excercises_id";
    public static final String APP_WEIGHT = "weight";
    public static final String APP_COUNT = "count";*/

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       /* db.execSQL("create table " + ProgramEntry.TABLE_PROGRAMS + "(" +
                ProgramEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ProgramEntry.PROG_NAME + " text, "
                + ProgramEntry.COLUMN_AMOUNT + " INTEGER NOT NULL, " +
                ProgramEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");");*/
        final String SQL_CREATE_GROCERYLIST_TABLE = "CREATE TABLE " +
                ProgramEntry.TABLE_PROGRAMS + " (" +
                ProgramEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ProgramEntry.PROG_NAME + " TEXT NOT NULL " +

                ");";

        db.execSQL(SQL_CREATE_GROCERYLIST_TABLE);
        db.execSQL("create table " + TABLE_EXERCISES + "(" + ExercisesEntry._ID
                + " integer primary key AUTOINCREMENT," + EX_NAME + " text," + EX_PROG_ID + " integer,"
                + "foreign key("+ EX_PROG_ID+") references "+ ProgramEntry.TABLE_PROGRAMS+"("+ ProgramEntry._ID+")" + ")");

        db.execSQL("create table " + TABLE_APPROACHES + "(" + APP_ID
                + " integer primary key AUTOINCREMENT,"  + APP_WEIGHT + " integer," + APP_COUNT + " integer,"+ APP_EX_ID + " integer,"
                + "foreign key("+ APP_EX_ID+") references "+TABLE_EXERCISES+"("+ExercisesEntry._ID+")" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + ApproachesEntry.TABLE_APPROACHES);
        db.execSQL("drop table if exists " + ExercisesEntry.TABLE_EXERCISES);
        db.execSQL("drop table if exists " + ProgramEntry.TABLE_PROGRAMS);

        onCreate(db);

    }
}