package com.example.globusproject.Fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import com.example.globusproject.Adapters.ApproachAdapter;
import com.example.globusproject.Tables.ApproachesTable;

public class ApproachFragment extends Fragment {

    private SQLiteDatabase database;
    private ApproachAdapter approachAdapter1, approachAdapter2;
    private EditText inputWeight, inputCount;
    private TextView lastTime;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_approach, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);

        BottomNavigationView navBar = requireActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        lastTime = view.findViewById(R.id.last_time);

        assert getArguments() != null;
        final long exercise_id = getArguments().getLong("ex_id");

        final RecyclerView recyclerView1 = view.findViewById(R.id.last_approach_recyclerview);
        recyclerView1.setLayoutManager(new LinearLayoutManager(requireContext()));
        try {
            approachAdapter1 = new ApproachAdapter(requireContext(), getPreviousItems(exercise_id));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        recyclerView1.setAdapter(approachAdapter1);

        final RecyclerView recyclerView2 = view.findViewById(R.id.approach_recyclerview);
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext()));
        approachAdapter2 = new ApproachAdapter(requireContext(), getAllItems(exercise_id));
        recyclerView2.setAdapter(approachAdapter2);


        FloatingActionButton add_approach_btn = view.findViewById(R.id.add_approach_btn);

        add_approach_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showAlertDialog();
            }
        });

        androidx.appcompat.widget.Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = Navigation.findNavController(requireView());
                if (!navController.popBackStack()) {
                    navController.navigate(R.id.action_approach_to_training);
                }
            }
        });

    }

    private void showAlertDialog(){

        assert getArguments() != null;
        final long exercise_id = getArguments().getLong("ex_id");
        final int program_id = getArguments().getInt("prog_id");

        LayoutInflater layoutInflater = LayoutInflater.from(requireContext());
        View customDialogView = layoutInflater.inflate(R.layout.dialog_add_approach, null);

        final AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(requireContext());

        mDialogBuilder.setView(customDialogView);

        inputWeight = customDialogView.findViewById(R.id.input_weight);
        inputCount = customDialogView.findViewById(R.id.input_count);

        inputWeight.setImeActionLabel("", EditorInfo.IME_ACTION_NEXT);

        inputWeight.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (inputWeight.getText().toString().trim().equalsIgnoreCase(""))
                        inputWeight.setError("Введите вес!");
                }
                return false;
            }
        });
        inputCount.setImeActionLabel("", EditorInfo.IME_ACTION_NEXT);

        inputCount.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (inputCount.getText().toString().trim().equalsIgnoreCase(""))
                        inputCount.setError("Введите кол-во повторений!");
                }
                return false;
            }
        });

        mDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
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
                if (inputWeight.getText().toString().isEmpty()){
                    inputWeight.setError("Введите вес!");
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                if (inputCount.getText().toString().isEmpty()){
                    inputCount.setError("Введите кол-во повторений!");
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                if (!inputCount.getText().toString().isEmpty() && !inputWeight.getText().toString().isEmpty()) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    addItem(exercise_id, program_id);
                    alertDialog.dismiss();
                }
            }
        });
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(alertDialog.getWindow()).getAttributes());
        layoutParams.width = (int) (displayWidth * 0.80f);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(layoutParams);

        inputWeight.getText().clear();
        inputCount.getText().clear();
    }

    private void addItem(long exercise_id, int program_id) {

        if (inputWeight.getText().toString().trim().length() == 0
                && inputCount.getText().toString().trim().length() == 0) {
            return;
        }

        double weight = Double.parseDouble(inputWeight.getText().toString());
        int count = Integer.parseInt(inputCount.getText().toString());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = new Date();

        ContentValues cv = new ContentValues();
        cv.put(ApproachesTable.ApproachesEntry.APP_WEIGHT, weight);
        cv.put(ApproachesTable.ApproachesEntry.APP_COUNT, count);
        cv.put(ApproachesTable.ApproachesEntry.APP_EX_ID, (int) exercise_id);
        cv.put(ApproachesTable.ApproachesEntry.APP_PROG_ID, program_id);
        cv.put(ApproachesTable.ApproachesEntry.APP_DATE, formatter.format(date));
        cv.put(ApproachesTable.ApproachesEntry.APP_IS_CURRENT, true);

        database.insert(ApproachesTable.ApproachesEntry.TABLE_APPROACHES, null, cv);
        approachAdapter2.swapCursor(getAllItems(exercise_id));

        inputWeight.getText().clear();
        inputCount.getText().clear();
    }

    private Cursor getAllItems(long id) {

        String whereClause = ApproachesTable.ApproachesEntry.APP_EX_ID + "=? AND " +
                ApproachesTable.ApproachesEntry.APP_IS_CURRENT + "=?";

        String[] whereArgs = new String[]{String.valueOf(id), String.valueOf(1)};

        return database.query(
                ApproachesTable.ApproachesEntry.TABLE_APPROACHES,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
    }

    private Cursor getPreviousItems(long id) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date1 = new Date();
        formatter.format(date1);

        String whereClause = ApproachesTable.ApproachesEntry.APP_EX_ID + "=? AND " +
                ApproachesTable.ApproachesEntry.APP_DATE + "=?";
        String[] whereArgs = new String[]{String.valueOf(id), searchPreviousDate(id, date1)};

        if(searchPreviousDate(id, date1).equals("error"))
            lastTime.setVisibility(View.GONE);
        else
            lastTime.setVisibility(View.VISIBLE);

        return database.query(
                ApproachesTable.ApproachesEntry.TABLE_APPROACHES,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
    }

    private String searchPreviousDate(long id, Date date) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        String whereClause = ApproachesTable.ApproachesEntry.APP_EX_ID + "=? AND " +
                ApproachesTable.ApproachesEntry.APP_IS_CURRENT + "=?";
        String[] whereArgs = new String[]{String.valueOf(id), String.valueOf(0)};

        Cursor c = database.query(
                ApproachesTable.ApproachesEntry.TABLE_APPROACHES,
                new String[]{"date"},
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        String stringDate, result;
        result = "error";
        Date minDate;
        minDate = formatter.parse("1999-01-01 00:00");

        while (c.moveToNext()) {
            stringDate = c.getString(c.getColumnIndex("date"));

            assert minDate != null;
            if (minDate.getTime() < date.getTime() && minDate.getTime() < Objects.requireNonNull(formatter.parse(stringDate)).getTime()) {
                minDate.setTime(Objects.requireNonNull(formatter.parse(stringDate)).getTime());
                result = stringDate;

            }
        }
        c.close();
        return result;
    }
}
