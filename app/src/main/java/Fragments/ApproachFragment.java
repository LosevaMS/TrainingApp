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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Adapters.ApproachAdapter;
import Tables.ApproachesTable;

public class ApproachFragment extends Fragment implements ApproachAdapter.OnNoteListener {

    private SQLiteDatabase database;
    private ApproachAdapter approachAdapter1, approachAdapter2;
    private EditText inputWeight, inputCount;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_approach, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = new Date();
        formatter.format(date1);

        assert getArguments() != null;
        final long arg1 = getArguments().getLong("ex_id");
        final int arg2 = getArguments().getInt("prog_id");

        final RecyclerView recyclerView1 = view.findViewById(R.id.last_approach_recyclerview);
        recyclerView1.setLayoutManager(new LinearLayoutManager(requireContext()));
        try {
            approachAdapter1 = new ApproachAdapter(requireContext(), getPreviousItems(arg1), this);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        recyclerView1.setAdapter(approachAdapter1);

        final RecyclerView recyclerView2 = view.findViewById(R.id.approach_recyclerview);
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext()));
        try {
            approachAdapter2 = new ApproachAdapter(requireContext(), getAllItems(arg1), this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        recyclerView2.setAdapter(approachAdapter2);


        FloatingActionButton add_approach_btn = view.findViewById(R.id.add_approach_btn);

        add_approach_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                LayoutInflater li = LayoutInflater.from(requireContext());
                View promptsView = li.inflate(R.layout.dialog_add_approach, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(requireContext());

                mDialogBuilder.setView(promptsView);

                inputWeight = promptsView.findViewById(R.id.input_weight);
                inputCount = promptsView.findViewById(R.id.input_count);

                inputWeight.setImeActionLabel("", EditorInfo.IME_ACTION_NEXT);

                inputWeight.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT) {
                            if (inputWeight.getText().toString().trim().equalsIgnoreCase(""))
                                inputWeight.setError("Введите вес!");
                            else
                                Toast.makeText(requireContext(), "Notnull", Toast.LENGTH_SHORT).show();
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
                                inputCount.setError("Введите количество повторений!");
                            else
                                Toast.makeText(requireContext(), "Notnull", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });

                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        try {
                                            addItem(arg1, arg2);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        // dialog.cancel();
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
                inputWeight.getText().clear();
                inputCount.getText().clear();
            }
        });

    }

    private void addItem(long ex_id, int prog_id) throws ParseException {

        if (inputWeight.getText().toString().trim().length() == 0
                && inputCount.getText().toString().trim().length() == 0) {
            return;
        }

        double weight = Double.parseDouble(inputWeight.getText().toString());
        int count = Integer.parseInt(inputCount.getText().toString());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        ContentValues cv = new ContentValues();
        cv.put(ApproachesTable.ApproachesEntry.APP_WEIGHT, weight);
        cv.put(ApproachesTable.ApproachesEntry.APP_COUNT, count);
        cv.put(ApproachesTable.ApproachesEntry.APP_EX_ID, (int) ex_id);
        cv.put(ApproachesTable.ApproachesEntry.APP_PROG_ID, prog_id);
        cv.put(ApproachesTable.ApproachesEntry.APP_DATE, formatter.format(date));

        database.insert(ApproachesTable.ApproachesEntry.TABLE_APPROACHES, null, cv);
        approachAdapter2.swapCursor(getAllItems(ex_id));

        inputWeight.getText().clear();
        inputCount.getText().clear();
    }

    private Cursor getAllItems(long id) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = new Date();
        formatter.format(date1);

        String whereClause = ApproachesTable.ApproachesEntry.APP_EX_ID + "=? AND " +
                ApproachesTable.ApproachesEntry.APP_DATE + "=?";
        String[] whereArgs = new String[]{String.valueOf(id), formatter.format(date1)};

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

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = new Date();
        formatter.format(date1);

        String whereClause = ApproachesTable.ApproachesEntry.APP_EX_ID + "=? AND " +
                ApproachesTable.ApproachesEntry.APP_DATE + "=?";
        String[] whereArgs = new String[]{String.valueOf(id), searchPreviousDate(id, date1)};

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

    public String searchPreviousDate(long id, Date date) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String query = "select date from " + ApproachesTable.ApproachesEntry.TABLE_APPROACHES + " WHERE _excercises_id = " + id;
        Cursor c = database.rawQuery(query, null);

        String a, res;
        res = "error";
        Date date2;
        date2 = formatter.parse("1999-01-01");
        boolean flag = false;
        while (c.moveToNext()) {
            a = c.getString(c.getColumnIndex("date"));

            if (date2.getTime() < date.getTime() && date2.getTime() < formatter.parse(a).getTime()) {
                flag = true;
                if (formatter.parse(a).getYear() == date.getYear() && formatter.parse(a).getMonth() == date.getMonth() &&
                        formatter.parse(a).getDay() != date.getDay() && flag == true) {
                    date2.setTime(formatter.parse(a).getTime());
                    res = a;
                }
            }
        }
        c.close();
        return res;
    }

    @Override
    public void onNoteClick(int position) {

    }
}
