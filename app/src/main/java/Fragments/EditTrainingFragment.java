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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.DBHelper;
import com.example.globusproject.InlineExercises;
import com.example.globusproject.R;
import com.example.globusproject.SharedViewModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Objects;

import Adapters.ExerciseListAdapter;
import Tables.ExercisesTable;
import Tables.ProgramTable;

import static android.view.View.GONE;

public class EditTrainingFragment extends Fragment {

    private EditText userInput2;
    private SQLiteDatabase database;
    private ExerciseListAdapter exerciseListAdapter;

    private SharedViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_training, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(GONE);

        EditText userInput = view.findViewById(R.id.edit_training_name);

        FloatingActionButton createExerciseBtn = view.findViewById(R.id.menu_create_ex_btn);
        FloatingActionButton chooseFromListBtn = view.findViewById(R.id.menu_list_btn);


        chooseFromListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Выбрать упражнение из списка",
                        Toast.LENGTH_LONG).show();
            }
        });


        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        assert getArguments() != null;
        final long arg1 = getArguments().getLong("prog_id");
        final String arg2 = getArguments().getString("prog_name");


        final RecyclerView recyclerView2 = view.findViewById(R.id.recyclerview2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext()));
        exerciseListAdapter = new ExerciseListAdapter(requireContext(), getAllItems(arg1));
        recyclerView2.setAdapter(exerciseListAdapter);

        userInput.setText(arg2);

        createExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(requireContext());
                View promptsView = li.inflate(R.layout.dialog_add_exercises, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(requireContext());

                mDialogBuilder.setView(promptsView);

                userInput2 = promptsView.findViewById(R.id.input_exercise_name);

                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        addItem(arg1);
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
                userInput2.getText().clear();
            }
        });

        chooseFromListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_edit_training_to_fragment_inline_exercises);
            }
        });


        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        viewModel.getText().observe(getViewLifecycleOwner(), new Observer<ArrayList<InlineExercises>>() {
            @Override
            public void onChanged(@Nullable ArrayList<InlineExercises> selectedExercises) {
                assert selectedExercises != null;
                for (int i = 0; i < selectedExercises.size(); i++) {
                    Toast.makeText(getActivity(), selectedExercises.get(i).getName() + " ",
                            Toast.LENGTH_SHORT).show();
                    addFromInlineList(arg1,selectedExercises.get(i));
                }
            }
        });

    }

    private void addFromInlineList(long id, InlineExercises item){

        ContentValues cv = new ContentValues();
        cv.put(ExercisesTable.ExercisesEntry.EX_NAME, item.getName());
        cv.put(ExercisesTable.ExercisesEntry.EX_PROG_ID, (int) id);
        cv.put(ExercisesTable.ExercisesEntry.EX_URI, item.getUri());

        database.insert(ExercisesTable.ExercisesEntry.TABLE_EXERCISES, null, cv);
        exerciseListAdapter.swapCursor(getAllItems(id));
    }

    private void addItem(long id) {

        if (userInput2.getText().toString().trim().length() == 0) {
            return;
        }

        String name = userInput2.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(ExercisesTable.ExercisesEntry.EX_NAME, name);
        cv.put(ExercisesTable.ExercisesEntry.EX_PROG_ID, (int) id);

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
}