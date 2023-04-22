package com.example.proyecto1_das;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.proyecto1_das.db.ExternalDB;
import com.example.proyecto1_das.dialog.MessageDialog;
import com.example.proyecto1_das.utils.FileUtils;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;

public class AddRoutineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.initialize(this);
        ThemeUtils.changeTheme(this);
        ThemeUtils.changeActionBar(this);
        setContentView(R.layout.activity_add_routine);

        Button b = findViewById(R.id.buttonSaveRoutine);
        b.setOnClickListener(c -> {
            FileUtils fileUtils = new FileUtils();
            String mail = fileUtils.readFile(this, "config.txt");

            EditText etName = findViewById(R.id.editTextRoutineName);
            String rName = etName.getText().toString();

            EditText etDesc = findViewById(R.id.etRoutineDesc);
            String rDesc = etDesc.getText().toString();

            if (!rName.isEmpty() && !rName.isBlank()) {
                String[] keys =  new String[4];
                Object[] params = new String[4];
                keys[0] = "param";
                keys[1] = "mail";
                keys[2] = "rName";
                keys[3] = "rDesc";
                params[0] = "insertRoutine";
                params[1] = mail;
                params[2] = rName;
                params[3] = rDesc;
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
                                    setResult(RESULT_OK);
                                    finish();
                                }

                            }
                        });
                WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
            } else {
                String message = getString(R.string.val_error);
                MessageDialog d = new MessageDialog("ERROR", message);
                d.show(getSupportFragmentManager(), "errorDialog");
            }

        });
    }
}