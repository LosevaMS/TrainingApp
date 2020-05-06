package Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.DBHelper;
import com.example.globusproject.InlineExercises;
import com.example.globusproject.R;
import com.example.globusproject.SharedViewModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import Adapters.ExerciseListAdapter;
import Tables.ExercisesTable;
import Tables.ProgramTable;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

public class EditTrainingFragment extends Fragment {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static final int IMAGE_CHANGE_CODE = 1002;

    private SQLiteDatabase database;
    private ExerciseListAdapter exerciseListAdapter;
    private EditText userInput;
    private EditText userInput2;
    private ImageView imageView;
    private Uri uri;
    private Button loadImage_btn;
    private CardView cardView;

    private SharedViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_training, container, false);
    }

    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(GONE);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        userInput = view.findViewById(R.id.edit_training_name);
        userInput2 = view.findViewById(R.id.input_text);
        loadImage_btn = view.findViewById(R.id.load_image_btn);

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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem((long) viewHolder.itemView.getTag());
                exerciseListAdapter = new ExerciseListAdapter(requireContext(), getAllItems(arg1));
                recyclerView2.setAdapter(exerciseListAdapter);
            }
        }).attachToRecyclerView(recyclerView2);


        userInput.setText(arg2);

        createExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(requireContext());
                View promptsView = li.inflate(R.layout.dialog_add_exercises, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(requireContext());

                mDialogBuilder.setView(promptsView);

                userInput2 = promptsView.findViewById(R.id.input_text);
                loadImage_btn = promptsView.findViewById(R.id.load_image_btn);
                imageView = promptsView.findViewById(R.id.preview_image);
                cardView = promptsView.findViewById(R.id.cardview_for_preview);

                uri = null;

                loadImage_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_DENIED) {
                                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                                requestPermissions(permissions, PERMISSION_CODE);
                            } else {
                                pickImageFromGallery();
                            }
                        } else {
                            pickImageFromGallery();
                        }
                    }
                });

                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        addItem(arg1);
                                        exerciseListAdapter = new ExerciseListAdapter(requireContext(), getAllItems(arg1));
                                        recyclerView2.setAdapter(exerciseListAdapter);
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
                // alertDialog.getWindow().setLayout(860, 1000);
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
                    /*Toast.makeText(getActivity(), selectedExercises.get(i).getName() + " ",
                            Toast.LENGTH_SHORT).show();*/
                    addFromInlineList(arg1,selectedExercises.get(i));
                }
            }
        });
        viewModel.deleteAll();

        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                String name = userInput.getText().toString();
                ContentValues cv = new ContentValues();
                cv.put(ProgramTable.ProgramEntry.PROG_NAME, name);
                database.update(ProgramTable.ProgramEntry.TABLE_PROGRAMS, cv,
                        ProgramTable.ProgramEntry._ID + "=" + (int) (getArguments().getLong("prog_id")),null);

                final NavController navController = Navigation.findNavController(getView());
                if (!navController.popBackStack()) {
                    navController.navigate(R.id.action_edit_training_to_navigation_list);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        androidx.appcompat.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userInput.getText().toString();
                ContentValues cv = new ContentValues();
                cv.put(ProgramTable.ProgramEntry.PROG_NAME, name);
                database.update(ProgramTable.ProgramEntry.TABLE_PROGRAMS, cv,
                        ProgramTable.ProgramEntry._ID + "=" + (int) (getArguments().getLong("prog_id")),null);
                final NavController navController = Navigation.findNavController(getView());
                if (!navController.popBackStack()) {
                    navController.navigate(R.id.action_edit_training_to_navigation_list);
                }
            }
        });

    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void changeImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_CHANGE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast toast = Toast.makeText(requireContext(),
                            "Permissions denied", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            uri = data.getData();
            imageView.setImageURI(data.getData());
        }
        if (resultCode == RESULT_OK && requestCode == IMAGE_CHANGE_CODE) {
            uri = data.getData();
            ContentValues cv = new ContentValues();
            cv.put(ProgramTable.ProgramEntry.PROG_URI, String.valueOf(uri));

            database.update(ProgramTable.ProgramEntry.TABLE_PROGRAMS, cv,
                    ProgramTable.ProgramEntry._ID + "=" + (int) (getArguments().getLong("prog_id")),null);
        }
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
        cv.put(ExercisesTable.ExercisesEntry.EX_URI, String.valueOf(uri));

        database.insert(ExercisesTable.ExercisesEntry.TABLE_EXERCISES, null, cv);
        exerciseListAdapter.swapCursor(getAllItems(id));

        userInput2.getText().clear();
    }

    private void removeItem(long id) {
        database.delete(ExercisesTable.ExercisesEntry.TABLE_EXERCISES,
                ExercisesTable.ExercisesEntry._ID + "=" + id, null);
        exerciseListAdapter.swapCursor(getAllItems(getArguments().getLong("prog_id")));
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_for_edit_training_fragment,menu);
        menu.findItem(R.id.change_photo_item).setVisible(true);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {

        if (item.getItemId()==R.id.change_photo_item){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    changeImageFromGallery();
                }
            } else {
                changeImageFromGallery();
            }
        }
       /* if(item.getItemId()==android.R.id.home){
            String name = userInput.getText().toString();
            ContentValues cv = new ContentValues();
            cv.put(ProgramTable.ProgramEntry.PROG_NAME, name);
            database.update(ProgramTable.ProgramEntry.TABLE_PROGRAMS, cv,
                    ProgramTable.ProgramEntry._ID + "=" + (int) (getArguments().getLong("prog_id")),null);
            final NavController navController = Navigation.findNavController(getView());
            if (!navController.popBackStack()) {
                navController.navigate(R.id.action_edit_training_to_navigation_list);
            }
        }*/
        return super.onOptionsItemSelected(item);
    }
}