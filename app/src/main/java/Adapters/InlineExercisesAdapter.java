package Adapters;

import android.app.ActionBar;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.globusproject.InlineExercises;
import com.example.globusproject.R;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

public class InlineExercisesAdapter extends RecyclerView.Adapter implements Filterable {

    private ArrayList<InlineExercises> inlineExercisesList;
    private ArrayList<InlineExercises> inlineExercisesListAll;
    private static ClickListener mClickListener;

    public InlineExercisesAdapter(ArrayList<InlineExercises> inlineExercisesList, ClickListener clickListener1) {
        this.inlineExercisesList = inlineExercisesList;
        inlineExercisesListAll = new ArrayList<>(inlineExercisesList);
        mClickListener = clickListener1;
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
            return new ViewHolderOne(view, mClickListener);
        }

        view = layoutInflater.inflate(R.layout.inline_ex_item, parent, false);
        return new ViewHolderTwo(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

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

            if(inlineExercisesList.get(position).isSelect()){
                viewHolderTwo.onOffImage.setImageResource(R.drawable.add_custom_btn2);
            }else{
                viewHolderTwo.onOffImage.setImageResource(R.drawable.add_custom_btn1);
            }

            viewHolderTwo.toggleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inlineExercisesList.get(position).isSelect()){
                        inlineExercisesList.get(position).setSelect(false);
                        viewHolderTwo.onOffImage.setImageResource(R.drawable.add_custom_btn1);
                    } else{
                        inlineExercisesList.get(position).setSelect(true);
                        viewHolderTwo.onOffImage.setImageResource(R.drawable.add_custom_btn2);
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return inlineExercisesList.size();
    }


    public ArrayList<InlineExercises> getSelected(){
        ArrayList<InlineExercises> selectedExercises = new ArrayList<>();
        for (int i=0; i<inlineExercisesList.size();i++){
            if (inlineExercisesList.get(i).isSelect())
                selectedExercises.add(inlineExercisesList.get(i));
        }
        return selectedExercises;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<InlineExercises> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(inlineExercisesListAll);
            } else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (InlineExercises item : inlineExercisesListAll){
                    if (item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            inlineExercisesList.clear();
            inlineExercisesList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };


    static class ViewHolderOne extends RecyclerView.ViewHolder implements View.OnClickListener {
        ClickListener clickListener;

        TextView headerName;
        ViewHolderOne(@NonNull final View itemView, ClickListener clickListener) {
            super(itemView);
            headerName = itemView.findViewById(R.id.header_name);

            this.clickListener = clickListener;
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    static class ViewHolderTwo extends RecyclerView.ViewHolder implements View.OnClickListener {
        ClickListener clickListener;

        TextView exerciseName;
        ImageView exerciseImage;
        ImageView onOffImage;
        LinearLayout toggleLayout;

        ViewHolderTwo(@NonNull final View itemView, ClickListener clickListener) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.inline_exercise_name);
            exerciseImage = itemView.findViewById(R.id.inline_ex_image);
            onOffImage = itemView.findViewById(R.id.add_inline_btn);
            toggleLayout = itemView.findViewById(R.id.toggle_layout);

            this.clickListener = clickListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
                clickListener.onItemClick(getAdapterPosition(), v);

        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        InlineExercisesAdapter.mClickListener = clickListener;
    }
    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}