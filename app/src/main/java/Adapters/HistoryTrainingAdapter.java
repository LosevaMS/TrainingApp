package Adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.globusproject.DBHelper;
import com.example.globusproject.R;

import org.jetbrains.annotations.NotNull;

import Tables.ApproachesTable;
import Tables.HistoryExercisesTable;

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
        private TextView exerciseName;
        private ImageView exerciseImage;
        ClickListener clickListener;

        private HistoryTrainingViewHolder(final View itemView, ClickListener clickListener) {
            super(itemView);

            exerciseName = itemView.findViewById(R.id.ex_name_item_training);
            exerciseImage = itemView.findViewById(R.id.ex_image_training);

            this.clickListener = clickListener;
            itemView.setOnClickListener(this);

            DBHelper dbHelper = new DBHelper(mContext);
            database = dbHelper.getWritableDatabase();

        }
        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
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

    @NotNull
    @Override
    public HistoryTrainingViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((mContext));
        View view = inflater.inflate(R.layout.ex_item, parent, false);
        return new HistoryTrainingViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NotNull HistoryTrainingViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String name = mCursor.getString(mCursor.getColumnIndex(HistoryExercisesTable.HistoryExercisesEntry.HISTORY_EX_NAME));
        String uri = mCursor.getString(mCursor.getColumnIndex(HistoryExercisesTable.HistoryExercisesEntry.HISTORY_EX_URI));
        long id = mCursor.getLong(mCursor.getColumnIndex(HistoryExercisesTable.HistoryExercisesEntry._ID));

        holder.exerciseName.setText(name);
        holder.itemView.setTag(id);

        if(!uri.equals("null") && uri.contains(".gif"))
            Glide
                    .with(holder.itemView.getContext())
                    .asGif()
                    .load(uri)
                    .error(R.drawable.delete)
                    .into(holder.exerciseImage);

        if (!uri.equals("null") && !uri.contains(".gif"))
            Glide.with(holder.itemView.getContext()).load(uri).into(holder.exerciseImage);

        if (uri.equals("null"))
            Glide.with(holder.itemView.getContext()).load(R.drawable.ic_sport4).into(holder.exerciseImage);
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
