package com.example.globusproject;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProgramListAdapter extends RecyclerView.Adapter<ProgramListAdapter.ProgramListViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    public ProgramListAdapter(Context context, Cursor cursor){
        mContext=context;
        mCursor=cursor;
    }
    public class ProgramListViewHolder extends RecyclerView.ViewHolder{
        public TextView nameText;
        public ProgramListViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textview_program_item);
        }
    }

    @NonNull
    @Override
    public ProgramListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.program_item,parent,false);
        return new ProgramListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramListViewHolder holder, int position) {
        if(!mCursor.move(position)){
            return;
        }
        String name = mCursor.getString(mCursor.getColumnIndex(DBHelper.ProgramEntry.PROG_NAME));

        holder.nameText.setText(name);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }
    public void swapCursor (Cursor newCursor){
        if (mCursor!=null){
            mCursor.close();
        }
        mCursor = newCursor;
        if(newCursor!=null){
            notifyDataSetChanged();
        }
    }
}
