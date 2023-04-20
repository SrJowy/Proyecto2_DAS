package com.example.proyecto1_das.calendar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto1_das.R;

import java.util.ArrayList;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<Day> daysOfMonth;
    private final OnItemListener onItemListener;


    public CalendarAdapter(ArrayList<Day> daysOfMonth, OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.dayOfMonth.setText(daysOfMonth.get(position).getDay());
        holder.updateColor(daysOfMonth.get(position).isSelected());
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface  OnItemListener {
        void onItemClick(int position, String dayText, View view);
    }
}
