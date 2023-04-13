package com.example.proyecto1_das;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.proyecto1_das.db.MyDB;
import com.example.proyecto1_das.dialog.MessageDialog;
import com.example.proyecto1_das.utils.FileUtils;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;

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

        Button bSignIn = findViewById(R.id.button);
        bSignIn.setOnClickListener(c -> {
            EditText etMail = findViewById(R.id.editTextMail);
            String mail = etMail.getText().toString();
            EditText etPassword = findViewById(R.id.editTextPassword);
            String password = etPassword.getText().toString();

            MyDB dbManager = new MyDB(this);
            boolean exists = dbManager.checkUsr(mail, password);
            dbManager.close();

            if (exists) {
                saveSession(mail);

                Intent i = new Intent(this, RoutineActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            } else {
                MessageDialog d = new MessageDialog("ERROR",
                        getString(R.string.msg_sign_in));
                d.show(getSupportFragmentManager(), "errorDialog");
            }

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