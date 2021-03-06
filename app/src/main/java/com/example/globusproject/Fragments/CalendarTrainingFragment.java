package com.example.globusproject.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.example.globusproject.Adapters.HistoryCalendarAdapter;
import com.example.globusproject.Tables.HistoryApproachesTable;
import com.example.globusproject.Tables.HistoryExercisesTable;
import com.example.globusproject.Tables.HistoryTable;

public class CalendarTrainingFragment extends Fragment {

    private SQLiteDatabase database;
    private HistoryCalendarAdapter historyListAdapter;
    private String condition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_calendar_training, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);

        BottomNavigationView navBar = requireActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        assert getArguments() != null;
        final String date = getArguments().getString("date");

        try {
            condition = searchIds(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final RecyclerView recyclerView = view.findViewById(R.id.recyclerview_calendar_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyListAdapter = new HistoryCalendarAdapter(requireContext(), getAllItems());
        recyclerView.setAdapter(historyListAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NotNull final RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(requireContext());
                mDialogBuilder
                        .setMessage("Удалить историю?")
                        .setCancelable(false)
                        .setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        removeItem((long) viewHolder.itemView.getTag());
                                    }
                                })
                        .setNegativeButton("Нет",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        historyListAdapter = new HistoryCalendarAdapter(requireContext(), getAllItems());
                                        recyclerView.setAdapter(historyListAdapter);
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
            }
        }).attachToRecyclerView(recyclerView);

        androidx.appcompat.widget.Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireView());
                if (!navController.popBackStack()) {
                    navController.navigate(R.id.action_calendarTrainingFragment_to_navigation_profile);
                }
            }
        });

        assert savedInstanceState != null;
        onSaveInstanceState(savedInstanceState);
    }

    private void removeItem(long id) {
        String date = searchDate(id);
        int program_id = searchProgId(id);

        ArrayList<Integer> exIdArray = searchExId(program_id);

        for (int i = 0; i < exIdArray.size(); i++) {
            database.delete(HistoryApproachesTable.HistoryApproachesEntry.TABLE_HISTORY_APPROACHES,
                    HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_EX_ID + "=? and "
                            + HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_PROG_ID + "=? and "
                            + HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_DATE + "=?",
                    new String[]{exIdArray.get(i).toString(), String.valueOf(program_id), date});
        }

        database.delete(HistoryTable.HistoryEntry.TABLE_HISTORY,
                HistoryTable.HistoryEntry._ID + "=" + id, null);
        historyListAdapter.swapCursor(getAllItems());
    }

    private Cursor getAllItems() {

        if (condition.isEmpty()) {

            return database.query(
                    HistoryTable.HistoryEntry.TABLE_HISTORY,
                    null,
                    null,
                    null,
                    null,
                    null,
                    HistoryTable.HistoryEntry._ID + " DESC"
            );
        } else {
            return database.rawQuery("SELECT * FROM history WHERE _id IN (" + condition + ")", null);
        }
    }

    private String searchIds(String date) throws ParseException {
        Cursor cursor = database.query(
                HistoryTable.HistoryEntry.TABLE_HISTORY,
                null,
                null,
                null,
                null,
                null,
                HistoryTable.HistoryEntry._ID + " DESC"
        );
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String idString = "";

        while (cursor.moveToNext()) {
            Date date1 = dateFormat.parse(cursor.getString(cursor.getColumnIndex("date")));
            assert date1 != null;
            if (date.equals(dateFormat.format(date1))) {
                idString = idString.concat(String.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))));
                idString = idString.concat(",");
            }
        }
        idString = idString.substring(0, idString.length() - 1);

        cursor.close();
        return idString;
    }

    private int searchProgId(long id) {
        String query = "select prog_id from " + HistoryTable.HistoryEntry.TABLE_HISTORY + " WHERE _id = " + id;
        Cursor cursor = database.rawQuery(query, null);

        int program_id = 0;
        if (cursor.moveToFirst()) {
            program_id = cursor.getInt(cursor.getColumnIndex("prog_id"));
        }
        cursor.close();
        return program_id;
    }

    private String searchDate(long id) {
        String query = "select date from " + HistoryTable.HistoryEntry.TABLE_HISTORY + " WHERE _id = " + id;
        Cursor cursor = database.rawQuery(query, null);

        String date = " ";
        if (cursor.moveToFirst()) {
            date = cursor.getString(cursor.getColumnIndex("date"));
        }
        cursor.close();
        return date;
    }

    private ArrayList<Integer> searchExId(int id) {
        String query = "select _id from " + HistoryExercisesTable.HistoryExercisesEntry.TABLE_HISTORY_EXERCISES + " WHERE prog_id = " + id;
        Cursor cursor = database.rawQuery(query, null);

        ArrayList<Integer> id_array = new ArrayList<>();
        while (cursor.moveToNext()) {
            id_array.add(cursor.getInt(cursor.getColumnIndex("_id")));
        }
        cursor.close();
        return id_array;
    }
}