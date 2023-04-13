package com.example.proyecto1_das.exercises;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto1_das.R;
import com.example.proyecto1_das.data.Exercise;
import com.example.proyecto1_das.db.MyDB;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddExerciseActivity extends AppCompatActivity {

    private List<String> lEx;
    private String rID;
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
            rID = bundle.getString("RID");
        }

        MyDB myDB = new MyDB(this);
        String lang = LocaleUtils.getLanguage(this);
        List<Exercise> lExercises = myDB.selectAllExercises(lang);
        myDB.close();

        lEx = new ArrayList<>();
        List<Exercise> lExercisesFiltered =
                checkExercisesInRoutine(rID, lang, lExercises);
        for (Exercise e : lExercisesFiltered) {
            String exName = e.getName();
            lEx.add(exName);
        }
        selectedEx = new Boolean[lEx.size()];
        Arrays.fill(selectedEx, Boolean.FALSE);

        ListView lv = findViewById(R.id.exList);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lEx);
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
            for (int i = 0; i < lExercisesFiltered.size(); i++) {
                if (selectedEx[i]) {
                    MyDB db = new MyDB(this);
                    db.insertEjRoutine(Integer.parseInt(rID),
                            lExercisesFiltered.get(i).getId());
                    db.close();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private List<Exercise> checkExercisesInRoutine(String rID, String lang,
                                                   List<Exercise> lExercises) {
        MyDB db = new MyDB(this);
        List<Exercise> lExercisesInRoutine = db.selectExercisesByRoutineID(rID, lang);
        db.close();
        List<Exercise> elsToRemove = lExercises.stream()
                .filter(p1 -> lExercisesInRoutine.stream()
                        .anyMatch(p2 -> p1.getId() == p2.getId()))
                .collect(Collectors.toList());

        lExercises.removeAll(elsToRemove);
        return lExercises;


    }
}