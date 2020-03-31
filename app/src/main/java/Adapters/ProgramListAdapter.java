package Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.DBHelper;
import com.example.globusproject.R;


import Tables.ProgramTable;


public class ProgramListAdapter extends RecyclerView.Adapter<ProgramListAdapter.ProgramListViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public ProgramListAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public class ProgramListViewHolder extends RecyclerView.ViewHolder {

        public TextView program_name_item;
        public ImageView delete_item, edit_item, play_item, gym;
        public CardView cardView;


        public ProgramListViewHolder(final View itemView) {
            super(itemView);
            program_name_item = itemView.findViewById(R.id.program_name_item);
            delete_item = itemView.findViewById(R.id.delete_item);
            edit_item = itemView.findViewById(R.id.edit_item);
            play_item = itemView.findViewById(R.id.play_item);
            gym = itemView.findViewById(R.id.gym);
            cardView = itemView.findViewById(R.id.cv);

            DBHelper dbHelper = new DBHelper(mContext);
            database = dbHelper.getWritableDatabase();

            delete_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
                    adb.setMessage("Удалить тренировку?");
                    adb.setPositiveButton("Да", myClickListener);
                    adb.setNegativeButton("Нет", myClickListener);
                    adb.create();
                    adb.show();
                }
            });
            edit_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    long id = (long) itemView.getTag();
                    bundle.putLong("prog_id", id);
                    String name = searchName(id);
                    bundle.putString("prog_name", name);
                    final NavController navController = Navigation.findNavController(itemView);
                    navController.navigate(R.id.action_navigation_list_to_edit_training, bundle);
                }
            });
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    long id = (long) itemView.getTag();
                    bundle.putLong("prog_id", id);
                    String name = searchName(id);
                    bundle.putString("prog_name", name);
                    final NavController navController = Navigation.findNavController(itemView);
                    navController.navigate(R.id.action_navigation_list_to_training, bundle);
                }
            });

        }

        DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE:
                        long id = (long) itemView.getTag();
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

        public String searchName(long id) {
            String query = "select name from " + ProgramTable.ProgramEntry.TABLE_PROGRAMS + " WHERE _id = " + id;
            Cursor c = database.rawQuery(query, null);

            String a = "not found";
            if (c.moveToFirst()) ;
            {
                a = c.getString(c.getColumnIndex("name"));
            }
            c.close();
            return a;
        }

    }

    private SQLiteDatabase database;

    @Override
    public ProgramListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.program_card, parent, false);

        return new ProgramListViewHolder(view);
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
    public void onBindViewHolder(ProgramListViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        long id = mCursor.getLong(mCursor.getColumnIndex(ProgramTable.ProgramEntry._ID));
        String uri = mCursor.getString(mCursor.getColumnIndex(ProgramTable.ProgramEntry.PROG_URI));
        String name = mCursor.getString(mCursor.getColumnIndex(ProgramTable.ProgramEntry.PROG_NAME));

        holder.program_name_item.setText(name);
        holder.itemView.setTag(id);
        holder.gym.setImageURI(Uri.parse(uri));


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
