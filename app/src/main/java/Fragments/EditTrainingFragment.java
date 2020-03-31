package Fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Adapters.ExerciseListAdapter;
import Tables.ExercisesTable;
import Tables.ProgramTable;

import static android.view.View.GONE;

public class EditTrainingFragment extends Fragment implements ExerciseListAdapter.OnNoteListener{

    private EditText userInput, userInput2;
    private SQLiteDatabase database;
    private ExerciseListAdapter exerciseListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_training,container,false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(GONE);

        userInput = view.findViewById(R.id.edit_training_name);
        FloatingActionButton add_exercise_button = view.findViewById(R.id.add_exercise_btn);
        FloatingActionButton okBtn = view.findViewById(R.id.ok_btn);

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        assert getArguments() != null;
        final long arg1 = getArguments().getLong("prog_id");
        final String arg2 = getArguments().getString("prog_name");

        final RecyclerView recyclerView2 = view.findViewById(R.id.recyclerview2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext()));
        exerciseListAdapter = new ExerciseListAdapter(requireContext(),getAllItems(arg1),this);
        recyclerView2.setAdapter(exerciseListAdapter);

        userInput.setText(arg2);

        add_exercise_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                LayoutInflater li = LayoutInflater.from(requireContext());
                View promptsView = li.inflate(R.layout.dialog_add_exercises, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(requireContext());

                mDialogBuilder.setView(promptsView);

                userInput2 = promptsView.findViewById(R.id.input_exercise_name);

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

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userInput.getText().toString();
                if(name !=arg2) {
                    ContentValues cv = new ContentValues();
                    cv.put(ProgramTable.ProgramEntry.PROG_NAME, name);
                    database.update(ProgramTable.ProgramEntry.TABLE_PROGRAMS, cv, "name = ?", new String[]{arg2});
                    //exerciseListAdapter.swapCursor(getAllItems(arg1));
                }
                final NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_edit_training_to_navigation_list);
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