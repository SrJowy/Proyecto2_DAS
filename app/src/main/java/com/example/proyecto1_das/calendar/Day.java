package com.example.proyecto1_das.calendar;

public class Day {
    private String day;
    private boolean isSelected;

    public Day (String day, boolean isSelected) {
        this.day = day;
        this.isSelected = isSelected;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
