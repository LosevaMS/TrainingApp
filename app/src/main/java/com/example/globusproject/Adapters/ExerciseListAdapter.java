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
import com.example.globusproject.OnExerciseListClickListener;
import com.example.globusproject.R;

import org.jetbrains.annotations.NotNull;

import com.example.globusproject.Tables.ExercisesTable;
import com.example.globusproject.ViewHolders.ExerciseViewHolder;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private static OnExerciseListClickListener onExerciseListClickListener;

    public ExerciseListAdapter(Context context, Cursor cursor, OnExerciseListClickListener onExerciseListClickListener) {
        mContext = context;
        mCursor = cursor;
        ExerciseListAdapter.onExerciseListClickListener = onExerciseListClickListener;
    }


    @NotNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((mContext));
        View view = inflater.inflate(R.layout.exercise_item, parent, false);
        return new ExerciseViewHolder(view,onExerciseListClickListener);
    }

    @Override
    public void onBindViewHolder(@NotNull ExerciseViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        holder.bindData(mCursor);

    }

    public void setOnExerciseListClickListener(OnExerciseListClickListener onExerciseListClickListener){
        ExerciseListAdapter.onExerciseListClickListener = onExerciseListClickListener;
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
