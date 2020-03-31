package Fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import Adapters.ExercisesAdapter;
import Adapters.HistoryTrainingAdapter;
import Tables.ExercisesTable;
import Tables.HistoryTable;

public class HistoryTrainingFragment extends Fragment implements HistoryTrainingAdapter.OnNoteListener{

    private SQLiteDatabase database;
    private HistoryTrainingAdapter historyTrainingAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_training,container,false);
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
        historyTrainingAdapter = new HistoryTrainingAdapter(requireContext(),getAllItems(arg1),this);
        recyclerView.setAdapter(historyTrainingAdapter);

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
    public void onNoteClick(int position) {
       /* final NavController navController = Navigation.findNavController(requireView());
        Bundle bundle = new Bundle();
        bundle.putLong("prog_id",getArguments().getLong("prog_id"));
        //bundle.putIntegerArrayList("ex_id",getArguments().getIntegerArrayList("ex_id"));
        bundle.putString("date",getArguments().getString("date"));
        navController.navigate(R.id.action_fragment_history_training_to_fragment_history_approach, bundle);*/
    }
}
