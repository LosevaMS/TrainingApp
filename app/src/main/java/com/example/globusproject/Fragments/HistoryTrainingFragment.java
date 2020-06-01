package com.example.globusproject.Fragments;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.globusproject.Adapters.HistoryTrainingAdapter;
import com.example.globusproject.Tables.HistoryExercisesTable;

public class HistoryTrainingFragment extends Fragment implements HistoryTrainingAdapter.ClickListener {

    private SQLiteDatabase database;
    private String date;

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

        RecyclerView recyclerView = view.findViewById(R.id.history_training_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        HistoryTrainingAdapter historyTrainingAdapter = new HistoryTrainingAdapter(requireContext(), getAllItems(program_id), this);
        recyclerView.setAdapter(historyTrainingAdapter);


        historyTrainingAdapter.setOnItemClickListener(new HistoryTrainingAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Bundle bundle = new Bundle();
                long exercise_id = (long) v.getTag();
                bundle.putLong("ex_id", exercise_id);
                bundle.putString("date", date);

                final NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_fragment_history_training_to_fragment_history_approach, bundle);
            }
        });

        androidx.appcompat.widget.Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = Navigation.findNavController(requireView());
                if (!navController.popBackStack()) {
                    navController.navigate(R.id.action_fragment_history_training_to_navigation_history);
                }
            }
        });
    }

    private Cursor getAllItems(long id) {

        String whereClause = HistoryExercisesTable.HistoryExercisesEntry.HISTORY_PROG_ID + "=?";

        String[] whereArgs = new String[]{String.valueOf(id)};

        return database.query(
                HistoryExercisesTable.HistoryExercisesEntry.TABLE_HISTORY_EXERCISES,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
    }

    @Override
    public void onItemClick(int position, View v) {
    }
}
