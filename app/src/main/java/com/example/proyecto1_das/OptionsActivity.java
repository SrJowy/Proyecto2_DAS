package com.example.proyecto1_das;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.proyecto1_das.preferences.Preferences;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;
import com.google.android.material.navigation.NavigationView;

public class OptionsActivity extends AppCompatActivity implements
        Preferences.PrefListener, NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.initialize(getBaseContext());
        ThemeUtils.changeTheme(this);
        ThemeUtils.changeActionBar(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_pref,
                new Preferences()).commit();
        setContentView(R.layout.activity_options);


        DrawerLayout d = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, d, R.string.nav_open,
                R.string.nav_close);
        actionBarDrawerToggle.getDrawerArrowDrawable()
                .setColor(ContextCompat.getColor(this, R.color.white));
        d.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView n = findViewById(R.id.nav_menu);
        n.bringToFront();
        n.setNavigationItemSelectedListener(this);
    }

    @Override
    public void changeLang(String lang) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void changeTheme(String theme) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
        if (R.id.nav_home == item.getItemId()) {
            Intent i = new Intent(this, RoutineActivity.class);
            startActivity(i);
        } else if (R.id.nav_logout == item.getItemId()) {
            boolean success = deleteFile("config.txt");

            if (success) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        return true;
    }
}