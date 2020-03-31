package Fragments;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Adapters.HistoryListAdapter;
import Adapters.ProgramListAdapter;
import Tables.ApproachesTable;
import Tables.HistoryTable;
import Tables.ProgramTable;

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
        return inflater.inflate(R.layout.fragment_history,container,false);
    }
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.VISIBLE);

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        final RecyclerView recyclerView=view.findViewById(R.id.recyclerview_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyListAdapter = new HistoryListAdapter(requireContext(),getAllItems(),this);
        recyclerView.setAdapter(historyListAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem((long) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);


        onSaveInstanceState(savedInstanceState);
    }


    private void removeItem(long id) {
        database.delete( HistoryTable.HistoryEntry.TABLE_HISTORY,
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
                HistoryTable.HistoryEntry._ID+" DESC"
        );
    }

    @Override
    public void onNoteClick(int position) {
        /*final NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_navigation_list_to_edit_training);*/

    }

}