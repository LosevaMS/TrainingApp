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

public class ApproachAdapter extends RecyclerView.Adapter<ApproachAdapter.ApproachViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private OnNoteListener mOnNoteListener;

    public ApproachAdapter(Context context, Cursor cursor, OnNoteListener onNoteListener) {
        mContext = context;
        mCursor = cursor;
        mOnNoteListener = onNoteListener;
    }

    public class ApproachViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView weight, count, number, kgx;
        OnNoteListener onNoteListener;

        public ApproachViewHolder(final View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            weight = itemView.findViewById(R.id.app_item_weight);
            count = itemView.findViewById(R.id.app_item_count);
            number = itemView.findViewById(R.id.number);
            kgx = itemView.findViewById(R.id.kgx);

            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);

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

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    private SQLiteDatabase database;

    @Override
    public ApproachViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((mContext));
        View view = inflater.inflate(R.layout.approach_item, parent, false);
        return new ApproachViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(ApproachViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        double weight = mCursor.getDouble(mCursor.getColumnIndex(ApproachesTable.ApproachesEntry.APP_WEIGHT));
        int count = mCursor.getInt(mCursor.getColumnIndex(ApproachesTable.ApproachesEntry.APP_COUNT));

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

    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}
