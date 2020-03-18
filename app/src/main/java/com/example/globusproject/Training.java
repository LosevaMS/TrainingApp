package com.example.globusproject;

import android.app.ActionBar;
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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import Tables.ExercisesTable;
import Tables.ProgramTable;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;

public class Training extends Fragment implements ExerciseListAdapter.OnNoteListener{

    private Button add_exercise_button;
    private TextView delete_program_button;
    private EditText userInput, userInput2;
    private Button okBtn, deleteBtn;
    private SQLiteDatabase database;
    private ExerciseListAdapter exerciseListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training,container,false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      /*  BottomNavigationView menuView = view.findViewById(R.id.nav_host_fragment);
        menuView.findViewById(R.id.action_navigation_list_to_training).setVisibility(View.GONE);*/
      /*BottomNavigationView bottomNavigation = view.findViewById(R.id.nav_view);
        bottomNavigation.getMenu().removeItem(R.id.nav_host_fragment);*/

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(GONE);


        userInput = view.findViewById(R.id.edit_exercise_name);
        add_exercise_button = view.findViewById(R.id.add_exercise_btn);
        okBtn = view.findViewById(R.id.ok_btn);
        deleteBtn = view.findViewById(R.id.delete_ex_item);

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        final long arg1 = getArguments().getLong("prog_id");
        final String arg2 = getArguments().getString("prog_name");

       /* Toast toast = Toast.makeText(requireContext(),String.valueOf(arg1),Toast.LENGTH_LONG);
        toast.show();
        Toast toast2 = Toast.makeText(requireContext(),String.valueOf(arg2),Toast.LENGTH_LONG);
        toast2.show();*/

        final RecyclerView recyclerView2 = view.findViewById(R.id.recyclerview2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext()));
        exerciseListAdapter = new ExerciseListAdapter(requireContext(),getAllItems(arg1),this);
        recyclerView2.setAdapter(exerciseListAdapter);


        userInput.setText(arg2);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userInput.getText().toString();
                ContentValues cv = new ContentValues();
                cv.put(ProgramTable.ProgramEntry.PROG_NAME, name);
                database.update(ProgramTable.ProgramEntry.TABLE_PROGRAMS, cv, "name = ?", new String[]{arg2});
                exerciseListAdapter.swapCursor(getAllItems(arg1));
                final NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_training_to_navigation_list);
            }
        });


        ContentValues cv = new ContentValues();
        //cv.put(ExercisesTable.ExercisesEntry.EX_NAME, "Жим лежа");
        //cv.put(ExercisesTable.ExercisesEntry.EX_PROG_ID,(int)arg1);
        //cv.put(ExercisesTable.ExercisesEntry.EX_NAME, "Бабочка");
        //cv.put(ExercisesTable.ExercisesEntry.EX_PROG_ID,2);

        //database.insert(ExercisesTable.ExercisesEntry.TABLE_EXERCISES, null, cv);
        //exerciseListAdapter.swapCursor(getAllItems());

        add_exercise_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                LayoutInflater li = LayoutInflater.from(requireContext());
                View promptsView = li.inflate(R.layout.dialog_add_program, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(requireContext());

                mDialogBuilder.setView(promptsView);

                userInput2 = promptsView.findViewById(R.id.input_text);

                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        addItem(arg1);
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
                userInput2.getText().clear();
            }
        });

    }

    private void addItem(long id) {

        if (userInput2.getText().toString().trim().length() == 0 ) {
            return;
        }

        String name = userInput2.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(ExercisesTable.ExercisesEntry.EX_NAME, name);
        cv.put(ExercisesTable.ExercisesEntry.EX_PROG_ID,(int)id);

        database.insert(ExercisesTable.ExercisesEntry.TABLE_EXERCISES, null, cv);
        exerciseListAdapter.swapCursor(getAllItems(id));

        userInput2.getText().clear();
    }

    private void removeItem(long id) {
        database.delete(ExercisesTable.ExercisesEntry.TABLE_EXERCISES,
                ExercisesTable.ExercisesEntry._ID + "=" + id, null);
        exerciseListAdapter.swapCursor(getAllItems(id));
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
        navController.navigate(R.id.action_navigation_list_to_training);*/

    }
}