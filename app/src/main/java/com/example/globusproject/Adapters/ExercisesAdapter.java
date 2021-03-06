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

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.globusproject.DBHelper;
import com.example.globusproject.R;

import org.jetbrains.annotations.NotNull;

import com.example.globusproject.Tables.ExercisesTable;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExercisesViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private SQLiteDatabase database;

    public ExercisesAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    class ExercisesViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private ImageView exerciseImage;

        private ExercisesViewHolder(final View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.ex_name_item_training);
            exerciseImage = itemView.findViewById(R.id.ex_image_training);

            DBHelper dbHelper = new DBHelper(mContext);
            database = dbHelper.getWritableDatabase();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    long exercise_id = (long) itemView.getTag();
                    bundle.putLong("ex_id", exercise_id);
                    int program_id = searchProgramId(exercise_id);
                    bundle.putInt("prog_id", program_id);

                    NavController navController = Navigation.findNavController(itemView);
                    navController.navigate(R.id.action_training_to_approach, bundle);
                }
            });

        }

        private int searchProgramId(long id) {
            String query = "select _program_id from " + ExercisesTable.ExercisesEntry.TABLE_EXERCISES + " WHERE _id = " + id;
            Cursor cursor = database.rawQuery(query, null);

            int program_id = 0;
            if (cursor.moveToFirst())
            {
                program_id = cursor.getInt(cursor.getColumnIndex("_program_id"));
            }
            cursor.close();
            return program_id;
        }
    }

    @NotNull
    @Override
    public ExercisesAdapter.ExercisesViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((mContext));
        View view = inflater.inflate(R.layout.ex_item, parent, false);
        return new ExercisesAdapter.ExercisesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ExercisesAdapter.ExercisesViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String name = mCursor.getString(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry.EX_NAME));
        String uri = mCursor.getString(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry.EX_URI));
        long id = mCursor.getLong(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry._ID));

        holder.nameText.setText(name);
        holder.itemView.setTag(id);

        if (!uri.equals("null") && uri.contains(".gif"))
            Glide
                    .with(holder.itemView.getContext())
                    .asGif()
                    .load(uri)
                    .error(R.drawable.ic_sport4)
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
}
