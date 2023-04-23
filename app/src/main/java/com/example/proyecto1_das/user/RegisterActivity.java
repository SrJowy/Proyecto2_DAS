package com.example.proyecto1_das.user;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.proyecto1_das.R;
import com.example.proyecto1_das.db.ExternalDB;
import com.example.proyecto1_das.dialog.MessageDialog;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;
import com.example.proyecto1_das.utils.ValidationUtils;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.changeTheme(this);
        ThemeUtils.changeActionBar(this);
        LocaleUtils.initialize(getBaseContext());
        setContentView(R.layout.activity_register);

        Button b = findViewById(R.id.bSend);
        b.setOnClickListener(c -> {
            EditText etName = findViewById(R.id.etName);
            String name = etName.getText().toString();

            EditText etSurname = findViewById(R.id.etSurname);
            String surname = etSurname.getText().toString();

            EditText etMail = findViewById(R.id.etMail);
            String mail = etMail.getText().toString();

            EditText etPassword = findViewById(R.id.etPassword);
            String password = etPassword.getText().toString();

            EditText etRepPassword = findViewById(R.id.etRepeatPass);
            String repeatPass = etRepPassword.getText().toString();

            try {
                // Validate the introduced data
                ValidationUtils.validateUsr(new String[]{name, surname, mail, password,
                        repeatPass});

                String[] keys =  new String[2];
                Object[] params = new String[2];
                keys[0] = "param";
                keys[1] = "mail";
                params[0] = "userExists";
                params[1] = mail;
                Data param = ExternalDB.createParam(keys, params);
                OneTimeWorkRequest oneTimeWorkRequest =
                        new OneTimeWorkRequest.Builder(ExternalDB.class)
                                .setInputData(param).build();
                WorkManager.getInstance(this)
                        .getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                        .observe(this, workInfo -> {
                            if (workInfo != null && workInfo.getState().isFinished()) {
                                int count = workInfo.getOutputData()
                                        .getInt("len", 0);
                                // Check if the user exists
                                if (count == 1) {
                                    MessageDialog d = new MessageDialog("ERROR",
                                            getString(R.string.msg_user_exists));
                                    d.show(getSupportFragmentManager(),
                                            "errorDialog");
                                } else {
                                    // Get the device Firebase token
                                    FirebaseMessaging.getInstance().getToken()
                                            .addOnCompleteListener(task -> {
                                                if (!task.isSuccessful()) {
                                                    Log.e("ERR_TOKEN", "onCreate"
                                                            , task.getException());
                                                    return;
                                                }
                                                String token = task.getResult();
                                                this.saveUser(mail, password, name,
                                                        surname, token);
                                                finish();
                                            });
                                }
                            }
                        });
                WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
            } catch (Exception e) {
                // Catch whatever the error might be when validating data
                String message = "";
                if ("usr".equals(e.getMessage())) {
                    message = getString(R.string.usr_msg);
                } else if ("surname".equals(e.getMessage())) {
                    message = getString(R.string.surname_msg);
                } else if ("mail".equals(e.getMessage())) {
                    message = getString(R.string.mail_msg);
                } else if ("pass".equals(e.getMessage())) {
                    message = getString(R.string.pass_msg);
                } else if ("exists".equals(e.getMessage())) {
                    message = getString(R.string.msg_user_exists);
                }
                MessageDialog d = new MessageDialog("ERROR", message);
                d.show(getSupportFragmentManager(), "errorDialog");
            }
        });
    }

    /*
     * Insert the user into the database values
     */
    private void saveUser(String mail, String password, String name, String surname,
                          String token) {
        String[] keys =  new String[6];
        Object[] params = new String[6];
        keys[0] = "param";
        keys[1] = "usr";
        keys[2] = "pass";
        keys[3] = "name";
        keys[4] = "surname";
        keys[5] = "token";
        params[0] = "signUp";
        params[1] = mail;
        params[2] = password;
        params[3] = name;
        params[4] = surname;
        params[5] = token;

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
                            Toast.makeText(this, getString(R.string.msg_success),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
    }
}