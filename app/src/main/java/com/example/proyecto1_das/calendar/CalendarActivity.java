package com.example.proyecto1_das.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.proyecto1_das.R;
import com.example.proyecto1_das.data.Routine;
import com.example.proyecto1_das.db.ExternalDB;
import com.example.proyecto1_das.dialog.CalendarDialog;
import com.example.proyecto1_das.dialog.MessageDialog;
import com.example.proyecto1_das.dialog.OptionDialog;
import com.example.proyecto1_das.utils.FileUtils;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener, CalendarDialog.DialogListener, OptionDialog.DialogListener{

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

        FileUtils fileUtils = new FileUtils();
        String mail = fileUtils.readFile(getApplicationContext(),"config.txt");
        String[] keys =  new String[4];
        Object[] params = new String[4];
        keys[0] = "param";
        keys[1] = "mail";
        keys[2] = "month";
        keys[3] = "year";
        params[0] = "findDiaries";
        params[1] = mail;
        params[2] = Integer.toString(selectedDate.getMonthValue());
        params[3] = Integer.toString(selectedDate.getYear());
        Data param = ExternalDB.createParam(keys, params);
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(param).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState() != WorkInfo.State.SUCCEEDED) {
                            MessageDialog d = new MessageDialog("ERROR",
                                    getString(R.string.error_server));
                            d.show(getSupportFragmentManager(), "errorDialog");
                        } else {
                            String[] days = workInfo.getOutputData().getStringArray(
                                    "days");
                            ArrayList<Day> daysInMonth = daysInMonthArray(selectedDate, days);

                            CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
                            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
                            calendarRecyclerView.setLayoutManager(layoutManager);
                            calendarRecyclerView.setAdapter(calendarAdapter);
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);

    }

    private ArrayList<Day> daysInMonthArray(LocalDate date, String[] days) {
        ArrayList<Day> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        List<String> daysArray = Arrays.asList(days);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++) {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add(new Day("", false));
            } else {
                String trueDay = String.valueOf(i - dayOfWeek);
                boolean isRed = daysArray.contains(trueDay);
                daysInMonthArray.add(new Day(trueDay, isRed));
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
            String dateString = createDate(dayText);
            Log.i("TAG", "onItemClick: " + dateString);
            FileUtils fileUtils = new FileUtils();
            String mail = fileUtils.readFile(getApplicationContext(),"config.txt");
            String[] keys =  new String[3];
            Object[] params = new String[3];
            keys[0] = "param";
            keys[1] = "mail";
            keys[2] = "date";
            params[0] = "selectDiary";
            params[1] = mail;
            params[2] = dateString;
            Data param = ExternalDB.createParam(keys, params);
            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(param).build();
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                    .observe(this, workInfo -> {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            if (workInfo.getState() != WorkInfo.State.SUCCEEDED) {
                                MessageDialog d = new MessageDialog("ERROR",
                                        getString(R.string.error_server));
                                d.show(getSupportFragmentManager(), "errorDialog");
                            } else {
                                String[] diary = workInfo.getOutputData().getStringArray(
                                        "diary");
                                if (diary.length == 0) {
                                    createOptions(dayText, view);
                                } else {
                                    OptionDialog optionDialog = new OptionDialog(
                                            getString(R.string.routine_selected) + diary[1],
                                            new CharSequence[] {getString(
                                                    R.string.remove)}, 2,
                                            false,
                                            new String[] {mail, dateString}, view);
                                    optionDialog.setListener(this);
                                    optionDialog.show(getSupportFragmentManager(),
                                            "routineSelectedInfo");
                                }
                            }

                        }
                    });
            WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
        }


    }

    private void createOptions(String dayText, View view) {
        FileUtils fileUtils = new FileUtils();
        String mail = fileUtils.readFile(this, "config.txt");
        String[] keys =  new String[2];
        Object[] params = new String[2];
        keys[0] = "param";
        keys[1] = "mail";
        params[0] = "loadRoutines";
        params[1] = mail;
        Data param = ExternalDB.createParam(keys, params);
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(param).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState() != WorkInfo.State.SUCCEEDED) {
                            MessageDialog d = new MessageDialog("ERROR",
                                    getString(R.string.error_server));
                            d.show(getSupportFragmentManager(), "errorDialog");
                        } else {
                            Data d = workInfo.getOutputData();
                            int size = d.getInt("size", 0);
                            List<Routine> lRoutines = new ArrayList<>();
                            for (int i = 0; i < size; i++) {
                                String[] routineRow = d.getStringArray(Integer.toString(i));
                                Routine r = new Routine();
                                r.setMail(routineRow[0]);
                                r.setName(routineRow[1]);
                                r.setDesc(routineRow[2]);
                                lRoutines.add(r);
                            }
                            CalendarDialog calendarDialog = new CalendarDialog(
                                    getString(R.string.select_routine), dayText, view, lRoutines);
                            calendarDialog.setListener(this);
                            calendarDialog.show(getSupportFragmentManager(),
                                    "routineSelector");
                        }

                    }
                });
        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);

    }

    private String createDate(String dayText) {
        int m = selectedDate.getMonth().getValue();
        int y = selectedDate.getYear();
        return y + "-" + m + "-" + dayText;
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
    public void onRoutineClick(String routine, String day, View assocNumView) {
        FileUtils fileUtils = new FileUtils();
        String date = this.createDate(day);
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
        params[3] = date;
        Data param = ExternalDB.createParam(keys, params);
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(param).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState() != WorkInfo.State.SUCCEEDED) {
                            MessageDialog d = new MessageDialog("ERROR",
                                    getString(R.string.error_server));
                            d.show(getSupportFragmentManager(), "errorDialog");
                        } else {
                            assocNumView.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
    }

    @Override
    public void onDialogRes(String res, View assocNumView, String[] args) {
        if (res.equals("00")) {
            if (ThemeUtils.isLightThemeSet(this)) {
                assocNumView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.background_light));
            } else {
                assocNumView.setBackgroundColor(424242);
            }
        }
    }
}