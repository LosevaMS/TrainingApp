package com.example.globusproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.globusproject.Tables.ApproachesTable.*;
import com.example.globusproject.Tables.ExercisesTable.*;
import com.example.globusproject.Tables.HistoryApproachesTable.*;
import com.example.globusproject.Tables.HistoryExercisesTable.*;
import com.example.globusproject.Tables.HistoryTable.*;
import com.example.globusproject.Tables.ProgramTable.*;
import com.example.globusproject.Tables.WeightTable.*;

import static com.example.globusproject.Tables.ApproachesTable.ApproachesEntry.APP_COUNT;
import static com.example.globusproject.Tables.ApproachesTable.ApproachesEntry.APP_EX_ID;
import static com.example.globusproject.Tables.ApproachesTable.ApproachesEntry.APP_IS_CURRENT;
import static com.example.globusproject.Tables.ApproachesTable.ApproachesEntry.APP_PROG_ID;
import static com.example.globusproject.Tables.ApproachesTable.ApproachesEntry.APP_DATE;
import static com.example.globusproject.Tables.ApproachesTable.ApproachesEntry.APP_WEIGHT;
import static com.example.globusproject.Tables.ApproachesTable.ApproachesEntry.TABLE_APPROACHES;
import static com.example.globusproject.Tables.ExercisesTable.ExercisesEntry.EX_NAME;
import static com.example.globusproject.Tables.ExercisesTable.ExercisesEntry.EX_PROG_ID;
import static com.example.globusproject.Tables.ExercisesTable.ExercisesEntry.EX_URI;
import static com.example.globusproject.Tables.ExercisesTable.ExercisesEntry.TABLE_EXERCISES;
import static com.example.globusproject.Tables.HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_COUNT;
import static com.example.globusproject.Tables.HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_DATE;
import static com.example.globusproject.Tables.HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_EX_ID;
import static com.example.globusproject.Tables.HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_PROG_ID;
import static com.example.globusproject.Tables.HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_WEIGHT;
import static com.example.globusproject.Tables.HistoryApproachesTable.HistoryApproachesEntry.TABLE_HISTORY_APPROACHES;
import static com.example.globusproject.Tables.HistoryExercisesTable.HistoryExercisesEntry.HISTORY_EX_NAME;
import static com.example.globusproject.Tables.HistoryExercisesTable.HistoryExercisesEntry.HISTORY_EX_URI;
import static com.example.globusproject.Tables.HistoryExercisesTable.HistoryExercisesEntry.TABLE_HISTORY_EXERCISES;
import static com.example.globusproject.Tables.HistoryTable.HistoryEntry.HISTORY_DATE;
import static com.example.globusproject.Tables.HistoryTable.HistoryEntry.HISTORY_PROG_ID;
import static com.example.globusproject.Tables.HistoryTable.HistoryEntry.HISTORY_PROG_NAME;
import static com.example.globusproject.Tables.HistoryTable.HistoryEntry.HISTORY_TIME;
import static com.example.globusproject.Tables.HistoryTable.HistoryEntry.HISTORY_URI;
import static com.example.globusproject.Tables.HistoryTable.HistoryEntry.TABLE_HISTORY;
import static com.example.globusproject.Tables.ProgramTable.ProgramEntry.PROG_URI;
import static com.example.globusproject.Tables.ProgramTable.ProgramEntry.TABLE_PROGRAMS;
import static com.example.globusproject.Tables.WeightTable.WeightEntry.DATE;
import static com.example.globusproject.Tables.WeightTable.WeightEntry.TABLE_WEIGHT;
import static com.example.globusproject.Tables.WeightTable.WeightEntry.WEIGHT;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME = "training";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_GROCERYLIST_TABLE = "CREATE TABLE " +
                TABLE_PROGRAMS + " (" +
                ProgramEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ProgramEntry.PROG_NAME + " TEXT NOT NULL ," + PROG_URI + " text" + ");";

        db.execSQL(SQL_CREATE_GROCERYLIST_TABLE);

        db.execSQL("create table " + TABLE_EXERCISES + "(" + ExercisesEntry._ID
                + " integer primary key AUTOINCREMENT," + EX_NAME + " text," + EX_PROG_ID + " integer," + EX_URI + " text,"
                + "foreign key(" + EX_PROG_ID + ") references " + TABLE_PROGRAMS + "(" + ProgramEntry._ID + ")" + ")");

        db.execSQL("create table " + TABLE_APPROACHES + "(" + ApproachesEntry._ID
                + " integer primary key AUTOINCREMENT," + APP_WEIGHT + " double," + APP_COUNT + " integer," + APP_EX_ID + " integer,"
                + APP_PROG_ID + " integer," + APP_DATE + " text," + APP_IS_CURRENT + " boolean,"
                + "foreign key(" + APP_EX_ID + ") references " + TABLE_EXERCISES + "(" + ExercisesEntry._ID + ")," +
                "foreign key(" + APP_PROG_ID + ") references " + TABLE_PROGRAMS + "(" + ProgramEntry._ID + ")" + ")");

        db.execSQL("create table " + TABLE_HISTORY + "(" + HistoryEntry._ID + " integer primary key AUTOINCREMENT,"
                + HISTORY_PROG_ID + " integer," + HISTORY_PROG_NAME + " text," + HISTORY_DATE + " text," + HISTORY_TIME
                + " integer, " + HISTORY_URI + " text" + ")");

        db.execSQL("create table " + TABLE_HISTORY_EXERCISES + "(" + HistoryExercisesEntry._ID + " integer primary key AUTOINCREMENT,"
                + HISTORY_EX_NAME + " text," + HistoryExercisesEntry.HISTORY_PROG_ID + " integer," + HISTORY_EX_URI + " text,"
                + "foreign key(" + HistoryExercisesEntry.HISTORY_PROG_ID + ") references "
                + TABLE_HISTORY + "(" + HISTORY_PROG_ID + ")" + ")");

        db.execSQL("create table " + TABLE_HISTORY_APPROACHES + "(" + HistoryApproachesEntry._ID
                + " integer primary key AUTOINCREMENT," + HISTORY_APP_WEIGHT + " double," + HISTORY_APP_COUNT + " integer,"
                + HISTORY_APP_EX_ID + " integer," + HISTORY_APP_PROG_ID + " integer," + HISTORY_APP_DATE + " text,"
                + "foreign key(" + HISTORY_APP_EX_ID + ") references " + TABLE_HISTORY_EXERCISES + "(" + HistoryExercisesEntry._ID + "),"
                + "foreign key(" + HISTORY_APP_PROG_ID + ") references " + TABLE_HISTORY + "(" + HISTORY_PROG_ID + ")" + ")");

        db.execSQL("create table " + TABLE_WEIGHT + "(" + WeightEntry._ID
                + " integer primary key AUTOINCREMENT," + WEIGHT + " float," + DATE + " text" + ")");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + ApproachesEntry.TABLE_APPROACHES);
        db.execSQL("drop table if exists " + ExercisesEntry.TABLE_EXERCISES);
        db.execSQL("drop table if exists " + TABLE_PROGRAMS);
        db.execSQL("drop table if exists " + HistoryExercisesEntry.TABLE_HISTORY_EXERCISES);
        db.execSQL("drop table if exists " + HistoryApproachesEntry.TABLE_HISTORY_APPROACHES);
        db.execSQL("drop table if exists " + TABLE_HISTORY);
        db.execSQL("drop table if exists " + TABLE_WEIGHT);

        onCreate(db);

    }
}