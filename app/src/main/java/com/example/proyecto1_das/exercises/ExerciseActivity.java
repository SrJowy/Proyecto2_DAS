package com.example.proyecto1_das.exercises;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.proyecto1_das.MainActivity;
import com.example.proyecto1_das.OptionsActivity;
import com.example.proyecto1_das.R;
import com.example.proyecto1_das.dialog.OptionDialog;
import com.example.proyecto1_das.exercises.fragments.ExerciseDataFragment;
import com.example.proyecto1_das.exercises.fragments.ExerciseFragment;
import com.example.proyecto1_das.utils.FileUtils;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class ExerciseActivity extends AppCompatActivity implements
        MyViewHolder.listenerViewHolder, NavigationView.OnNavigationItemSelectedListener,
        OptionDialog.DialogListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private String rID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.initialize(getBaseContext());
        ThemeUtils.changeTheme(this);
        ThemeUtils.changeActionBar(this);
        setContentView(R.layout.activity_exercise);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            rID = bundle.getString("RID");
            Bundle b = new Bundle();
            b.putString("RID", rID);
            ExerciseFragment eFrag = new ExerciseFragment();
            eFrag.setArguments(b);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, eFrag)
                    .commit();
        }

        DrawerLayout d = findViewById(R.id.my_drawer_layout2);
        actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, d, R.string.nav_open, R.string.nav_close);
        actionBarDrawerToggle.getDrawerArrowDrawable()
                .setColor(ContextCompat.getColor(this, R.color.white));
        d.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ExerciseDataFragment eFragment =
                    (ExerciseDataFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragmentContainerView3);
            getSupportFragmentManager().beginTransaction().hide(eFragment).commit();
        }

        NavigationView n = findViewById(R.id.nav_menu);
        n.bringToFront();
        n.setNavigationItemSelectedListener(this);

        FloatingActionButton fButton = findViewById(R.id.floating_button);
        fButton.setOnClickListener(c -> {
            Intent i = new Intent(this, AddExerciseActivity.class);
            i.putExtra("RID", rID);
            activityResultLauncher.launch(i);
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    reloadFragment();
                }
            }
    );

    private void reloadFragment() {
        ExerciseFragment eFragment = new ExerciseFragment();
        Bundle b = new Bundle();
        b.putString("RID", rID);
        eFragment.setArguments(b);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, eFragment)
                .commit();
    }

    @Override
    public void selectItem(int exID) {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ExerciseDataFragment eFragment =
                    (ExerciseDataFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragmentContainerView3);
            if (!eFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().show(eFragment).commit();
            }
            eFragment.setData2(exID);
        } else {
            Intent i = new Intent(this, ExerciseDataActivity.class);
            i.putExtra("ExID", exID);
            startActivity(i);
        }
    }

    @Override
    public void showActivityInfo(int exID) {
        CharSequence[] options = {getString(R.string.remove)};
        String[] args = {rID, Integer.toString(exID)};
        OptionDialog dialogOption =
                new OptionDialog(getString(R.string.do_action_menu),options, 1, false, args);
        dialogOption.setListener(this);
        dialogOption.show(getSupportFragmentManager(), "dialogExercise");
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

    @Override
    public void onDialogRes(String res) {
        if (res.equals("00")) {
            reloadFragment();
        }
    }
}