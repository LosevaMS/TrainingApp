package com.example.globusproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import Tables.ProgramTable;


public class ProgramListAdapter extends RecyclerView.Adapter<ProgramListAdapter.ProgramListViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private OnNoteListener mOnNoteListener;

    public ProgramListAdapter(Context context, Cursor cursor, OnNoteListener onNoteListener){
        mContext = context;
        mCursor = cursor;
        mOnNoteListener = onNoteListener;
    }
    public class ProgramListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //public TextView program_name_item,item_rectangle, delete_item, edit_item, play_item;
        public TextView program_name_item;
        public ImageView delete_item,edit_item,play_item;

        OnNoteListener onNoteListener;

        public ProgramListViewHolder(final View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            program_name_item = itemView.findViewById(R.id.program_name_item);
            //item_rectangle = itemView.findViewById(R.id.item_rectangle);
            delete_item = itemView.findViewById(R.id.delete_item);
            edit_item = itemView.findViewById(R.id.edit_item);
            play_item = itemView.findViewById(R.id.play_item);

            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);

            DBHelper dbHelper = new DBHelper(mContext);
            database = dbHelper.getWritableDatabase();

            delete_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
                    adb.setMessage("Удалить тренировку?");
                    adb.setPositiveButton("Да", myClickListener);
                    adb.setNegativeButton("Нет",myClickListener);
                    adb.create();
                    adb.show();
                }
            });
            edit_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    long id = (long)itemView.getTag();
                    bundle.putLong("prog_id",id);
                    final NavController navController = Navigation.findNavController(itemView);
                    navController.navigate(R.id.action_navigation_list_to_training, bundle);
                }
            });
        }
        DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case Dialog.BUTTON_POSITIVE:
                        long id = (long)itemView.getTag();
                        database.delete(ProgramTable.ProgramEntry.TABLE_PROGRAMS,
                                ProgramTable.ProgramEntry._ID + "=" + id, null);
                        notifyItemRemoved(getAdapterPosition());
                        swapCursor(getAllItems());
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }

    }

    private SQLiteDatabase database;
    @Override
    public ProgramListViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.program_card,parent,false);

        return new ProgramListViewHolder(view,mOnNoteListener);
    }
    private Cursor getAllItems() {
        return database.query(
                ProgramTable.ProgramEntry.TABLE_PROGRAMS,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }


    @Override
    public void onBindViewHolder( ProgramListViewHolder holder, int position) {
        if(!mCursor.moveToPosition(position)){
            return;
        }
        String name = mCursor.getString(mCursor.getColumnIndex(ProgramTable.ProgramEntry.PROG_NAME));
        long id = mCursor.getLong(mCursor.getColumnIndex(ProgramTable.ProgramEntry._ID));

        holder.program_name_item.setText(name);
        holder.itemView.setTag(id);
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
    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
