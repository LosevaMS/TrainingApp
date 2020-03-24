package Adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;

import Tables.ExercisesTable;
import Tables.ProgramTable;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExercisesViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private OnNoteListener mOnNoteListener;

    public ExercisesAdapter(Context context, Cursor cursor, OnNoteListener onNoteListener){
        mContext = context;
        mCursor = cursor;
        mOnNoteListener = onNoteListener;
    }

    public class ExercisesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nameText;
        OnNoteListener onNoteListener;

        public ExercisesViewHolder( final View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            nameText = itemView.findViewById(R.id.ex_name_item);

            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);

            DBHelper dbHelper = new DBHelper(mContext);
            database = dbHelper.getWritableDatabase();

            nameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    long id = (long)itemView.getTag();
                    bundle.putLong("ex_id",id);
                    String name = searchName(id);
                    bundle.putString("ex_name",name);
                    final NavController navController = Navigation.findNavController(itemView);
                    navController.navigate(R.id.action_training_to_approach, bundle);
                }
            });

        }

        public String searchName (long id)
        {
            String query = "select name from " + ExercisesTable.ExercisesEntry.TABLE_EXERCISES + " WHERE _id = " + id;
            Cursor c = database.rawQuery(query , null);

            String a = "not found";
            if (c.moveToFirst());
            {
                a = c.getString(c.getColumnIndex("name"));
            }
            c.close();
            return a;
        }

        public long searchId (long id)
        {
            String query = "select _program_id from " + ExercisesTable.ExercisesEntry.TABLE_EXERCISES + " WHERE _id = " + id;
            Cursor c = database.rawQuery(query , null);

            long a;
            if (c.moveToFirst());
            {
                a = c.getLong(c.getColumnIndex("_program_id"));
            }
            c.close();
            return a;
        }

        private Cursor getAllItems(long prog_id) {
            return database.query(
                    ExercisesTable.ExercisesEntry.TABLE_EXERCISES,
                    null,
                    ExercisesTable.ExercisesEntry.EX_PROG_ID + "=" + prog_id,
                    null,
                    null,
                    null,
                    null
            );
        }
        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    private SQLiteDatabase database;
    @Override
    public ExercisesAdapter.ExercisesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((mContext));
        View view = inflater.inflate(R.layout.ex_item, parent, false);
        return new ExercisesAdapter.ExercisesViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(ExercisesAdapter.ExercisesViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)){
            return;
        }
        String name = mCursor.getString(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry.EX_NAME));
        long id = mCursor.getLong(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry._ID));

        holder.nameText.setText(name);
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if(mCursor != null){
            mCursor.close();
        }
        mCursor = newCursor;

        if(newCursor != null){
            notifyDataSetChanged();
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
