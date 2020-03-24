package Adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;

import Tables.ApproachesTable;
import Tables.ExercisesTable;
import Tables.ProgramTable;

public class ApproachAdapter extends RecyclerView.Adapter<ApproachAdapter.ApproachViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private OnNoteListener mOnNoteListener;

    public ApproachAdapter(Context context, Cursor cursor, OnNoteListener onNoteListener){
        mContext = context;
        mCursor = cursor;
        mOnNoteListener = onNoteListener;
    }

    public class ApproachViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView weight, count;
        OnNoteListener onNoteListener;

        public ApproachViewHolder( final View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            weight = itemView.findViewById(R.id.app_item_weight);
            count = itemView.findViewById(R.id.app_item_count);

            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);

            DBHelper dbHelper = new DBHelper(mContext);
            database = dbHelper.getWritableDatabase();

        }


        private Cursor getAllItems(long prog_id) {
            return database.query(
                    ApproachesTable.ApproachesEntry.TABLE_APPROACHES,
                    null,
                    null,
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
    public ApproachAdapter.ApproachViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((mContext));
        View view = inflater.inflate(R.layout.approach_item, parent, false);
        return new ApproachAdapter.ApproachViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(ApproachAdapter.ApproachViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)){
            return;
        }
        int weight = mCursor.getInt(mCursor.getColumnIndex(ApproachesTable.ApproachesEntry.APP_WEIGHT));
        int count = mCursor.getInt(mCursor.getColumnIndex(ApproachesTable.ApproachesEntry.APP_COUNT));
        long ex_id = mCursor.getLong(mCursor.getColumnIndex(ApproachesTable.ApproachesEntry.APP_EX_ID));
        long prog_id = mCursor.getLong(mCursor.getColumnIndex(ApproachesTable.ApproachesEntry.APP_PROG_ID));
        long date = mCursor.getLong(mCursor.getColumnIndex(ApproachesTable.ApproachesEntry.APP_DATE));

        holder.weight.setText(weight);
        holder.count.setText(count);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if(mCursor != null){
            mCursor.close();
        }
        mCursor = newCursor;

        if(newCursor != null){
            notifyDataSetChanged();
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
