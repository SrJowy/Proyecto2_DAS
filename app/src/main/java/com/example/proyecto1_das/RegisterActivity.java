package com.example.proyecto1_das;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto1_das.db.MyDB;
import com.example.proyecto1_das.dialog.MessageDialog;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;
import com.example.proyecto1_das.utils.ValidationUtils;

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
                ValidationUtils.validateUsr(new String[]{name, surname, mail, password,
                        repeatPass});
                MyDB myDB = new MyDB(this);
                if (myDB.userExistInDB(mail) == 1) {
                    throw new Exception("exists");
                }
                myDB.insertUsr(mail, password, name, surname);
                myDB.close();
                Toast.makeText(this, getString(R.string.msg_success),
                        Toast.LENGTH_LONG).show();
                finish();
            } catch (Exception e) {
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
}