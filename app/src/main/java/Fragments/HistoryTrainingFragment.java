package Fragments;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import Adapters.HistoryTrainingAdapter;
import Tables.ExercisesTable;

public class HistoryTrainingFragment extends Fragment implements HistoryTrainingAdapter.ClickListener {

    private SQLiteDatabase database;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_training, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        assert getArguments() != null;
        final long arg1 = getArguments().getLong("prog_id");
        final String arg2 = getArguments().getString("date");

        final RecyclerView recyclerView = view.findViewById(R.id.history_training_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        HistoryTrainingAdapter historyTrainingAdapter = new HistoryTrainingAdapter(requireContext(), getAllItems(arg1),this);
        recyclerView.setAdapter(historyTrainingAdapter);

       /* recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(requireContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );*/

        historyTrainingAdapter.setOnItemClickListener(new HistoryTrainingAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Bundle bundle = new Bundle();
                long exId = (long) v.getTag();
                bundle.putLong("ex_id", exId);
                bundle.putString("date", arg2);

                final NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_fragment_history_training_to_fragment_history_approach, bundle);
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

    @Override
    public void onItemClick(int position, View v) {
    }
}
