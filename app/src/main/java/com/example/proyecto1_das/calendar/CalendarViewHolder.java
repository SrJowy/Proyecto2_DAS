package com.example.proyecto1_das.calendar;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto1_das.R;

/*
 * Code extracted and adapted from StackOverflow (User: callumhilldeveloper)
 * https://github.com/codeWithCal/CalendarTutorialAndroidStudio
 */
public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView dayOfMonth;
    private final CalendarAdapter.OnItemListener onItemListener;
    private boolean isRed;
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener) {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText(), view);
    }

    public boolean isRed() {
        return isRed;
    }

    public void setRed(boolean red) {
        isRed = red;
    }

    public void updateColor(Boolean b) {
        if (b) itemView.setBackgroundColor(ContextCompat.getColor(this.dayOfMonth.getContext(), R.color.red));
    }
}