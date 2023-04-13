package com.example.proyecto1_das.exercises;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.proyecto1_das.MainActivity;
import com.example.proyecto1_das.OptionsActivity;
import com.example.proyecto1_das.R;
import com.example.proyecto1_das.exercises.fragments.ExerciseDataFragment;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;
import com.google.android.material.navigation.NavigationView;

public class ExerciseDataActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.initialize(getBaseContext());
        ThemeUtils.changeTheme(this);
        ThemeUtils.changeActionBar(this);
        setContentView(R.layout.activity_exercise_data);

        DrawerLayout d = findViewById(R.id.my_drawer_layout3);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, d, R.string.nav_open,
                R.string.nav_close);
        actionBarDrawerToggle.getDrawerArrowDrawable()
                .setColor(ContextCompat.getColor(this, R.color.white));
        d.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int exID = extras.getInt("ExID");

            ExerciseDataFragment eFragment =
                    (ExerciseDataFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragmentContainerView2);

            eFragment.setData(exID);
        }

        NavigationView n = findViewById(R.id.nav_menu);
        n.bringToFront();
        n.setNavigationItemSelectedListener(this);

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
        }
        return true;
    }
}