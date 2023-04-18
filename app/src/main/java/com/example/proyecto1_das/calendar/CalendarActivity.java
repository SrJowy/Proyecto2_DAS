package com.example.proyecto1_das.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.proyecto1_das.R;
import com.example.proyecto1_das.RoutineActivity;
import com.example.proyecto1_das.db.ExternalDB;
import com.example.proyecto1_das.db.MyDB;
import com.example.proyecto1_das.dialog.CalendarDialog;
import com.example.proyecto1_das.dialog.MessageDialog;
import com.example.proyecto1_das.utils.FileUtils;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener, CalendarDialog.DialogListener{

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.changeTheme(this);
        ThemeUtils.changeActionBar(this);
        ThemeUtils.setBackArrow(this);
        LocaleUtils.initialize(getBaseContext());
        setContentView(R.layout.activity_calendar);
        initWidgets();
        selectedDate = LocalDate.now();
        setMonthView();
    }

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++) {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return  daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public void previousMonthAction(View view) {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, String dayText, View view) {
        if(!dayText.equals("")) {
            CalendarDialog calendarDialog = new CalendarDialog("Select routine", selectedDate);
            calendarDialog.setListener(this);
            calendarDialog.show(getSupportFragmentManager(), "routineSelector");


        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRoutineClick(String routine, LocalDate date) {
        FileUtils fileUtils = new FileUtils();
        String mail = fileUtils.readFile(getApplicationContext(),"config.txt");
        String[] keys =  new String[4];
        Object[] params = new String[4];
        keys[0] = "param";
        keys[1] = "mail";
        keys[2] =  "routine";
        keys[3] = "date";
        params[0] = "routineDate";
        params[1] = mail;
        params[2] = routine;
        params[3] = date.toString();
        Data param = ExternalDB.createParam(keys, params);
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(param).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            boolean success = workInfo.getOutputData().getBoolean("success", false);
                        } else {
                            MessageDialog d = new MessageDialog("ERROR",
                                    getString(R.string.error_server));
                            d.show(getSupportFragmentManager(), "errorDialog");
                        }

                    }
                });
        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
    }
}