package Adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;

import Tables.ApproachesTable;
import Tables.HistoryApproachesTable;

public class HistoryApproachAdapter extends RecyclerView.Adapter<HistoryApproachAdapter.HistoryApproachViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    public HistoryApproachAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public class HistoryApproachViewHolder extends RecyclerView.ViewHolder {
        public TextView weight, count, number, kgx;

        public HistoryApproachViewHolder(final View itemView) {
            super(itemView);
            weight = itemView.findViewById(R.id.app_item_weight);
            count = itemView.findViewById(R.id.app_item_count);
            number = itemView.findViewById(R.id.number);
            kgx = itemView.findViewById(R.id.kgx);

            DBHelper dbHelper = new DBHelper(mContext);
            database = dbHelper.getWritableDatabase();

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
    public HistoryApproachViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((mContext));
        View view = inflater.inflate(R.layout.app_item_for_history, parent, false);
        return new HistoryApproachViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryApproachViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        double weight = mCursor.getDouble(mCursor.getColumnIndex(HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_WEIGHT));
        int count = mCursor.getInt(mCursor.getColumnIndex(HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_COUNT));

        holder.weight.setText(String.valueOf(weight));
        holder.count.setText(String.valueOf(count));
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