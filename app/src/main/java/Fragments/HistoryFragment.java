package Fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

import java.util.ArrayList;

import Adapters.HistoryListAdapter;
import Tables.ApproachesTable;
import Tables.HistoryApproachesTable;
import Tables.HistoryExercisesTable;
import Tables.HistoryTable;

public class HistoryFragment extends Fragment implements HistoryListAdapter.OnNoteListener {

    private SQLiteDatabase database;
    private HistoryListAdapter historyListAdapter;

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

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.VISIBLE);

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        final RecyclerView recyclerView = view.findViewById(R.id.recyclerview_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyListAdapter = new HistoryListAdapter(requireContext(), getAllItems(), this);
        recyclerView.setAdapter(historyListAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem((long) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);

        assert savedInstanceState != null;
        onSaveInstanceState(savedInstanceState);
    }


    private void removeItem(long id) {
        String date = searchDate(id);
        int prog_id = searchProgId(id);

        ArrayList<Integer> exIdArray = searchExId(prog_id);

        for (int i = 0; i<exIdArray.size(); i++){
            database.delete(HistoryApproachesTable.HistoryApproachesEntry.TABLE_HISTORY_APPROACHES,
                    HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_EX_ID + "=? and "
                            + HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_PROG_ID + "=? and "
                            + HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_DATE + "=?" ,
                    new String[]{exIdArray.get(i).toString(), String.valueOf(prog_id), date});
        }
        for (int i = 0; i<exIdArray.size(); i++){
            database.delete(HistoryExercisesTable.HistoryExercisesEntry.TABLE_HISTORY_EXERCISES,
                    HistoryExercisesTable.HistoryExercisesEntry._ID + "=" + exIdArray.get(i), null);
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

    public int searchProgId(long id) {
        String query = "select prog_id from " + HistoryTable.HistoryEntry.TABLE_HISTORY + " WHERE _id = " + id;
        Cursor c = database.rawQuery(query, null);

        int a = 0;
        if (c.moveToFirst())
        {
            a = c.getInt(c.getColumnIndex("prog_id"));
        }
        c.close();
        return a;
    }

    public String searchDate(long id) {
        String query = "select date from " + HistoryTable.HistoryEntry.TABLE_HISTORY + " WHERE _id = " + id;
        Cursor c = database.rawQuery(query, null);

        String a = " ";
        if (c.moveToFirst())
        {
            a = c.getString(c.getColumnIndex("date"));
        }
        c.close();
        return a;
    }

    public ArrayList<Integer> searchExId(int id) {
        String query = "select _id from " + HistoryExercisesTable.HistoryExercisesEntry.TABLE_HISTORY_EXERCISES + " WHERE prog_id = " + id;
        Cursor c = database.rawQuery(query, null);

        ArrayList a = new ArrayList();
        while (c.moveToNext())
        {
            a.add(c.getInt(c.getColumnIndex("_id")));
        }
        c.close();
        return a;
    }

    @Override
    public void onNoteClick(int position) {
    }

}