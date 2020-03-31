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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import Adapters.HistoryApproachAdapter;
import Adapters.HistoryTrainingAdapter;
import Tables.ApproachesTable;
import Tables.ExercisesTable;

public class HistoryApproachFragment extends Fragment implements HistoryApproachAdapter.OnNoteListener{

    private SQLiteDatabase database;
    private HistoryApproachAdapter historyApproachAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_approach,container,false);
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
        final long arg1 = getArguments().getLong("ex_id");
        //final ArrayList<Integer> arg2 = getArguments().getIntegerArrayList("ex_id");
        final String arg2 = getArguments().getString("date");

        /*Toast toast = Toast.makeText(requireContext(),
                arg2+ " ", Toast.LENGTH_LONG);
        toast.show();*/

        final RecyclerView recyclerView = view.findViewById(R.id.history_approach_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyApproachAdapter = new HistoryApproachAdapter(requireContext(),getAllItems(arg1,arg2),this);
        recyclerView.setAdapter(historyApproachAdapter);

    }

    private Cursor getAllItems(long ex_id, String date) {

        String whereClause = ApproachesTable.ApproachesEntry.APP_EX_ID + "=? AND " +
                ApproachesTable.ApproachesEntry.APP_DATE + "=?";
        String[] whereArgs = new String[]{String.valueOf(ex_id), date};

        return database.query(
                ApproachesTable.ApproachesEntry.TABLE_APPROACHES,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
    }
    @Override
    public void onNoteClick(int position) {
       /* final NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_navigation_list_to_training);*/
    }
}