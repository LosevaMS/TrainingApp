package Fragments;

import android.content.ClipData;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import Adapters.ExercisesAdapter;
import Tables.ExercisesTable;
import Tables.HistoryTable;

public class TrainingFragment extends Fragment {

    private SQLiteDatabase database;
    private Chronometer chronometer;
    private boolean btnColor = true;
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

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);

        navBar.setVisibility(View.GONE);assert getArguments() != null;
        final long arg1 = getArguments().getLong("prog_id");
        final String arg2 = getArguments().getString("prog_name");

        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(arg2);

        TextView finishText = view.findViewById(R.id.finish_training);

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        final Date date = new Date();

        //chronometer = view.findViewById(R.id.chronometer);
        //chronometerHelper = new ChronometerHelper();


        /*if((savedInstanceState !=null) && savedInstanceState.containsKey("ChronoTime"))
            chronometer.setBase(savedInstanceState.getLong("ChronoTime"));
        Toast.makeText(getActivity()," = " + savedInstanceState.getLong("ChronoTime") ,
                Toast.LENGTH_LONG).show();*/



       // startStopWatch();

       /* if (chronometerHelper.getStartTime() != null) {
            if (chronometerHelper.getPause()==null) chronometer.setBase(chronometerHelper.getStartTime());
            else chronometer.setBase(SystemClock.elapsedRealtime()-chronometerHelper.getPause());
        }
            if (chronometerHelper.getStartTime() != null && chronometerHelper.isRunning()) {
            chronometer.setBase(chronometerHelper.getStartTime());
            chronometer.start();
        }*/


        /*chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 10000) {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    Toast.makeText(getActivity(), "Bing!", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        final RecyclerView recyclerView2 = view.findViewById(R.id.exercises_recyclerview);
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext()));
        ExercisesAdapter exercisesAdapter = new ExercisesAdapter(requireContext(), getAllItems(arg1));
        recyclerView2.setAdapter(exercisesAdapter);

        finishText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues cv = new ContentValues();
                cv.put(HistoryTable.HistoryEntry.HISTORY_PROG_ID, arg1);
                cv.put(HistoryTable.HistoryEntry.HISTORY_PROG_NAME, arg2);
                cv.put(HistoryTable.HistoryEntry.HISTORY_DATE, formatter.format(date));

                database.insert(HistoryTable.HistoryEntry.TABLE_HISTORY, null, cv);

                chronometer.stop();
                String time;
                int elapsed = (int)(SystemClock.elapsedRealtime()-chronometer.getBase());
                time = String.valueOf(elapsed/1000);
                Toast.makeText(getActivity(), "Time is: "+time+" sec",
                        Toast.LENGTH_LONG).show();
                resetChronometer(v);
                pauseChronometer(v);

                final NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_training_to_navigation_list);
            }
        });
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


    public void startChronometer(View v) {
        if (!chronometerHelper.isRunning()) {
            if (chronometerHelper.getPause()!=null){ chronometer.setBase(SystemClock.elapsedRealtime() - chronometerHelper.getPause());
            chronometerHelper.setStartTime(SystemClock.elapsedRealtime() - chronometerHelper.getPause());}
            else{
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometerHelper.setStartTime(SystemClock.elapsedRealtime());
            }
            chronometerHelper.setRunning(true);
            chronometer.start();
        }
    }

    public void pauseChronometer(View v) {
        if (chronometerHelper.isRunning()) {
            chronometer.stop();
            chronometerHelper.setPause(SystemClock.elapsedRealtime() - chronometer.getBase());
            chronometerHelper.setRunning(false);
        }
    }

    public void resetChronometer(View v) {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometerHelper.setStartTime(SystemClock.elapsedRealtime());
        chronometerHelper.setPause(0);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_for_training_fragment,menu);
        menu.findItem(R.id.play_item).setVisible(true);
        menu.findItem(R.id.timer_item).setVisible(true);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {

      if (btnColor){
          item.setIcon(R.drawable.ic_pause);
          startChronometer(requireView());
          btnColor = false;
      }else{
          item.setIcon(R.drawable.ic_play_white);
          pauseChronometer(requireView());
          btnColor = true;
      }
      if (item.getItemId()==android.R.id.home){
          pauseChronometer(requireView());
      }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        final MenuItem menuItem  = menu.findItem(R.id.timer_item);
        FrameLayout rootView = (FrameLayout) menuItem.getActionView();
        chronometer = (Chronometer) rootView.findViewById(R.id.chronometer2);
        chronometerHelper = new ChronometerHelper();

        MenuItem playPauseBtn = menu.findItem(R.id.play_item);
        if (chronometerHelper.isRunning()) playPauseBtn.setIcon(R.drawable.ic_pause);

        if (chronometerHelper.getStartTime() != null) {
            if (chronometerHelper.getPause()==null) chronometer.setBase(chronometerHelper.getStartTime());
            else chronometer.setBase(SystemClock.elapsedRealtime()-chronometerHelper.getPause());
        }
        if (chronometerHelper.getStartTime() != null && chronometerHelper.isRunning()) {
            chronometer.setBase(chronometerHelper.getStartTime());
            chronometer.start();
        }
        super.onPrepareOptionsMenu(menu);
    }

}
