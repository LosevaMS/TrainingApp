package com.example.globusproject.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.globusproject.DBHelper;
import com.example.globusproject.R;

import org.jetbrains.annotations.NotNull;

import com.example.globusproject.Tables.HistoryTable;


public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.HistoryListViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public HistoryListAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    class HistoryListViewHolder extends RecyclerView.ViewHolder {

        private TextView program_name_item, date, time;
        private ImageView gym;

        private HistoryListViewHolder(final View itemView) {
            super(itemView);
            program_name_item = itemView.findViewById(R.id.program_name_item_history);
            date = itemView.findViewById(R.id.date_history);
            time = itemView.findViewById(R.id.time_history);
            CardView cardView = itemView.findViewById(R.id.cv_history);
            gym = itemView.findViewById(R.id.gym);

            DBHelper dbHelper = new DBHelper(mContext);
            database = dbHelper.getWritableDatabase();

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    long id = (long) itemView.getTag();

                    bundle.putLong("prog_id", searchProgId(id));
                    bundle.putString("date", searchDate(id));

                    NavController navController = Navigation.findNavController(itemView);
                    navController.navigate(R.id.action_navigation_history_to_fragment_history_training, bundle);
                }
            });
        }

        private int searchProgId(long id) {
            String query = "select prog_id from " + HistoryTable.HistoryEntry.TABLE_HISTORY + " WHERE _id = " + id;
            Cursor cursor = database.rawQuery(query, null);

            int program_id = 0;
            if (cursor.moveToFirst()) {
                program_id = cursor.getInt(cursor.getColumnIndex("prog_id"));
            }
            cursor.close();
            return program_id;
        }

        private String searchDate(long id) {
            String query = "select date from " + HistoryTable.HistoryEntry.TABLE_HISTORY + " WHERE _id = " + id;
            Cursor cursor = database.rawQuery(query, null);

            String dateString = " ";
            if (cursor.moveToFirst()) {
                dateString = cursor.getString(cursor.getColumnIndex("date"));
            }
            cursor.close();
            return dateString;
        }
    }

    private SQLiteDatabase database;

    @NotNull
    @Override
    public HistoryListViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.history_card, parent, false);

        return new HistoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull HistoryListViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String prog_name = mCursor.getString(mCursor.getColumnIndex(HistoryTable.HistoryEntry.HISTORY_PROG_NAME));
        long id = mCursor.getLong(mCursor.getColumnIndex(HistoryTable.HistoryEntry._ID));
        String date = mCursor.getString(mCursor.getColumnIndex(HistoryTable.HistoryEntry.HISTORY_DATE));
        String time = mCursor.getString(mCursor.getColumnIndex(HistoryTable.HistoryEntry.HISTORY_TIME)) + " мин";
        String uri = mCursor.getString(mCursor.getColumnIndex(HistoryTable.HistoryEntry.HISTORY_URI));

        holder.program_name_item.setText(prog_name);
        holder.date.setText(date);
        holder.time.setText(time);
        holder.itemView.setTag(id);

        if (uri.equals("null"))
            holder.gym.setImageResource(R.drawable.gym4app6);
        else
            Glide.with(holder.itemView.getContext()).load(uri).into(holder.gym);
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
}