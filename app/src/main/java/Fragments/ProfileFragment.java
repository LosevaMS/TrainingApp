package Fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import Tables.HistoryTable;
import Tables.WeightTable;


public class ProfileFragment extends Fragment {

    private SQLiteDatabase database;
    private CompactCalendarView compactCalendar;
    private LineChart weightChart;
    private LineDataSet lineDataSet1 = new LineDataSet(null,null);
    private LineDataSet lineDataSet2 = new LineDataSet(null,null);
    private ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    private LineData lineData;
    private EditText inputWeight;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.VISIBLE);

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        /*ContentValues cv = new ContentValues();
        cv.put(WeightTable.WeightEntry.WEIGHT, 50);
        cv.put(WeightTable.WeightEntry.DATE, "2020-03-01");
        database.insert(WeightTable.WeightEntry.TABLE_WEIGHT, null, cv);

        cv = new ContentValues();
        cv.put(WeightTable.WeightEntry.WEIGHT, 46);
        cv.put(WeightTable.WeightEntry.DATE, "2020-03-23");
        database.insert(WeightTable.WeightEntry.TABLE_WEIGHT, null, cv);

        cv = new ContentValues();
        cv.put(WeightTable.WeightEntry.WEIGHT, 43);
        cv.put(WeightTable.WeightEntry.DATE, "2020-04-15");
        database.insert(WeightTable.WeightEntry.TABLE_WEIGHT, null, cv);

        cv = new ContentValues();
        cv.put(WeightTable.WeightEntry.WEIGHT, 42);
        cv.put(WeightTable.WeightEntry.DATE, "2020-05-10");
        database.insert(WeightTable.WeightEntry.TABLE_WEIGHT, null, cv);*/

        final String[] monthNames = { "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль",
                "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь" };

        final TextView monthText = view.findViewById(R.id.month_text);
        final TextView yearText = view.findViewById(R.id.year_text);
        weightChart = view.findViewById(R.id.weight_chart);
        TextView weightText = view.findViewById(R.id.weight_text);
        ImageView addWeightBtn = view.findViewById(R.id.add_weight_btn);

        try {
            lineDataSet1 = new LineDataSet(weightValues(),"Вес");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            lineDataSet2 = new LineDataSet(averageWeight(), "Средний вес");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);

        XAxis xAxis = weightChart.getXAxis();
        YAxis yAxis = weightChart.getAxisLeft();
        YAxis yAxis1 = weightChart.getAxisRight();

        xAxis.setTextColor(Color.parseColor("#FF637B8F"));
        yAxis.setTextColor(Color.parseColor("#FF637B8F"));
        yAxis1.setEnabled(false);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM");
                Timestamp ts = new Timestamp((long) value);
                Date formatDate = new Date(ts.getTime());
                return sdf.format(formatDate);
            }
        });


        Legend legend = weightChart.getLegend();
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);

        Description description = new Description();
        description.setText("");
        weightChart.setDescription(description);

        assert lineDataSet1 != null;
        lineDataSet1.setLineWidth(2);
        lineDataSet1.setColor(Color.parseColor("#FF51B1FF"));
        lineDataSet1.setCircleColor(Color.parseColor("#FF2C98F0"));
        lineDataSet1.setCircleHoleColor(Color.parseColor("#FFFFFF"));
        lineDataSet1.setCircleRadius(4);
        lineDataSet1.setCircleHoleRadius(2);
        lineDataSet1.setValueTextSize(8);
        lineDataSet1.enableDashedLine(7,10,0);

        assert lineDataSet2 != null;
        lineDataSet2.setLineWidth(1.5f);
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setDrawHighlightIndicators(false);
        lineDataSet2.setValueTextSize(8);

        lineData = new LineData(dataSets);
        weightChart.setData(lineData);
        weightChart.invalidate();

        addWeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(requireContext());
                View promptsView = li.inflate(R.layout.dialog_add_weight, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(requireContext());

                mDialogBuilder.setView(promptsView);

                inputWeight = promptsView.findViewById(R.id.input_weight_profile);

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
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        addWeight();
                                        updateChart();
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
            }
        });

       String myDate = "12-05-2020 15:03";
       SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = sdf.parse(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();


        DateFormat monthFormatter = new SimpleDateFormat("M");
        Date currentMonth = new Date();
        monthText.setText(monthNames[Integer.parseInt(monthFormatter.format(currentMonth))-1]);

        DateFormat yearFormatter = new SimpleDateFormat("yyyy");
        Date currentYear = new Date();
        yearText.setText(yearFormatter.format(currentYear));

        compactCalendar =  view.findViewById(R.id.compactcalendar_view);
        compactCalendar.setFirstDayOfWeek(Calendar.MONDAY);

        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            addTrainingInCalendar();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendar.getEvents(dateClicked);
                if (events.size()!=0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("date", dateFormat.format(dateClicked));
                    final NavController navController = Navigation.findNavController(getView());
                    navController.navigate(R.id.action_navigation_profile_to_calendarTrainingFragment, bundle);
                }
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                DateFormat formatter = new SimpleDateFormat("M");
                monthText.setText(monthNames[Integer.parseInt(formatter.format(firstDayOfNewMonth))-1]);
            }
        });


    }

    private void updateChart(){
        try {
            lineDataSet1 = new LineDataSet(weightValues(),"Вес");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            lineDataSet2 = new LineDataSet(averageWeight(), "Средний вес");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dataSets.clear();
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);


        XAxis xAxis = weightChart.getXAxis();
        YAxis yAxis = weightChart.getAxisLeft();
        YAxis yAxis1 = weightChart.getAxisRight();

        xAxis.setTextColor(Color.parseColor("#FF637B8F"));
        yAxis.setTextColor(Color.parseColor("#FF637B8F"));
        yAxis1.setEnabled(false);

        Legend legend = weightChart.getLegend();
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);

        Description description = new Description();
        description.setText("");
        weightChart.setDescription(description);

        assert lineDataSet1 != null;
        lineDataSet1.setLineWidth(2);
        lineDataSet1.setColor(Color.parseColor("#FF51B1FF"));
        lineDataSet1.setCircleColor(Color.parseColor("#FF2C98F0"));
        lineDataSet1.setCircleHoleColor(Color.parseColor("#FFFFFF"));
        lineDataSet1.setCircleRadius(4);
        lineDataSet1.setCircleHoleRadius(2);
        lineDataSet1.setValueTextSize(8);
        lineDataSet1.enableDashedLine(7,10,0);

        assert lineDataSet2 != null;
        lineDataSet2.setLineWidth(1.5f);
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setDrawHighlightIndicators(false);
        lineDataSet2.setValueTextSize(8);


        lineData = new LineData(dataSets);
        weightChart.clear();
        weightChart.setData(lineData);
        weightChart.invalidate();
    }

    private void addTrainingInCalendar() throws ParseException {

        Cursor c = database.query(
                HistoryTable.HistoryEntry.TABLE_HISTORY,
                null,
                null,
                null,
                null,
                null,
                null
        );

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        HashSet<String> dateHashSet = new HashSet<>();

        while (c.moveToNext()) {
            date = dateFormat.parse(c.getString(c.getColumnIndex("date")));
            if (dateHashSet.add(dateFormat.format(date))){
                Event ev1 = new Event(Color.parseColor("#FF2C98F0"), date.getTime());
                compactCalendar.addEvent(ev1);
            }

        }
        c.close();
    }

    private void addWeight(){
        if (inputWeight.getText().toString().trim().length() == 0) {
            return;
        }

        float weight = Float.parseFloat(inputWeight.getText().toString());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        Cursor cursor = database.query(
                WeightTable.WeightEntry.TABLE_WEIGHT,
                null,
                null,
                null,
                null,
                null,
                null
        );
        boolean flag = false;
        int id = 0;
        while (cursor.moveToNext()){
            if ((cursor.getString(cursor.getColumnIndex("date"))).equals(formatter.format(date))) {
                flag = true;
                id = cursor.getInt(cursor.getColumnIndex("_id"));
            }
        }

        if (!flag) {
            ContentValues cv = new ContentValues();
            cv.put(WeightTable.WeightEntry.WEIGHT, weight);
            cv.put(WeightTable.WeightEntry.DATE, formatter.format(date));
            database.insert(WeightTable.WeightEntry.TABLE_WEIGHT, null, cv);
        }
        else {
            ContentValues cv = new ContentValues();
            cv.put(WeightTable.WeightEntry.WEIGHT, weight);
            cv.put(WeightTable.WeightEntry.DATE, formatter.format(date));
            database.update(WeightTable.WeightEntry.TABLE_WEIGHT, cv,
                    WeightTable.WeightEntry._ID + "=" + id,null);
        }
        cursor.close();
    }

    private ArrayList<Entry> weightValues() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Cursor cursor = database.query(
                WeightTable.WeightEntry.TABLE_WEIGHT,
                null,
                null,
                null,
                null,
                null,
                null
        );
        if (cursor.getCount()!=0) {
            ArrayList<Entry> data = new ArrayList<>();
            while (cursor.moveToNext()) {
                Date date = sdf.parse(cursor.getString(cursor.getColumnIndex("date")));
                float weight = cursor.getFloat(cursor.getColumnIndex("weight"));
                long millis = date.getTime();
                data.add(new Entry((float)millis, weight));
            }
            cursor.close();
            return data;
        } else return new ArrayList<>();
    }

    private ArrayList<Entry> averageWeight() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Cursor cursor = database.query(
                WeightTable.WeightEntry.TABLE_WEIGHT,
                null,
                null,
                null,
                null,
                null,
                null);
        Float sum = 0f;
        Float count = (float) cursor.getCount();
        Date minDate = new Date();
        if (cursor.getCount()!=0) {
            while (cursor.moveToNext()) {
                sum = sum + cursor.getFloat(cursor.getColumnIndex("weight"));
                String dateStr = cursor.getString(cursor.getColumnIndex("date"));
                if (minDate.getTime()>formatter.parse(dateStr).getTime())
                    minDate = formatter.parse(dateStr);
            }
            sum=sum/count;
            cursor.close();
            ArrayList<Entry> data = new ArrayList<>();
            Date date = new Date();
            long millis = date.getTime();
            long minDateMillis = minDate.getTime();
            data.add(new Entry((float)minDateMillis,sum));
            data.add(new Entry((float)millis, sum));
            return data;
        }
        else{
            ArrayList<Entry> data = new ArrayList<>();
            Date date = new Date();
            long millis = date.getTime();
            data.add(new Entry((float)millis,0));
            return data;
        }
    }

}