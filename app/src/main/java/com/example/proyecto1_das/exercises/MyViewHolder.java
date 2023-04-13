package com.example.proyecto1_das.exercises;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto1_das.data.Exercise;
import com.example.proyecto1_das.databinding.FragmentExerciseBinding;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public final TextView mIdView;

    public ImageView mImgView;
    public final TextView mContentView;
    private final listenerViewHolder listener;
    public Exercise mItem;
    public boolean[] selected;

    public MyViewHolder(FragmentExerciseBinding binding) {
        super(binding.getRoot());
        listener = (listenerViewHolder) binding.getRoot().getContext();
        mImgView = binding.imgView;
        mIdView = binding.itemNumber;
        mContentView = binding.content;
        binding.getRoot().setOnClickListener(view -> {
            if (!selected[getAbsoluteAdapterPosition()]) {
                listener.selectItem(mItem.getId());
            }
        });

        binding.getRoot().setOnLongClickListener(view -> {
            listener.showActivityInfo(mItem.getId());
            return true;
        });
    }

    public interface listenerViewHolder {
        void selectItem(int exID);

        void showActivityInfo(int exID);
    }

}
