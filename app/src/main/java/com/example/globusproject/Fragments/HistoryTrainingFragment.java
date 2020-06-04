package com.example.globusproject.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;
import com.example.globusproject.Tables.ApproachesTable;
import com.example.globusproject.Tables.HistoryApproachesTable;
import com.example.globusproject.Tables.HistoryTable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.globusproject.Adapters.HistoryTrainingAdapter;
import com.example.globusproject.Tables.HistoryExercisesTable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryTrainingFragment extends Fragment implements HistoryTrainingAdapter.ClickListener {

    private SQLiteDatabase database;
    private String date;
    private String condition;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_training, container, false);
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
        long program_id = getArguments().getLong("prog_id");
        date = getArguments().getString("date");

        condition = searchIds(date, program_id);

        if (condition != null) {
            RecyclerView recyclerView = view.findViewById(R.id.history_training_recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            HistoryTrainingAdapter historyTrainingAdapter = new HistoryTrainingAdapter(requireContext(), getAllItems(), this);
            recyclerView.setAdapter(historyTrainingAdapter);

            historyTrainingAdapter.setOnItemClickListener(new HistoryTrainingAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Bundle bundle = new Bundle();
                    long exercise_id = (long) v.getTag();
                    bundle.putLong("ex_id", exercise_id);
                    bundle.putString("date", date);
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_fragment_history_training_to_fragment_history_approach, bundle);
                }
            });
        }

        androidx.appcompat.widget.Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireView());
                if (!navController.popBackStack()) {
                    navController.navigate(R.id.action_fragment_history_training_to_navigation_history);
                }
            }
        });
    }

    private Cursor getAllItems() {

        return database.rawQuery("SELECT * FROM history_exercises WHERE _id IN (" + condition + ")", null);

    }

    private String searchIds(String date, long program_id) {

        String whereClause = HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_DATE + "=? AND " +
                HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_PROG_ID + "=?";

        String[] whereArgs = new String[]{date, String.valueOf(program_id)};

        Cursor cursor = database.query(
                HistoryApproachesTable.HistoryApproachesEntry.TABLE_HISTORY_APPROACHES,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                HistoryTable.HistoryEntry._ID + " DESC"
        );
        String idString = "";
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        while (cursor.moveToNext()) {
            idString = idString.concat(String.valueOf(cursor.getInt(cursor.getColumnIndex("_excercise_id"))));
            idString = idString.concat(",");
        }
        idString = idString.substring(0, idString.length() - 1);

        cursor.close();
        return idString;
    }

    @Override
    public void onItemClick(int position, View v) {
    }
}
