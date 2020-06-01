package com.example.globusproject.ViewHolders;

import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.globusproject.Adapters.HistoryTrainingAdapter;
import com.example.globusproject.R;
import com.example.globusproject.Tables.HistoryExercisesTable;

public class HistoryTrainingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView exerciseName;
    private ImageView exerciseImage;
    HistoryTrainingAdapter.ClickListener clickListener;

    public HistoryTrainingViewHolder(final View itemView, HistoryTrainingAdapter.ClickListener clickListener) {
        super(itemView);

        exerciseName = itemView.findViewById(R.id.ex_name_item_training);
        exerciseImage = itemView.findViewById(R.id.ex_image_training);

        this.clickListener = clickListener;
        itemView.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        clickListener.onItemClick(getAdapterPosition(), v);
    }

    public void bindData(Cursor mCursor){
        String name = mCursor.getString(mCursor.getColumnIndex(HistoryExercisesTable.HistoryExercisesEntry.HISTORY_EX_NAME));
        String uri = mCursor.getString(mCursor.getColumnIndex(HistoryExercisesTable.HistoryExercisesEntry.HISTORY_EX_URI));
        long id = mCursor.getLong(mCursor.getColumnIndex(HistoryExercisesTable.HistoryExercisesEntry._ID));

        exerciseName.setText(name);
        itemView.setTag(id);

        if(!uri.equals("null") && uri.contains(".gif"))
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
}
