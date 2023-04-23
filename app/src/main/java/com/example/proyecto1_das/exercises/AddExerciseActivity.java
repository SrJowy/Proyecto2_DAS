package com.example.proyecto1_das.exercises;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto1_das.R;
import com.example.proyecto1_das.data.Exercise;
import com.example.proyecto1_das.db.ExternalDB;
import com.example.proyecto1_das.dialog.MessageDialog;
import com.example.proyecto1_das.utils.FileUtils;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddExerciseActivity extends AppCompatActivity {

    private List<String> lEx;
    private String rName;
    private Boolean[] selectedEx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.initialize(this);
        ThemeUtils.changeTheme(this);
        ThemeUtils.changeActionBar(this);
        setContentView(R.layout.activity_add_exercise);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            rName = bundle.getString("rName");
        }

        String url = "http://" + ExternalDB.getIp() + ":5000/exercise";
        JSONObject requestBody = new JSONObject();
        String lang = LocaleUtils.getLanguage(this);

        try {
            requestBody.put("lang", lang);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                requestBody, response -> {
                    Log.i("AEA", "onCreate: " + response);
                    List<Exercise> lExercises = transformJson(response);
                    Log.i("AEA", "onCreate: " + lExercises.size());
                    checkExercisesInRoutine(rName, lang, lExercises);

                }, error -> Log.e("AEA", "onCreate: ", error));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

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

    /*
     * Check if a routine has exercises
     */
    private void checkExercisesInRoutine(String rName, String lang,
                                                   List<Exercise> lExercises) {
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

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                requestBody, response -> {
                    Log.i("AEA", "checkExercisesInRoutine: " + response);
                    List<Exercise> lExercisesInRoutine = transformJson(response);
                    lEx = new ArrayList<>();

                    List<Exercise> elsToRemove = lExercises.stream()
                            .filter(p1 -> lExercisesInRoutine.stream()
                                    .anyMatch(p2 -> p1.getId() == p2.getId()))
                            .collect(Collectors.toList());

                    lExercises.removeAll(elsToRemove);
                    for (Exercise e : lExercises) {
                        String exName = e.getName();
                        lEx.add(exName);
                    }
                    selectedEx = new Boolean[lEx.size()];
                    Arrays.fill(selectedEx, Boolean.FALSE);

                    ListView lv = findViewById(R.id.exList);
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(this,
                                    android.R.layout.simple_list_item_1, lEx);
                    lv.setAdapter(adapter);
                    lv.setOnItemClickListener((adapterView, view, i, l) -> {
                        if (!selectedEx[i]) {
                            view.setBackgroundColor(getResources()
                                    .getColor(com.google.android.material.R.color.abc_tint_default,
                                            getTheme()));
                            selectedEx[i] = true;
                        } else {
                            view.setBackgroundColor(
                                    getResources().getColor(com.google.android.material.
                                            R.color.cardview_shadow_end_color, getTheme()));
                            selectedEx[i] = false;
                        }

                    });

                    Button b = findViewById(R.id.buttonSaveExercises);
                    b.setOnClickListener(c -> {
                        for (int i = 0; i < lExercises.size(); i++) {
                            if (selectedEx[i]) {
                                insertEjRoutine(rName, mail, lExercises.get(i).getId());

                            }
                        }
                    });

                }, error -> Log.e("AEA", "onCreate: ", error));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

    /*
     * Inserts data into ROUTINE_EXERCISE table
     */
    public void insertEjRoutine(String rName, String mail, Integer exID) {
        String[] keys =  new String[4];
        Object[] params = new String[4];
        keys[0] = "param";
        keys[1] = "mail";
        keys[2] = "rName";
        keys[3] = "exID";
        params[0] = "insertEjRoutine";
        params[1] = mail;
        params[2] = rName;
        params[3] = exID.toString();
        Data param = ExternalDB.createParam(keys, params);
        OneTimeWorkRequest oneTimeWorkRequest =
                new OneTimeWorkRequest.Builder(ExternalDB.class)
                        .setInputData(param).build();
        WorkManager.getInstance(this)
                .getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState() != WorkInfo.State.SUCCEEDED) {
                            MessageDialog d = new MessageDialog("ERROR",
                                    getString(R.string.error_server));
                            d.show(getSupportFragmentManager(), "errorDialog");
                        } else {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
    }
}