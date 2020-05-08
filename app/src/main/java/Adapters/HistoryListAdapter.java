package Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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

import java.util.ArrayList;

import Tables.ApproachesTable;
import Tables.HistoryTable;
import Tables.ProgramTable;


public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.HistoryListViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private OnNoteListener mOnNoteListener;

    public HistoryListAdapter(Context context, Cursor cursor, OnNoteListener onNoteListener) {
        mContext = context;
        mCursor = cursor;
        mOnNoteListener = onNoteListener;
    }

    public class HistoryListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView program_name_item, date, time;
        public CardView cardView;
        public ImageView gym;

        OnNoteListener onNoteListener;

        public HistoryListViewHolder(final View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            program_name_item = itemView.findViewById(R.id.program_name_item_history);
            date = itemView.findViewById(R.id.date_history);
            time = itemView.findViewById(R.id.time_history);
            cardView = itemView.findViewById(R.id.cv_history);
            gym = itemView.findViewById(R.id.gym);

            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);

            DBHelper dbHelper = new DBHelper(mContext);
            database = dbHelper.getWritableDatabase();


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    long id = (long) itemView.getTag();

                    bundle.putLong("prog_id", searchProgId(id));
                    bundle.putString("date", searchDate(id));
                    //bundle.putIntegerArrayList("ex_id",searchExId(id));

                    final NavController navController = Navigation.findNavController(itemView);
                    navController.navigate(R.id.action_navigation_history_to_fragment_history_training, bundle);
                }
            });

        }


        public int searchProgId(long id) {
            String query = "select prog_id from " + HistoryTable.HistoryEntry.TABLE_HISTORY + " WHERE _id = " + id;
            Cursor c = database.rawQuery(query, null);

            int a = 0;
            if (c.moveToFirst())
            {
                a = c.getInt(c.getColumnIndex("prog_id"));
            }
            c.close();
            return a;
        }

        public ArrayList<Integer> searchExId(long id) {
            String query = "select ex_id from " + HistoryTable.HistoryEntry.TABLE_HISTORY + " WHERE _id = " + id;
            Cursor c = database.rawQuery(query, null);

            ArrayList a = new ArrayList();
            if (c.moveToNext())
            {
                a.add(c.getInt(c.getColumnIndex("ex_id")));
            }
            c.close();
            return a;
        }

        public String searchDate(long id) {
            String query = "select date from " + HistoryTable.HistoryEntry.TABLE_HISTORY + " WHERE _id = " + id;
            Cursor c = database.rawQuery(query, null);

            String a = " ";
            if (c.moveToFirst())
            {
                a = c.getString(c.getColumnIndex("date"));
            }
            c.close();
            return a;
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }

    }

    private SQLiteDatabase database;

    @Override
    public HistoryListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.history_card, parent, false);

        return new HistoryListViewHolder(view, mOnNoteListener);
    }

    private Cursor getAllItems() {
        return database.query(
                HistoryTable.HistoryEntry.TABLE_HISTORY,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }


    @Override
    public void onBindViewHolder(HistoryListViewHolder holder, int position) {
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

        if(uri.equals("null"))
            holder.gym.setImageResource(R.drawable.gym4app6);
        else
        Glide.with(holder.itemView.getContext()).load(uri).into(holder.gym);

    }

   /* public String searchUri(long id) {
        String query = "select uri from " + ProgramTable.ProgramEntry.TABLE_PROGRAMS + " WHERE _id = " + id;
        Cursor c = database.rawQuery(query, null);

        String a = "not found";
        if (c.moveToFirst()) ;
        {
            a = c.getString(c.getColumnIndex("uri"));
        }
        c.close();
        return a;
    }*/

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