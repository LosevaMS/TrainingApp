package com.example.globusproject.Adapters;

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

import com.example.globusproject.Tables.ApproachesTable;
import com.example.globusproject.Tables.HistoryExercisesTable;
import com.example.globusproject.ViewHolders.HistoryTrainingViewHolder;

public class HistoryTrainingAdapter extends RecyclerView.Adapter<HistoryTrainingViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private static ClickListener mClickListener;

    public HistoryTrainingAdapter(Context context, Cursor cursor, ClickListener clickListener1) {
        mContext = context;
        mCursor = cursor;
        mClickListener = clickListener1;
    }

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
       holder.bindData(mCursor);
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
