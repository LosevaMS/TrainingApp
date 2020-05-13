package Fragments;

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


import Adapters.HistoryApproachAdapter;
import Tables.ApproachesTable;
import Tables.HistoryApproachesTable;

public class HistoryApproachFragment extends Fragment {

    private SQLiteDatabase database;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_approach, container, false);
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
        final String arg2 = getArguments().getString("date");


        final RecyclerView recyclerView = view.findViewById(R.id.history_approach_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        HistoryApproachAdapter historyApproachAdapter = new HistoryApproachAdapter(requireContext(), getAllItems(arg1, arg2));
        recyclerView.setAdapter(historyApproachAdapter);


        androidx.appcompat.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = Navigation.findNavController(getView());
                if (!navController.popBackStack()) {
                    navController.navigate(R.id.action_fragment_history_approach_to_fragment_history_training);
                }
            }
        });

    }

    private Cursor getAllItems(long ex_id, String date) {

        String whereClause = HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_EX_ID + "=? AND " +
                HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_DATE + "=?";
        String[] whereArgs = new String[]{String.valueOf(ex_id), date};

        return database.query(
                HistoryApproachesTable.HistoryApproachesEntry.TABLE_HISTORY_APPROACHES,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
    }
}