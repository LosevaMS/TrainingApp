package com.example.globusproject.ViewHolders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.globusproject.Adapters.ExerciseListAdapter;
import com.example.globusproject.DBHelper;
import com.example.globusproject.OnExerciseListClickListener;
import com.example.globusproject.R;
import com.example.globusproject.Tables.ExercisesTable;

public class ExerciseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView nameText;
    private ImageView exerciseImage;
    private Button deleteBtn;
    private OnExerciseListClickListener onExerciseListClickListener;

    public ExerciseViewHolder(final View itemView, OnExerciseListClickListener onExerciseListClickListener) {
        super(itemView);
        nameText = itemView.findViewById(R.id.exercise_name_item);
        deleteBtn = itemView.findViewById(R.id.delete_ex_item);
        exerciseImage = itemView.findViewById(R.id.ex_image);

        this.onExerciseListClickListener = onExerciseListClickListener;

        deleteBtn.setOnClickListener(this);

        /*deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
                adb.setMessage("Удалить упражнение?");
                adb.setPositiveButton("Да", myClickListener);
                adb.setNegativeButton("Нет", myClickListener);
                adb.create();
                adb.show();
            }
        });*/
    }

   /* DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
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
        }*/
    //}

    public void bindData(Cursor mCursor){
        String name = mCursor.getString(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry.EX_NAME));
        long id = mCursor.getLong(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry._ID));
        String uri = mCursor.getString(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry.EX_URI));

        nameText.setText(name);
        itemView.setTag(id);

        if (!uri.equals("null") && uri.contains(".gif"))
            Glide
                    .with(itemView.getContext())
                    .asGif()
                    .load(uri)
                    .error(R.drawable.delete)
                    .into(exerciseImage);

        if (!uri.equals("null") && !uri.contains(".gif"))
            Glide.with(itemView.getContext()).load(uri).into(exerciseImage);

        if (uri.equals("null"))
            Glide.with(itemView.getContext()).load(R.drawable.ic_sport4).into(exerciseImage);
    }

    @Override
    public void onClick(View v) {
        onExerciseListClickListener.onClickExercise(getAdapterPosition(),v);
    }
}
