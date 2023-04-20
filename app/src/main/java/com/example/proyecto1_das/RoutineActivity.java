package com.example.proyecto1_das;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.proyecto1_das.calendar.CalendarActivity;
import com.example.proyecto1_das.data.Routine;
import com.example.proyecto1_das.db.MyDB;
import com.example.proyecto1_das.dialog.OptionDialog;
import com.example.proyecto1_das.exercises.ExerciseActivity;
import com.example.proyecto1_das.utils.FileUtils;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class RoutineActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, OptionDialog.DialogListener {

    private ArrayList<String> lRoutines;
    ActivityResultLauncher<Intent> activityResultLauncher
            = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    addDataToList();
                }
            });
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NotificationManager elManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.initialize(getBaseContext());
        ThemeUtils.changeTheme(this);
        ThemeUtils.changeActionBar(this);
        setContentView(R.layout.activity_routine);

        addDataToList();

        /*
         * Set up hamburger menu
         * Code extracted and adapted from GeeksforGeeks
         * https://www.geeksforgeeks.org/navigation-drawer-in-android/
         */
        DrawerLayout d = findViewById(R.id.my_drawer_layout);

        actionBarDrawerToggle =
                new ActionBarDrawerToggle(
                        this, d, R.string.nav_open, R.string.nav_close);
        actionBarDrawerToggle
                .getDrawerArrowDrawable().setColor(
                        ContextCompat.getColor(this, R.color.white));
        d.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView n = findViewById(R.id.nav_menu);
        n.bringToFront();
        n.setNavigationItemSelectedListener(this);

        // Set up + button action
        FloatingActionButton fButton = findViewById(R.id.floating_button);
        fButton.setOnClickListener(c -> {
            Intent i = new Intent(this, AddRoutineActivity.class);
            activityResultLauncher.launch(i);
        });

        //NOTIFICATIONS
        elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel elCanal =
                new NotificationChannel("pock_rout",
                        "Pocket Routine Notifications",
                        NotificationManager.IMPORTANCE_DEFAULT);
        elCanal.setDescription("Notifications of your routine app");
        elCanal.enableLights(true);
        elCanal.setLightColor(Color.RED);
        elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        elCanal.enableVibration(true);
        elManager.createNotificationChannel(elCanal);

    }

    /*
     * Adds data to the routine list
     * It is called everytime there is a change in the database
     */
    private void addDataToList() {
        FileUtils fUtils = new FileUtils();
        String mail = fUtils.readFile(getApplicationContext(), "config.txt");
        if (!mail.isEmpty()) {
            loadData(mail);
        } else {
            lRoutines = new ArrayList<>();
        }
        ListView lv = findViewById(R.id.lRutinas);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, lRoutines);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(this, ExerciseActivity.class);
            intent.putExtra("RID",
                    ((TextView) view).getText().toString().split(":")[0]);
            startActivity(intent);
        });
        lv.setOnItemLongClickListener((adapterView, view, i, l) -> {
            CharSequence[] options = {getString(R.string.remove)};
            String[] args = {mail, ((TextView) view).getText()
                    .toString().split(":")[1].trim()};
            OptionDialog dialogOption =
                    new OptionDialog(getString(R.string.do_action_menu), options,
                            0, false, args, null);
            dialogOption.setListener(this);
            dialogOption.show(getSupportFragmentManager(), "dialogRoutine");
            return true;
        });
    }

    private void loadData(String mail) {
        MyDB dbManager = new MyDB(this);
        List<Routine> lRoutinesDB = dbManager.loadRoutines(mail);
        lRoutines = new ArrayList<String>();
        for (Routine r : lRoutinesDB) {
            lRoutines.add(r.getId() + ": " + r.getDesc());
        }
        dbManager.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (R.id.nav_settings == item.getItemId()) {
            Intent i = new Intent(this, OptionsActivity.class);
            startActivity(i);
        } else if (R.id.nav_logout == item.getItemId()) {
            boolean success = deleteFile("config.txt");

            if (success) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else if (R.id.nav_calendar == item.getItemId()) {
            Intent i = new Intent(this, CalendarActivity.class);
            startActivity(i);
        }
        return true;
    }

    @Override
    public void onDialogRes(String res, View v) {
        if (res.equals("00")) {
            addDataToList();
            NotificationCompat.Builder elBuilder =
                    new NotificationCompat.Builder(this, "pock_rout");
            elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                    .setContentTitle(getString(R.string.notif_title_alert))
                    .setContentText(getString(R.string.notif_msg_alert))
                    .setSubText(getString(R.string.notif_data_changes))
                    .setVibrate(new long[]{0, 1000, 500, 1000})
                    .setAutoCancel(true);
            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1, elBuilder.build());
        }
    }
}