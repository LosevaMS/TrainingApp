package Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.globusproject.DBHelper;
import com.example.globusproject.R;

import Tables.ExercisesTable;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.ExerciseViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public ExerciseListAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText;
        public Button deleteBtn;
        public ImageView exerciseImage;

        public ExerciseViewHolder( final View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.exercise_name_item);
            deleteBtn = itemView.findViewById(R.id.delete_ex_item);
            exerciseImage = itemView.findViewById(R.id.ex_image);

            DBHelper dbHelper = new DBHelper(mContext);
            database = dbHelper.getWritableDatabase();

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
                    adb.setMessage("Удалить упражнение?");
                    adb.setPositiveButton("Да", myClickListener);
                    adb.setNegativeButton("Нет",myClickListener);
                    adb.create();
                    adb.show();
                }
            });
        }

        DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case Dialog.BUTTON_POSITIVE:
                        long id = (long)itemView.getTag();
                        long prog_id = searchId(id);
                        database.delete(ExercisesTable.ExercisesEntry.TABLE_EXERCISES,
                                ExercisesTable.ExercisesEntry._ID + "=" + id, null);
                        notifyItemRemoved(getAdapterPosition());
                        swapCursor(getAllItems(prog_id));
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

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
    }

    private SQLiteDatabase database;
    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((mContext));
        View view = inflater.inflate(R.layout.exercise_item, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)){
            return;
        }
        String name = mCursor.getString(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry.EX_NAME));
        long id = mCursor.getLong(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry._ID));
        String uri = mCursor.getString(mCursor.getColumnIndex(ExercisesTable.ExercisesEntry.EX_URI));

        holder.nameText.setText(name);
        holder.itemView.setTag(id);

        if(!uri.equals("null") && uri.contains(".gif"))
            Glide
                    .with(holder.itemView.getContext())
                    .asGif()
                    .load(uri)
                    .error(R.drawable.delete)
                    .into(holder.exerciseImage);

        if (!uri.equals("null") && !uri.contains(".gif"))
            Glide.with(holder.itemView.getContext()).load(uri).into(holder.exerciseImage);
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
}
