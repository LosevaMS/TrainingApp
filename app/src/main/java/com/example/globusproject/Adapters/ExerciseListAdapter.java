package com.example.globusproject.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.globusproject.DBHelper;
import com.example.globusproject.R;

import org.jetbrains.annotations.NotNull;

import com.example.globusproject.Tables.ExercisesTable;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.ExerciseViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public ExerciseListAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private ImageView exerciseImage;

        private ExerciseViewHolder(final View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.exercise_name_item);
            Button deleteBtn = itemView.findViewById(R.id.delete_ex_item);
            exerciseImage = itemView.findViewById(R.id.ex_image);

            DBHelper dbHelper = new DBHelper(mContext);
            database = dbHelper.getWritableDatabase();

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
                    adb.setMessage("Удалить упражнение?");
                    adb.setPositiveButton("Да", myClickListener);
                    adb.setNegativeButton("Нет", myClickListener);
                    adb.create();
                    adb.show();
                }
            });
        }

        DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE:
                        long id = (long) itemView.getTag();
                        database.delete(ExercisesTable.ExercisesEntry.TABLE_EXERCISES,
                                ExercisesTable.ExercisesEntry._ID + "=" + id, null);
                        notifyItemRemoved(getAdapterPosition());
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

    }

    private SQLiteDatabase database;

    @NotNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((mContext));
        View view = inflater.inflate(R.layout.exercise_item, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ExerciseViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String name = mCursor.getString(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry.EX_NAME));
        long id = mCursor.getLong(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry._ID));
        String uri = mCursor.getString(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry.EX_URI));

        holder.nameText.setText(name);
        holder.itemView.setTag(id);

        if (!uri.equals("null") && uri.contains(".gif"))
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
}
