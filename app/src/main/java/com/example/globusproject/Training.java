package com.example.globusproject;

import android.app.ActionBar;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    private Button add_program_button;
    private TextView delete_program_button;
    private EditText userInput;
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

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        final RecyclerView recyclerView2 = view.findViewById(R.id.recyclerview2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext()));
        exerciseListAdapter = new ExerciseListAdapter(requireContext(),getAllItems(),this);
        recyclerView2.setAdapter(exerciseListAdapter);

        long arg1 = getArguments().getLong("prog_id");

        Toast toast = Toast.makeText(requireContext(),String.valueOf(arg1),Toast.LENGTH_LONG);
        toast.show();

        ContentValues cv = new ContentValues();
        //cv.put(ExercisesTable.ExercisesEntry.EX_NAME, "Жим лежа");
        //cv.put(ExercisesTable.ExercisesEntry.EX_PROG_ID,(int)arg1);
        //cv.put(ExercisesTable.ExercisesEntry.EX_NAME, "Бабочка");
        //cv.put(ExercisesTable.ExercisesEntry.EX_PROG_ID,2);

        //database.insert(ExercisesTable.ExercisesEntry.TABLE_EXERCISES, null, cv);
        //exerciseListAdapter.swapCursor(getAllItems());

    }

    private void addItem() {

        if (userInput.getText().toString().trim().length() == 0 ) {
            return;
        }

        String name = userInput.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(ProgramTable.ProgramEntry.PROG_NAME, name);

        database.insert(ProgramTable.ProgramEntry.TABLE_PROGRAMS, null, cv);
        exerciseListAdapter.swapCursor(getAllItems());

        userInput.getText().clear();
    }

    private void removeItem(long id) {
        database.delete(ProgramTable.ProgramEntry.TABLE_PROGRAMS,
                ProgramTable.ProgramEntry._ID + "=" + id, null);
        exerciseListAdapter.swapCursor(getAllItems());
    }

    private Cursor getAllItems() {
        return database.query(
                ExercisesTable.ExercisesEntry.TABLE_EXERCISES,
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