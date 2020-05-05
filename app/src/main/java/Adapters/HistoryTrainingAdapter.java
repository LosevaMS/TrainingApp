package Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;

import Tables.ApproachesTable;
import Tables.ExercisesTable;
import Tables.HistoryTable;

public class HistoryTrainingAdapter extends RecyclerView.Adapter<HistoryTrainingAdapter.HistoryTrainingViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private static ClickListener mClickListener;

    public HistoryTrainingAdapter(Context context, Cursor cursor, ClickListener clickListener1) {
        mContext = context;
        mCursor = cursor;
        mClickListener = clickListener1;
    }

    public class HistoryTrainingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView exerciseName;
        ClickListener clickListener;

        public HistoryTrainingViewHolder(final View itemView, ClickListener clickListener) {
            super(itemView);

            exerciseName = itemView.findViewById(R.id.ex_name_item);

            this.clickListener = clickListener;
            itemView.setOnClickListener(this);

            DBHelper dbHelper = new DBHelper(mContext);
            database = dbHelper.getWritableDatabase();

        }
        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        public int searchProgId(long id) {
            String query = "select _program_id from " + ExercisesTable.ExercisesEntry.TABLE_EXERCISES + " WHERE _id = " + id;
            Cursor c = database.rawQuery(query, null);

            int a = 0;
            if (c.moveToFirst()) ;
            {
                a = c.getInt(c.getColumnIndex("_program_id"));
            }
            c.close();
            return a;
        }

        public String searchDate(long id) {
            String query = "select date from " + HistoryTable.HistoryEntry.TABLE_HISTORY + " WHERE prog_id = " + id;
            Cursor c = database.rawQuery(query, null);

            String a = " ";
            if (c.moveToFirst()) ;
            {
                a = c.getString(c.getColumnIndex("date"));
            }
            c.close();
            return a;
        }


        private Cursor getAllItems(long ex_id) {
            return database.query(
                    ApproachesTable.ApproachesEntry.TABLE_APPROACHES,
                    null,
                    ApproachesTable.ApproachesEntry.APP_EX_ID + "=" + ex_id,
                    null,
                    null,
                    null,
                    null
            );
        }

    }

    private SQLiteDatabase database;

    @Override
    public HistoryTrainingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((mContext));
        View view = inflater.inflate(R.layout.ex_item, parent, false);
        return new HistoryTrainingViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(HistoryTrainingViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String name = mCursor.getString(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry.EX_NAME));
        long id = mCursor.getLong(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry._ID));

        holder.exerciseName.setText(name);
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
    public void setOnItemClickListener(ClickListener clickListener) {
        HistoryTrainingAdapter.mClickListener = clickListener;
    }
    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
