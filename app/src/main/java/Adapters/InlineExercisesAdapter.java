package Adapters;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.globusproject.InlineExercises;
import com.example.globusproject.R;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

public class InlineExercisesAdapter extends RecyclerView.Adapter {

    ArrayList<InlineExercises> inlineExercisesList;

    public InlineExercisesAdapter(ArrayList<InlineExercises> inlineExercisesList) {
        this.inlineExercisesList = inlineExercisesList;
    }

    @Override
    public int getItemViewType(int position) {
        if (inlineExercisesList.get(position).getUri()==null) {
            return 0; //header
        }
        return 1; //item
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == 0) {
            view = layoutInflater.inflate(R.layout.header_item, parent, false);
            return new ViewHolderOne(view);
        }

        view = layoutInflater.inflate(R.layout.inline_ex_item, parent, false);
        return new ViewHolderTwo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (inlineExercisesList.get(position).getUri()==null) {
            ViewHolderOne viewHolderOne = (ViewHolderOne) holder;
            viewHolderOne.headerName.setText(inlineExercisesList.get(position).getName());
        }else {
            final ViewHolderTwo viewHolderTwo = (ViewHolderTwo) holder;
            viewHolderTwo.exerciseName.setText(inlineExercisesList.get(position).getName());
            Glide
                    .with(viewHolderTwo.itemView.getContext())
                    .asGif()
                    .load(inlineExercisesList.get(position).getUri())
                    .error(R.drawable.delete)
                    .into(viewHolderTwo.exerciseImage);
        }
    }

    @Override
    public int getItemCount() {
        return inlineExercisesList.size();
    }

    class ViewHolderOne extends RecyclerView.ViewHolder {

        TextView headerName;
        public ViewHolderOne(@NonNull View itemView) {
            super(itemView);
            headerName = itemView.findViewById(R.id.header_name);

        }
    }

    class ViewHolderTwo extends RecyclerView.ViewHolder {

        TextView exerciseName;
        ImageView exerciseImage;
        Boolean checked = false;
        ToggleButton addInlineToggleBtn;
        LinearLayout toggleLayout;

        public ViewHolderTwo(@NonNull final View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.inline_exercise_name);
            exerciseImage = itemView.findViewById(R.id.inline_ex_image);
            addInlineToggleBtn = itemView.findViewById(R.id.add_inline_btn);
            toggleLayout = itemView.findViewById(R.id.toggle_layout);

            toggleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checked){
                        checked = false;
                        addInlineToggleBtn.setChecked(false);
                    } else{
                        checked = true;
                        addInlineToggleBtn.setChecked(true);
                    }
                }
            });
        }
    }
}