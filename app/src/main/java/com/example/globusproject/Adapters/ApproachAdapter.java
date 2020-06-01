package com.example.globusproject.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.example.globusproject.R;
import org.jetbrains.annotations.NotNull;
import com.example.globusproject.ViewHolders.ApproachViewHolder;

public class ApproachAdapter extends RecyclerView.Adapter<ApproachViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    public ApproachAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @NotNull
    @Override
    public ApproachViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((mContext));
        View view = inflater.inflate(R.layout.approach_item, parent, false);
        return new ApproachViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ApproachViewHolder holder, int position) {
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
