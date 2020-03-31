package Fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Tables.ApproachesTable;
import Tables.HistoryTable;
import Tables.ProgramTable;


public class ProfileFragment extends Fragment {

    private SQLiteDatabase database;
    private TextView firstDate, trainingNumber, approachesNumber, weightNumber;

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

        firstDate = view.findViewById(R.id.first_date);
        trainingNumber = view.findViewById(R.id.finished_cnt);
        approachesNumber = view.findViewById(R.id.app_item_count);
        weightNumber = view.findViewById(R.id.weight_cnt);

        /*try {
            firstDate.setText(searchFirstDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
*/
       /* ContentValues cv = new ContentValues();
        cv.put(ApproachesTable.ApproachesEntry.APP_PROG_ID, 1);
        cv.put(ApproachesTable.ApproachesEntry.APP_EX_ID, 1);
        cv.put(ApproachesTable.ApproachesEntry.APP_DATE, "2020-02-02");
        cv.put(ApproachesTable.ApproachesEntry.APP_COUNT, 88);
        cv.put(ApproachesTable.ApproachesEntry.APP_WEIGHT, 88);

        database.insert(ApproachesTable.ApproachesEntry.TABLE_APPROACHES, null, cv);

        cv = new ContentValues();
        cv.put(HistoryTable.HistoryEntry.HISTORY_PROG_ID, 1);
        cv.put(HistoryTable.HistoryEntry.HISTORY_PROG_NAME, "Тренировка 1");
        cv.put(HistoryTable.HistoryEntry.HISTORY_DATE, "2020-02-02");

        database.insert(HistoryTable.HistoryEntry.TABLE_HISTORY, null, cv);*/

    }

    public String searchFirstDate() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String query = "select date from " + HistoryTable.HistoryEntry.TABLE_HISTORY;
        Cursor c = database.rawQuery(query, null);

        String a, res;
        res = "error";
        Date date = new Date();
        boolean flag = false;
        while (c.moveToNext()) {
            a = c.getString(c.getColumnIndex("date"));

            if (date.getTime() > formatter.parse(a).getTime()) {
                res = a;
            }
        }
        c.close();
        return res;
    }
}