package Fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.Date;

import Adapters.ExercisesAdapter;
import Tables.ExercisesTable;
import Tables.HistoryTable;

public class TrainingFragment extends Fragment {

    private SQLiteDatabase database;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_training, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        TextView finishText = view.findViewById(R.id.finish_training);

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        final Date date = new Date();

        assert getArguments() != null;
        final long arg1 = getArguments().getLong("prog_id");
        final String arg2 = getArguments().getString("prog_name");

        final RecyclerView recyclerView2 = view.findViewById(R.id.exercises_recyclerview);
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext()));
        ExercisesAdapter exercisesAdapter = new ExercisesAdapter(requireContext(), getAllItems(arg1));
        recyclerView2.setAdapter(exercisesAdapter);

        finishText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues cv = new ContentValues();
                cv.put(HistoryTable.HistoryEntry.HISTORY_PROG_ID, arg1);
                cv.put(HistoryTable.HistoryEntry.HISTORY_PROG_NAME, arg2);
                cv.put(HistoryTable.HistoryEntry.HISTORY_DATE, formatter.format(date));

                database.insert(HistoryTable.HistoryEntry.TABLE_HISTORY, null, cv);

                final NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_training_to_navigation_list);
            }
        });


    }

    private Cursor getAllItems(long id) {
        return database.query(
                ExercisesTable.ExercisesEntry.TABLE_EXERCISES,
                null,
                ExercisesTable.ExercisesEntry.EX_PROG_ID + "=" + id,
                null,
                null,
                null,
                null
        );
    }
}
