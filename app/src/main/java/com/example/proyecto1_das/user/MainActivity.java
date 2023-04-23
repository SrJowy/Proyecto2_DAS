package com.example.proyecto1_das.user;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.proyecto1_das.R;
import com.example.proyecto1_das.db.ExternalDB;
import com.example.proyecto1_das.dialog.MessageDialog;
import com.example.proyecto1_das.routines.RoutineActivity;
import com.example.proyecto1_das.utils.FileUtils;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ask user for permission to send him notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new
                        String[]{POST_NOTIFICATIONS}, 11);
            }
        }
        // Initialize theme checking his dark theme preference
        ThemeUtils.initAppTheme(this);
        ThemeUtils.changeTheme(this);
        // Updates de action bar to include a correct color and the name of the app
        ThemeUtils.changeActionBar(this);

        // Check if the user has an active session
        FileUtils fUtils = new FileUtils();
        if (fUtils.sessionExists(getApplicationContext(), "config.txt")) {
            Intent intent = new Intent(getApplicationContext(), RoutineActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        // Initialize user's main language in the app
        LocaleUtils.initAppLang(getBaseContext());
        LocaleUtils.initialize(getBaseContext());
        setContentView(R.layout.activity_main);

        // Check if the user exist in db and sign him in
        Button bSignIn = findViewById(R.id.button);
        bSignIn.setOnClickListener(c -> {
            EditText etMail = findViewById(R.id.editTextMail);
            String mail = etMail.getText().toString();
            EditText etPassword = findViewById(R.id.editTextPassword);
            String password = etPassword.getText().toString();

            String[] keys =  new String[3];
            Object[] params = new String[3];
            keys[0] = "param";
            keys[1] = "mail";
            keys[2] =  "pass";
            params[0] = "signIn";
            params[1] = mail;
            params[2] = password;
            Data param = ExternalDB.createParam(keys, params);
            OneTimeWorkRequest oneTimeWorkRequest =
                    new OneTimeWorkRequest.Builder(ExternalDB.class)
                            .setInputData(param).build();
            WorkManager.getInstance(this)
                    .getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                    .observe(this, workInfo -> {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                                boolean success = workInfo.getOutputData()
                                        .getBoolean("success", false);

                                if (success) {
                                    // Stores the user's e-mail in "config.txt" file
                                    saveSession(mail);

                                    Intent i = new Intent(this,
                                            RoutineActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                } else {
                                    MessageDialog d = new MessageDialog("ERROR",
                                            getString(R.string.msg_sign_in));
                                    d.show(getSupportFragmentManager(), "errorDialog");
                                }
                            } else {
                                MessageDialog d = new MessageDialog("ERROR",
                                        getString(R.string.error_server));
                                d.show(getSupportFragmentManager(), "errorDialog");
                            }

                        }
                    });
            WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);

        });

        Button bRegister = findViewById(R.id.button2);
        bRegister.setOnClickListener(c -> {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        });

    }
    /*
     * Code extracted and adapted from StackOverflow (User: Iarsaars)
     * https://stackoverflow.com/questions/14376807/read-write-string-from-to-a-file-in-android
     */
    private void saveSession(String mail) {
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(openFileOutput("config.txt",
                            Context.MODE_PRIVATE));
            outputStreamWriter.write(mail);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e);
        }
    }
}