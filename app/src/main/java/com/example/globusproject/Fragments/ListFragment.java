package com.example.globusproject.Fragments;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import com.example.globusproject.Adapters.ProgramListAdapter;
import com.example.globusproject.Tables.ProgramTable;

import static android.app.Activity.RESULT_OK;


public class ListFragment extends Fragment {

    private Button loadImage_btn;
    private EditText userInput;
    private SQLiteDatabase database;
    private ProgramListAdapter programListAdapter;
    private ImageView imageView;
    private Uri uri;
    private RecyclerView recyclerView;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        BottomNavigationView navBar = requireActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.VISIBLE);

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        programListAdapter = new ProgramListAdapter(requireContext(), getAllItems());
        recyclerView.setAdapter(programListAdapter);

        userInput = view.findViewById(R.id.input_text);
        loadImage_btn = view.findViewById(R.id.load_image_btn);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NotNull final RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(requireContext());
                mDialogBuilder
                        .setMessage("Удалить тренировку?")
                        .setCancelable(false)
                        .setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        removeItem((long) viewHolder.itemView.getTag());
                                    }
                                })
                        .setNegativeButton("Нет",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        programListAdapter = new ProgramListAdapter(requireContext(), getAllItems());
                                        recyclerView.setAdapter(programListAdapter);
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
            }
        }).attachToRecyclerView(recyclerView);

        assert savedInstanceState != null;
        onSaveInstanceState(savedInstanceState);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast toast = Toast.makeText(requireContext(),
                        "Permissions denied", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            assert data != null;
            uri = data.getData();
            imageView.setImageURI(data.getData());
        }
    }

    private void addItem() {

        if (userInput.getText().toString().trim().length() == 0) {
            return;
        }

        String name = userInput.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(ProgramTable.ProgramEntry.PROG_NAME, name);
        cv.put(ProgramTable.ProgramEntry.PROG_URI, String.valueOf(uri));

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

    private void showAlertDialog() {

        LayoutInflater li = LayoutInflater.from(requireContext());
        View promptsView = li.inflate(R.layout.dialog_add_program, null);

        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(requireContext());

        mDialogBuilder.setView(promptsView);

        userInput = promptsView.findViewById(R.id.input_text);
        loadImage_btn = promptsView.findViewById(R.id.load_image_btn);
        imageView = promptsView.findViewById(R.id.preview_image);

        userInput.setImeActionLabel("", EditorInfo.IME_ACTION_NEXT);

        userInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (userInput.getText().toString().trim().equalsIgnoreCase(""))
                        userInput.setError("Введите название!");
                }
                return false;
            }
        });

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
                .setCancelable(true)
                .setPositiveButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        final AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInput.getText().toString().isEmpty()) {
                    userInput.setError("Введите название!");
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                if (!userInput.getText().toString().isEmpty()) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    addItem();
                    alertDialog.dismiss();
                }
            }
        });
        userInput.getText().clear();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_list_menu, menu);
        MenuItem addItem = menu.findItem(R.id.add_program_item);

        addItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showAlertDialog();
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}