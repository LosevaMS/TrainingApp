package Fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.github.mikephil.charting.data.BarDataSet;

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

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment {

    private SQLiteDatabase database;
    private CompactCalendarView compactCalendar;
    private LineChart weightChart;
    private LineDataSet lineDataSet1 = new LineDataSet(null,null);
    private LineDataSet lineDataSet2 = new LineDataSet(null,null);
    private ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    private LineData lineData;
    private EditText inputWeight;
    private TextView currentWeight, minWeight, maxWeight, bmiIndicator, bmiDescription, paramWeight, paramHeight;
    private ImageView indicator;
    private float bmi;
    private SharedPreferences sPref;
    final String BMI_PREFERENCE = "bmi_pref";
    final String PARAM_WEIGHT = "weight_pref";
    final String PARAM_HEIGHT = "height_pref";

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
        cv.put(WeightTable.WeightEntry.DATE, "2020-04-01");
        database.insert(WeightTable.WeightEntry.TABLE_WEIGHT, null, cv);

        cv = new ContentValues();
        cv.put(WeightTable.WeightEntry.WEIGHT, 46);
        cv.put(WeightTable.WeightEntry.DATE, "2020-04-15");
        database.insert(WeightTable.WeightEntry.TABLE_WEIGHT, null, cv);

        cv = new ContentValues();
        cv.put(WeightTable.WeightEntry.WEIGHT, 43);
        cv.put(WeightTable.WeightEntry.DATE, "2020-04-23");
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
        ImageView addWeightBtn = view.findViewById(R.id.add_weight_btn);
        currentWeight = view.findViewById(R.id.current_weight_text);
        minWeight = view.findViewById(R.id.min_weight_text);
        maxWeight = view.findViewById(R.id.max_weight_text);
        ImageView editParamBtn = view.findViewById(R.id.edit_param_btn);
        indicator = view.findViewById(R.id.indicator);
        bmiIndicator = view.findViewById(R.id.bmi_indicator);
        bmiDescription = view.findViewById(R.id.bmi_description);
        paramWeight = view.findViewById(R.id.param_weight_text);
        paramHeight = view.findViewById(R.id.param_height_text);

        setBMI();
        paramWeight.setText(loadParamWeight());
        paramHeight.setText(loadParamHeight());

        editParamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(requireContext());
                View promptsView = li.inflate(R.layout.dialog_edit_bmi, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(requireContext());

                mDialogBuilder.setView(promptsView);

                final EditText inputWeightBMI = promptsView.findViewById(R.id.input_weight_bmi);
                final EditText inputHeightBMI = promptsView.findViewById(R.id.input_count_bmi);

                inputWeightBMI.setImeActionLabel("", EditorInfo.IME_ACTION_NEXT);

                inputWeightBMI.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT) {
                            if (inputWeightBMI.getText().toString().trim().equalsIgnoreCase(""))
                                inputWeightBMI.setError("Введите вес!");
                            else
                                Toast.makeText(requireContext(), "Notnull", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                inputHeightBMI.setImeActionLabel("", EditorInfo.IME_ACTION_NEXT);

                inputHeightBMI.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            if (inputHeightBMI.getText().toString().trim().equalsIgnoreCase(""))
                                inputHeightBMI.setError("Введите рост!");
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
                                        float weight = Float.parseFloat(inputWeightBMI.getText().toString());
                                        float height = Float.parseFloat(inputHeightBMI.getText().toString());
                                        String weightText = inputWeightBMI.getText().toString().concat(" кг");
                                        String heightText = inputHeightBMI.getText().toString().concat(" см");
                                        paramWeight.setText(weightText);
                                        paramHeight.setText(heightText);
                                        height = height/100;
                                        bmi = weight/(height*height);
                                        saveBMI(bmi, weightText, heightText);
                                        indicator.setTranslationX(35.2f*(bmi-15));
                                        bmiIndicator.setText(String.valueOf(bmi));
                                        bmiIndicator.setTranslationX(35.2f*(bmi-15));
                                        setBMI();
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
                inputWeightBMI.getText().clear();
                inputHeightBMI.getText().clear();
            }
        });


        try {
            currentWeight.setText(getCurrentWeight());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        maxWeight.setText(getMaxWeight());
        minWeight.setText(getMinWeight());

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
                                        try {
                                            currentWeight.setText(getCurrentWeight());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        maxWeight.setText(getMaxWeight());
                                        minWeight.setText(getMinWeight());
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

    private  void setBMI(){
        if (loadBMI()<15){
            indicator.setVisibility(View.GONE);
            bmiIndicator.setText(String.format("%.2f",loadBMI()));
            bmiIndicator.setTranslationX(35.2f * 0);
            bmiDescription.setText("Очень сильное истощение");
           // bmiDescription.setTextColor(Color.parseColor("#304156"));
        }
        if (loadBMI()>=15 && loadBMI()<=16){
            indicator.setVisibility(View.VISIBLE);
            indicator.setTranslationX(35.2f * (loadBMI() - 15));
            bmiIndicator.setText(String.format("%.2f",loadBMI()));
            bmiIndicator.setTranslationX(35.2f * (loadBMI() - 15));
            bmiDescription.setText("Выраженный дефицит массы тела");
          //  bmiDescription.setTextColor(Color.parseColor("#4C6C93"));
        }
        if (loadBMI()>16 && loadBMI()<=18.5){
            indicator.setVisibility(View.VISIBLE);
            indicator.setTranslationX(35.2f * (loadBMI() - 15));
            bmiIndicator.setText(String.format("%.2f",loadBMI()));
            bmiIndicator.setTranslationX(35.2f * (loadBMI() - 15));
            bmiDescription.setText("Недостаточная масса тела");
           // bmiDescription.setTextColor(Color.parseColor("#6DAFFF"));
        }
        if (loadBMI()>18.5 && loadBMI()<=25){
            indicator.setVisibility(View.VISIBLE);
            indicator.setTranslationX(35.2f * (loadBMI() - 15));
            bmiIndicator.setText(String.format("%.2f",loadBMI()));
            bmiIndicator.setTranslationX(35.2f * (loadBMI() - 15));
            bmiDescription.setText("Норма");
           // bmiDescription.setTextColor(Color.parseColor("#74DD78"));
        }
        if (loadBMI()>25 && loadBMI()<=30){
            indicator.setVisibility(View.VISIBLE);
            indicator.setTranslationX(35.2f * (loadBMI() - 15));
            bmiIndicator.setText(String.format("%.2f",loadBMI()));
            bmiIndicator.setTranslationX(35.2f * (loadBMI() - 15));
            bmiDescription.setText("Избыточная масса тела");
          //  bmiDescription.setTextColor(Color.parseColor("#DCE683"));
        }
        if (loadBMI()>30 && loadBMI()<=35){
            indicator.setVisibility(View.VISIBLE);
            indicator.setTranslationX(35.2f * (loadBMI() - 15));
            bmiIndicator.setText(String.format("%.2f",loadBMI()));
            bmiIndicator.setTranslationX(35.2f * (loadBMI() - 15));
            bmiDescription.setText("Ожирение первой степени");
            //bmiDescription.setTextColor(Color.parseColor("#FEB546"));
        }
        if (loadBMI()>35 && loadBMI()<=40){
            indicator.setVisibility(View.VISIBLE);
            indicator.setTranslationX(35.2f * (loadBMI() - 15));
            bmiIndicator.setText(String.format("%.2f",loadBMI()));
            bmiIndicator.setTranslationX(35.2f * (loadBMI() - 15));
            bmiDescription.setText("Ожирение второй степени");
            //bmiDescription.setTextColor(Color.parseColor("#EA444E"));
        }
        if (loadBMI()>40 ){
            indicator.setVisibility(View.GONE);
            bmiIndicator.setText(String.format("%.2f",loadBMI()));
            bmiIndicator.setTranslationX(35.2f * (22));
            bmiDescription.setText("Ожирение третьей степени");
            //bmiDescription.setTextColor(Color.parseColor("#D9242F"));
        }
    }

    private void saveBMI(float bmi, String weight, String height) {
        sPref = requireActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putFloat(BMI_PREFERENCE, bmi);
        ed.putString(PARAM_WEIGHT, weight);
        ed.putString(PARAM_HEIGHT, height);
        ed.apply();
    }
    private float loadBMI() {
        sPref = requireActivity().getPreferences(MODE_PRIVATE);
        return sPref.getFloat(BMI_PREFERENCE, 0f);
    }
    private String loadParamWeight() {
        sPref = requireActivity().getPreferences(MODE_PRIVATE);
        return sPref.getString(PARAM_WEIGHT, "0 кг");
    }
    private String loadParamHeight() {
        sPref = requireActivity().getPreferences(MODE_PRIVATE);
        return sPref.getString(PARAM_HEIGHT, "0 см");
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

    private String getMaxWeight(){
        float max = 0f;
        Cursor cursor = database.query(
                WeightTable.WeightEntry.TABLE_WEIGHT,
                null,
                null,
                null,
                null,
                null,
                null);
        while(cursor.moveToNext()){
            float weight = cursor.getFloat(cursor.getColumnIndex("weight"));
            if (weight > max)
                max = weight;
        }
        cursor.close();
        if(max==0f)
            return "0 кг";
        else {
            String maxWeight = String.valueOf(max);
            maxWeight = maxWeight.concat(" кг");
            return maxWeight;
        }
    }
    private String getMinWeight(){
        float min = 10000f;
        Cursor cursor = database.query(
                WeightTable.WeightEntry.TABLE_WEIGHT,
                null,
                null,
                null,
                null,
                null,
                null);
        while(cursor.moveToNext()){
            float weight = cursor.getFloat(cursor.getColumnIndex("weight"));
            if (weight < min)
                min = weight;
        }
        cursor.close();
        if(min==10000f)
            return "0 кг";
        else {
            String minWeight = String.valueOf(min);
            minWeight = minWeight.concat(" кг");
            return minWeight;
        }
    }
    private String getCurrentWeight() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        float current = 0f;
        Date currentDate = formatter.parse("1999-01-01");
        Cursor cursor = database.query(
                WeightTable.WeightEntry.TABLE_WEIGHT,
                null,
                null,
                null,
                null,
                null,
                null);
        while(cursor.moveToNext()){
            float weight = cursor.getFloat(cursor.getColumnIndex("weight"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            if (formatter.parse(date).getTime() > currentDate.getTime()) {
                currentDate = formatter.parse(date);
                current = weight;
            }
        }
        cursor.close();
        if(formatter.format(currentDate).equals("1999-01-01"))
            return "0 кг";
        else {
            String currentWeight = String.valueOf(current);
            currentWeight = currentWeight.concat(" кг");
            return currentWeight;
        }
    }
}