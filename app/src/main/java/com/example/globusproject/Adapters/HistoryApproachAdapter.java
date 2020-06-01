package com.example.globusproject.Adapters;

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

import org.jetbrains.annotations.NotNull;

import com.example.globusproject.Tables.ApproachesTable;
import com.example.globusproject.Tables.HistoryApproachesTable;
import com.example.globusproject.ViewHolders.HistoryApproachViewHolder;

public class HistoryApproachAdapter extends RecyclerView.Adapter<HistoryApproachViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    public HistoryApproachAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @NotNull
    @Override
    public HistoryApproachViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((mContext));
        View view = inflater.inflate(R.layout.app_item_for_history, parent, false);
        return new HistoryApproachViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull HistoryApproachViewHolder holder, int position) {
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
}