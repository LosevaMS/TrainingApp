package com.example.globusproject.ViewHolders;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.globusproject.R;
import com.example.globusproject.Tables.HistoryApproachesTable;

public class HistoryApproachViewHolder extends RecyclerView.ViewHolder {

    private TextView weight;
    private TextView count;

    public HistoryApproachViewHolder(final View itemView) {
        super(itemView);
        weight = itemView.findViewById(R.id.app_item_weight);
        count = itemView.findViewById(R.id.app_item_count);
    }

    public void bindData(Cursor mCursor){
        double mWeight = mCursor.getDouble(mCursor.getColumnIndex(HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_WEIGHT));
        int mCount = mCursor.getInt(mCursor.getColumnIndex(HistoryApproachesTable.HistoryApproachesEntry.HISTORY_APP_COUNT));

        weight.setText(String.valueOf(mWeight));
        count.setText(String.valueOf(mCount));
    }
}
