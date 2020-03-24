package Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import Adapters.ApproachAdapter;
import Adapters.ExercisesAdapter;
import Tables.ApproachesTable;
import Tables.ExercisesTable;

public class ApproachFragment extends Fragment implements ApproachAdapter.OnNoteListener{

    private SQLiteDatabase database;
    private ApproachAdapter approachAdapter;
    private TextView finishText;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_approach,container,false);
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
        final String arg2 = getArguments().getString("ex_name");

        final RecyclerView recyclerView1 = view.findViewById(R.id.last_approach_recyclerview);
        recyclerView1.setLayoutManager(new LinearLayoutManager(requireContext()));
        approachAdapter = new ApproachAdapter(requireContext(),getAllItems(arg1),this);
        recyclerView1.setAdapter(approachAdapter);

        final RecyclerView recyclerView2 = view.findViewById(R.id.approach_recyclerview);
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext()));
        approachAdapter = new ApproachAdapter(requireContext(),getAllItems(arg1),this);
        recyclerView2.setAdapter(approachAdapter);

        /*Toast toast = Toast.makeText(requireContext(),
                arg1 + arg2, Toast.LENGTH_LONG);
        toast.show();*/

    }
    private Cursor getAllItems(long id) {
        return database.query(
                ApproachesTable.ApproachesEntry.TABLE_APPROACHES,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
    @Override
    public void onNoteClick(int position) {

    }
}
