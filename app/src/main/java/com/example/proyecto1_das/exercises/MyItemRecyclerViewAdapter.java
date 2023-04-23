package com.example.proyecto1_das.exercises;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto1_das.R;
import com.example.proyecto1_das.data.Exercise;
import com.example.proyecto1_das.databinding.FragmentExerciseBinding;
import com.example.proyecto1_das.utils.ThemeUtils;

import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static boolean[] selected;
    private final List<Exercise> mValues;
    private final Context context;

    public MyItemRecyclerViewAdapter(List<Exercise> items, Context context) {
        selected = new boolean[items.size()];
        mValues = items;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(
                FragmentExerciseBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false));

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Drawable svg = ContextCompat.getDrawable(context,
                R.drawable.dumbbell_8_svgrepo_com);
        if (!ThemeUtils.isLightThemeSet(context)) {
            assert svg != null;
            DrawableCompat.setTint(svg, ContextCompat.getColor(context, R.color.white));
        }
        holder.mImgView.setImageDrawable(svg);
        holder.mContentView.setText(mValues.get(position).getName());
        holder.selected = selected;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


}