package com.example.proyecto1_das.exercises;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto1_das.user.MainActivity;
import com.example.proyecto1_das.preferences.OptionsActivity;
import com.example.proyecto1_das.R;
import com.example.proyecto1_das.calendar.CalendarActivity;
import com.example.proyecto1_das.data.Exercise;
import com.example.proyecto1_das.db.ExternalDB;
import com.example.proyecto1_das.dialog.MessageDialog;
import com.example.proyecto1_das.dialog.OptionDialog;
import com.example.proyecto1_das.exercises.fragments.ExerciseDataFragment;
import com.example.proyecto1_das.exercises.fragments.ExerciseFragment;
import com.example.proyecto1_das.gym.GymFinderActivity;
import com.example.proyecto1_das.utils.FileUtils;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExerciseActivity extends AppCompatActivity implements
        MyViewHolder.listenerViewHolder, NavigationView.OnNavigationItemSelectedListener,
        OptionDialog.DialogListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private String rName;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.initialize(getBaseContext());
        ThemeUtils.changeTheme(this);
        ThemeUtils.changeActionBar(this);
        setContentView(R.layout.activity_exercise);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Log.i("EA", "onCreate: bundle not null");
            rName = bundle.getString("rName");

            String lang = LocaleUtils.getLanguage(this);

            FileUtils fileUtils = new FileUtils();
            String mail = fileUtils.readFile(this, "config.txt");

            String url = "http://" + ExternalDB.getIp() + ":5000/exercise";
            JSONObject requestBody = new JSONObject();

            try {
                requestBody.put("mail", mail);
                requestBody.put("routine_name", rName);
                requestBody.put("lang", lang);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                    response -> {
                        Log.i("EA", "onCreate: " + response);

                        List<Exercise> lExercises = transformJson(response);
                        ExerciseFragment eFrag = new ExerciseFragment();
                        eFrag.setlExercises(lExercises);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainerView, eFrag)
                                .commit();

                    }, error -> Log.e("EA", "onCreate: ", error));

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        } else{
            Log.i("EA", "onCreate: bundle null");
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
            i.putExtra("rName", rName);
            activityResultLauncher.launch(i);
        });

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Intent i = new Intent(this, GymFinderActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(this,
                                getString(R.string.permission_denied),
                                Toast.LENGTH_SHORT).show();
                    }
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

    private List<Exercise> transformJson(JSONObject response) {
        List<Exercise> lEx = new ArrayList<>();
        try {
            JSONArray array = (JSONArray) response.get("result");
            for (int i = 0; i < array.length(); i++) {
                Exercise e = new Exercise();
                JSONObject json = (JSONObject) array.get(i);
                e.setId((Integer) json.get("ID"));
                e.setName((String) json.get("NAME"));
                e.setDes((String) json.get("DES"));
                e.setNumSeries((Integer) json.get("NUM_SERIES"));
                e.setNumReps((Integer) json.get("NUM_REPS"));
                e.setNumKgs(Double.valueOf(json.get("KG").toString()));
                e.setLink((String) json.get("LINK"));
                lEx.add(e);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return lEx;
    }

    private void reloadFragment() {
        String lang = LocaleUtils.getLanguage(this);

        FileUtils fileUtils = new FileUtils();
        String mail = fileUtils.readFile(this, "config.txt");

        String url = "http://" + ExternalDB.getIp() + ":5000/exercise";
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("mail", mail);
            requestBody.put("routine_name", rName);
            requestBody.put("lang", lang);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    Log.i("EA", "onCreate: " + response);

                    List<Exercise> lExercises = transformJson(response);
                    ExerciseFragment eFrag = new ExerciseFragment();
                    eFrag.setlExercises(lExercises);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, eFrag)
                            .commit();

                }, error -> Log.e("EA", "onCreate: ", error));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
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
        FileUtils fileUtils = new FileUtils();
        String mail = fileUtils.readFile(this, "config.txt");

        CharSequence[] options = {getString(R.string.remove)};
        String[] args = {rName, mail, Integer.toString(exID)};
        OptionDialog dialogOption =
                new OptionDialog(getString(R.string.do_action_menu),options, 1, false, args, null);
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

    // Manage the option selected in the hamburger menu
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
        } else if (R.id.nav_gyms == item.getItemId()) {
            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(ACCESS_FINE_LOCATION);
            } else {
                Intent i = new Intent(this, GymFinderActivity.class);
                startActivity(i);
            }
        }
        return true;
    }

    @Override
    public void onDialogRes(String res, View v, String[] args) {
        if (res.equals("02")) {
            String[] keys =  new String[4];
            Object[] params = new String[4];
            keys[0] = "param";
            keys[1] = "name";
            keys[2] = "mail";
            keys[3] = "idEj";
            params[0] = "removeRoutineEx";
            params[1] = args[0];
            params[2] = args[1];
            params[3] = args[2];
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
                                reloadFragment();
                            }
                        }
                    });
            WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
        } else {
            reloadFragment();
        }

    }
}