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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import com.example.globusproject.Adapters.HistoryListAdapter;
import com.example.globusproject.Tables.HistoryApproachesTable;
import com.example.globusproject.Tables.HistoryExercisesTable;
import com.example.globusproject.Tables.HistoryTable;

public class HistoryFragment extends Fragment {

    private SQLiteDatabase database;
    private HistoryListAdapter historyListAdapter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);

        BottomNavigationView navBar = requireActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.VISIBLE);

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        recyclerView = view.findViewById(R.id.recyclerview_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyListAdapter = new HistoryListAdapter(requireContext(), getAllItems());
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
                                        historyListAdapter = new HistoryListAdapter(requireContext(), getAllItems());
                                        recyclerView.setAdapter(historyListAdapter);
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
            }
        }).attachToRecyclerView(recyclerView);

        assert savedInstanceState != null;
        onSaveInstanceState(savedInstanceState);
    }


    private void removeItem(long id) {
        String date = searchDate(id);
        int program_id = searchProgId(id);

        ArrayList<Integer> exerciseIdArray = searchExId(program_id);

        for (int i = 0; i < exerciseIdArray.size(); i++) {
            database.delete(HistoryApproachesTable.HistoryApproachesEntry.TABLE_HISTORY_APPROACHES,
                    HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_EX_ID + "=? and "
                            + HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_PROG_ID + "=? and "
                            + HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_DATE + "=?",
                    new String[]{exerciseIdArray.get(i).toString(), String.valueOf(program_id), date});
        }

        database.delete(HistoryTable.HistoryEntry.TABLE_HISTORY,
                HistoryTable.HistoryEntry._ID + "=" + id, null);
        historyListAdapter.swapCursor(getAllItems());
    }

    private Cursor getAllItems() {
        return database.query(
                HistoryTable.HistoryEntry.TABLE_HISTORY,
                null,
                null,
                null,
                null,
                null,
                HistoryTable.HistoryEntry._ID + " DESC"
        );
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