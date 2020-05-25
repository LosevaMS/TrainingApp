package Fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.ChronometerHelper;
import com.example.globusproject.DBHelper;
import com.example.globusproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import Adapters.ExercisesAdapter;
import Tables.ApproachesTable;
import Tables.ExercisesTable;
import Tables.HistoryApproachesTable;
import Tables.HistoryTable;
import Tables.ProgramTable;

public class TrainingFragment extends Fragment {

    private SQLiteDatabase database;
    private Chronometer chronometer;
    static private boolean btnPause = true;

    private ChronometerHelper chronometerHelper;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_training, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        BottomNavigationView navBar = requireActivity().findViewById(R.id.nav_view);

        navBar.setVisibility(View.GONE);
        assert getArguments() != null;
        final long arg1 = getArguments().getLong("prog_id");
        final String arg2 = getArguments().getString("prog_name");

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(arg2);

        TextView finishText = view.findViewById(R.id.finish_training);

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        final Date date = new Date();

        final RecyclerView recyclerView2 = view.findViewById(R.id.exercises_recyclerview);
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext()));
        ExercisesAdapter exercisesAdapter = new ExercisesAdapter(requireContext(), getAllItems(arg1));
        recyclerView2.setAdapter(exercisesAdapter);

        finishText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chronometer.stop();
                int elapsed;
                if (chronometerHelper.getPause() != null && !chronometerHelper.isRunning())
                    elapsed = chronometerHelper.getPause().intValue();
                else
                    elapsed = (int) (SystemClock.elapsedRealtime() - chronometer.getBase());

                int time = elapsed / 60000;
                Toast.makeText(getActivity(), "Time is: " + time + " min",
                        Toast.LENGTH_LONG).show();
                resetChronometer();
                pauseChronometer();

                btnPause = true;

                ContentValues cv = new ContentValues();
                cv.put(HistoryTable.HistoryEntry.HISTORY_PROG_ID, arg1);
                cv.put(HistoryTable.HistoryEntry.HISTORY_PROG_NAME, arg2);
                cv.put(HistoryTable.HistoryEntry.HISTORY_URI, searchUri(arg1));
                cv.put(HistoryTable.HistoryEntry.HISTORY_DATE, formatter.format(date));
                cv.put(HistoryTable.HistoryEntry.HISTORY_TIME, time);

                addApproachesInHistory(formatter.format(date));
                changeApproachesState(formatter.format(date));

                database.insert(HistoryTable.HistoryEntry.TABLE_HISTORY, null, cv);

                final NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_training_to_navigation_list);
            }
        });

        androidx.appcompat.widget.Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = Navigation.findNavController(requireView());
                if (!navController.popBackStack()) {
                    navController.navigate(R.id.action_training_to_navigation_list);
                }
            }
        });
    }

    private void changeApproachesState(String date) {

        ContentValues cv = new ContentValues();
        cv.put(ApproachesTable.ApproachesEntry.APP_IS_CURRENT, false);
        cv.put(ApproachesTable.ApproachesEntry.APP_DATE, date);

        database.update(ApproachesTable.ApproachesEntry.TABLE_APPROACHES, cv,
                ApproachesTable.ApproachesEntry.APP_IS_CURRENT + "=" + 1, null);

    }

    private void addApproachesInHistory(String date) {

        ContentValues cv;

        String whereClause = ApproachesTable.ApproachesEntry.APP_IS_CURRENT + "=?";
        String[] whereArgs = new String[]{String.valueOf(1)};

        Cursor c = database.query(
                ApproachesTable.ApproachesEntry.TABLE_APPROACHES,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        while (c.moveToNext()) {
            cv = new ContentValues();
            cv.put(HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_PROG_ID,
                    c.getInt(c.getColumnIndex(ApproachesTable.ApproachesEntry.APP_PROG_ID)));
            cv.put(HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_EX_ID,
                    c.getInt(c.getColumnIndex(ApproachesTable.ApproachesEntry.APP_EX_ID)));
            cv.put(HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_DATE, date);
            cv.put(HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_COUNT,
                    c.getInt(c.getColumnIndex(ApproachesTable.ApproachesEntry.APP_COUNT)));
            cv.put(HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_WEIGHT,
                    c.getDouble(c.getColumnIndex(ApproachesTable.ApproachesEntry.APP_WEIGHT)));
            cv.put(HistoryApproachesTable.HistoryApproachesEntry._ID,
                    c.getInt(c.getColumnIndex(ApproachesTable.ApproachesEntry._ID)));
            database.insert(HistoryApproachesTable.HistoryApproachesEntry.TABLE_HISTORY_APPROACHES, null, cv);
        }
        c.close();
    }

    private String searchUri(long id) {
        String query = "select uri from " + ProgramTable.ProgramEntry.TABLE_PROGRAMS + " WHERE _id = " + id;
        Cursor c = database.rawQuery(query, null);

        String a = "not found";
        if (c.moveToFirst()) {
            a = c.getString(c.getColumnIndex("uri"));
        }
        c.close();
        return a;
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

    private void startChronometer() {
        if (!chronometerHelper.isRunning()) {
            if (chronometerHelper.getPause() != null) {
                chronometer.setBase(SystemClock.elapsedRealtime() - chronometerHelper.getPause());
                chronometerHelper.setStartTime(SystemClock.elapsedRealtime() - chronometerHelper.getPause());
            } else {
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometerHelper.setStartTime(SystemClock.elapsedRealtime());
            }
            chronometerHelper.setRunning(true);
            chronometer.start();
        }
    }

    private void pauseChronometer() {
        if (chronometerHelper.isRunning()) {
            chronometer.stop();
            chronometerHelper.setPause(SystemClock.elapsedRealtime() - chronometer.getBase());
            chronometerHelper.setRunning(false);
        }
    }

    private void resetChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometerHelper.setStartTime(SystemClock.elapsedRealtime());
        chronometerHelper.setPause(0);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_for_training_fragment, menu);
        menu.findItem(R.id.play_item).setVisible(true);
        menu.findItem(R.id.timer_item).setVisible(true);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {

        if (btnPause) {
            item.setIcon(R.drawable.ic_pause);
            startChronometer();
            btnPause = false;
        } else {
            item.setIcon(R.drawable.ic_play_white);
            pauseChronometer();
            btnPause = true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.timer_item);
        FrameLayout rootView = (FrameLayout) menuItem.getActionView();
        chronometer = rootView.findViewById(R.id.chronometer2);
        chronometerHelper = new ChronometerHelper();

        MenuItem playPauseBtn = menu.findItem(R.id.play_item);
        if (chronometerHelper.isRunning()) playPauseBtn.setIcon(R.drawable.ic_pause);

        if (chronometerHelper.getStartTime() != null) {
            if (chronometerHelper.getPause() == null)
                chronometer.setBase(chronometerHelper.getStartTime());
            else chronometer.setBase(SystemClock.elapsedRealtime() - chronometerHelper.getPause());
        }
        if (chronometerHelper.getStartTime() != null && chronometerHelper.isRunning()) {
            chronometer.setBase(chronometerHelper.getStartTime());
            chronometer.start();
        }
        super.onPrepareOptionsMenu(menu);
    }

}
