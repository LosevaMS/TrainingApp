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
import Tables.ProgramTable;

public class HistoryTrainingAdapter extends RecyclerView.Adapter<HistoryTrainingAdapter.HistoryTrainingViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private OnNoteListener mOnNoteListener;

    public HistoryTrainingAdapter(Context context, Cursor cursor, OnNoteListener onNoteListener) {
        mContext = context;
        mCursor = cursor;
        mOnNoteListener = onNoteListener;
    }

    public class HistoryTrainingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView exerciseName;
        OnNoteListener onNoteListener;

        public HistoryTrainingViewHolder(final View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            exerciseName = itemView.findViewById(R.id.ex_name_item);

            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);

            DBHelper dbHelper = new DBHelper(mContext);
            database = dbHelper.getWritableDatabase();

            exerciseName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    long exId = (long) itemView.getTag();
                    bundle.putLong("ex_id", exId);
                    //bundle.putLong("prog_id", searchProgId(exId));
                    bundle.putString("date", searchDate(searchProgId(exId)));

                    final NavController navController = Navigation.findNavController(itemView);
                    navController.navigate(R.id.action_fragment_history_training_to_fragment_history_approach, bundle);
                }
            });
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

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    private SQLiteDatabase database;

    @Override
    public HistoryTrainingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((mContext));
        View view = inflater.inflate(R.layout.ex_item, parent, false);
        return new HistoryTrainingViewHolder(view, mOnNoteListener);
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

    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}
