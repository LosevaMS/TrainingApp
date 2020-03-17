package com.example.globusproject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import Tables.ProgramTable;


public class ListFragment extends Fragment implements ProgramListAdapter.OnNoteListener {

    private Button add_program_button;
    private EditText userInput;
    private SQLiteDatabase database;
    private ProgramListAdapter programListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_list,container,false);
    }
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.VISIBLE);

        /*final NavController navController = Navigation.findNavController(view);
        TextView ex1 = view.findViewById(R.id.ex1);
        ex1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_navigation_list_to_training);

            }
        });
*/
        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        final RecyclerView recyclerView=view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        programListAdapter = new ProgramListAdapter(requireContext(),getAllItems(),this);
        recyclerView.setAdapter(programListAdapter);

        add_program_button = view.findViewById(R.id.add_btn);

        userInput = view.findViewById(R.id.input_text);

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




        add_program_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                LayoutInflater li = LayoutInflater.from(requireContext());
                View promptsView = li.inflate(R.layout.dialog_add_program, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(requireContext());

                mDialogBuilder.setView(promptsView);

                userInput = promptsView.findViewById(R.id.input_text);

                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                       addItem();
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
                userInput.getText().clear();
            }
        });


        onSaveInstanceState(savedInstanceState);
    }


    private void addItem() {

        if (userInput.getText().toString().trim().length() == 0 ) {
            return;
        }

        String name = userInput.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(ProgramTable.ProgramEntry.PROG_NAME, name);

        database.insert(ProgramTable.ProgramEntry.TABLE_PROGRAMS, null, cv);
        programListAdapter.swapCursor(getAllItems());

        userInput.getText().clear();
    }

    private void removeItem(long id) {
        database.delete(ProgramTable.ProgramEntry.TABLE_PROGRAMS,
                ProgramTable.ProgramEntry._ID + "=" + id, null);
        programListAdapter.swapCursor(getAllItems());
    }

    private Cursor getAllItems() {
        return database.query(
                ProgramTable.ProgramEntry.TABLE_PROGRAMS,
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
        final NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_navigation_list_to_training);

    }

}