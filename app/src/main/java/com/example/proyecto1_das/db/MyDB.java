package com.example.proyecto1_das.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.proyecto1_das.R;
import com.example.proyecto1_das.data.Exercise;
import com.example.proyecto1_das.data.Routine;
import com.example.proyecto1_das.dialog.MessageDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Local database manager class
 */
public class MyDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE_ROUTINES =
            "CREATE TABLE ROUTINES ('ID' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "'MAIL' VARCHAR(255), 'DESCRIPTION' TEXT, FOREIGN KEY (MAIL) " +
                    "REFERENCES USERS(MAIL), UNIQUE (ID, MAIL))";

    private static final String SQL_CREATE_TABLE_USERS =
            "CREATE TABLE USERS ('MAIL' VARCHAR(255) PRIMARY KEY NOT NULL, " +
                    "'PASSWORD' VARCHAR(255), 'NAME' VARCHAR(255), " +
                    "'SURNAME' VARCHAR(255))";

    private static final String SQL_CREATE_TABLE_EX =
            "CREATE TABLE EXERCISES (ID INTEGER NOT NULL,NAME TEXT,DES TEXT," +
                    "NUM_SERIES INTEGER,NUM_REPS INTEGER,KG REAL,LINK TEXT, " +
                    "LANG VARCHAR(2), PRIMARY KEY(ID, NAME))";

    private static final String SQL_CREATE_TABLE_EJS_ROUT =
            "CREATE TABLE ROUTINE_EXERCISE (ID_ROUT INTEGER, ID_EJ INTEGER," +
                    "PRIMARY KEY (ID_ROUT, ID_EJ), FOREIGN KEY (ID_ROUT) " +
                    "REFERENCES ROUTINES(ID), FOREIGN KEY (ID_EJ) REFERENCES " +
                    "EXERCISES(ID))";

    public MyDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_ROUTINES);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_USERS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EX);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EJS_ROUT);
        sqLiteDatabase.execSQL("INSERT INTO EXERCISES " +
                        "(ID, NAME, DES, NUM_SERIES, NUM_REPS, KG, LINK, LANG) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{1, "Press de banca", "", 4, 12, 60.0,
                        "https://musclewiki.com/barbell/male/chest/barbell-bench-press"
                        , "es"});
        sqLiteDatabase.execSQL("INSERT INTO EXERCISES " +
                        "(ID, NAME, DES, NUM_SERIES, NUM_REPS, KG, LINK, LANG) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{2, "Tríceps con cuerda", "Realízalo con una polea", 4, 12,
                        15,
                        "https://musclewiki.com/cables/male/triceps/cable-push-down",
                        "es"});
        sqLiteDatabase.execSQL("INSERT INTO EXERCISES " +
                        "(ID, NAME, DES, NUM_SERIES, NUM_REPS, KG, LINK, LANG) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{3, "Press de banca inclinado 45º",
                        "Lo puedes hacer con barra o mancuernas",
                        4, 10, 15, "https://musclewiki.com/dumbbells/male/chest/" +
                        "dumbbell-incline-bench-press", "es"});
        sqLiteDatabase.execSQL("INSERT INTO EXERCISES " +
                        "(ID, NAME, DES, NUM_SERIES, NUM_REPS, KG, LINK, LANG) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{1, "Bench press", "", 4, 12, 60.0,
                        "https://musclewiki.com/barbell/male/chest/barbell-bench-press",
                        "en"});
        sqLiteDatabase.execSQL("INSERT INTO EXERCISES " +
                        "(ID, NAME, DES, NUM_SERIES, NUM_REPS, KG, LINK, LANG) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{2, "Triceps extension", "Do it using a pulley", 4, 12, 15,
                        "https://musclewiki.com/cables/male/triceps/cable-push-down",
                        "en"});
        sqLiteDatabase.execSQL("INSERT INTO EXERCISES " +
                        "(ID, NAME, DES, NUM_SERIES, NUM_REPS, KG, LINK, LANG) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{3, "Incline bench press", "Do it using dumbbells or a bar",
                        4, 10, 15, "https://musclewiki.com/dumbbells/male/chest/" +
                        "dumbbell-incline-bench-press", "en"});
        sqLiteDatabase.execSQL("INSERT INTO USERS (MAIL, PASSWORD, NAME, SURNAME) " +
                        "VALUES (?, ?, ?, ?)",
                new Object[]{"admin@gmail.com", "admin123", "admin", "admin"});
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_ROUTINES);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_USERS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EX);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EJS_ROUT);
    }

    public void insertRoutine(String mail, String description) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO ROUTINES (MAIL, DESCRIPTION) VALUES (?, ?)";
        db.execSQL(sql, new Object[]{mail, description});
        db.close();
    }

    public void insertEjRoutine(int idRout, int idEj) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO ROUTINE_EXERCISE (ID_ROUT, ID_EJ) VALUES (?, ?)";
        try {
            db.execSQL(sql, new Object[]{idRout, idEj});
        } catch (SQLException e) {
            Log.e("INSERT_ERROR", "insertEjRoutine: Already exists ", e);
        }
        db.close();
    }

    public void insertExercises(int id, String name, String des, int numSeries,
                                int numReps, double kg, String link, String lang) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO EXERCISES (ID, NAME, DES, NUM_SERIES, NUM_REPS, " +
                "KG, LINK, LANG) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            db.execSQL(sql, new Object[]{id, name, des, numSeries, numReps, kg,
                    link, lang});
        } catch (SQLException e) {
            Log.e("INSERT_ERROR", "insertExercises: Already exists ", e);
        }
        db.close();
    }

    public void insertUsr(String usr, String pass, String name, String surname, AppCompatActivity act) {
        String[] keys =  new String[5];
        Object[] params = new String[5];
        keys[0] = "param";
        keys[1] = "usr";
        keys[2] = "pass";
        keys[3] = "name";
        keys[4] = "surname";
        params[0] = "signUp";
        params[1] = usr;
        params[2] = pass;
        params[3] = name;
        params[4] = surname;

        Data param = ExternalDB.createParam(keys, params);
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(param).build();
        WorkManager.getInstance(act).getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                .observe(act, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState() != WorkInfo.State.SUCCEEDED) {
                            MessageDialog d = new MessageDialog("ERROR",
                                    act.getString(R.string.error_server));
                            d.show(act.getSupportFragmentManager(), "errorDialog");
                        }
                    }
                });
        WorkManager.getInstance(act).enqueue(oneTimeWorkRequest);
    }

    public List<Routine> loadRoutines(String mail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM ROUTINES WHERE MAIL = ?";
        Cursor cursor = db.rawQuery(query, new String[]{mail});

        List<Routine> lRoutines = new ArrayList<>();

        while (cursor.moveToNext()) {
            Routine r = new Routine();
            r.setId(cursor.getInt(0));
            r.setMail(cursor.getString(1));
            r.setDesc(cursor.getString(2));
            lRoutines.add(r);
        }
        cursor.close();
        db.close();
        return lRoutines;
    }


    public List<Exercise> selectExercisesByRoutineID(String idRoutine, String lang) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.ID, e.NAME, e.DES, e.NUM_SERIES, e.NUM_REPS, e.KG, " +
                "e.LINK FROM EXERCISES e INNER JOIN ROUTINE_EXERCISE re " +
                "ON e.ID = re.ID_EJ WHERE re.ID_ROUT = ? AND e.LANG = ?";
        Cursor cursor = db.rawQuery(query, new String[]{idRoutine, lang});

        List<Exercise> lEx = new ArrayList<>();

        while (cursor.moveToNext()) {
            Exercise e = new Exercise();
            e.setId(cursor.getInt(0));
            e.setName(cursor.getString(1));
            e.setDes(cursor.getString(2));
            e.setNumSeries(cursor.getInt(3));
            e.setNumReps(cursor.getInt(4));
            e.setNumKgs(cursor.getDouble(5));
            e.setLink(cursor.getString(6));
            lEx.add(e);
        }
        cursor.close();
        db.close();
        return lEx;
    }

    public List<Exercise> selectExerciseByExerciseID(int idEx, String lang) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ID, NAME, DES, NUM_SERIES, NUM_REPS, KG, LINK " +
                "FROM EXERCISES WHERE ID = ? AND LANG = ?";
        Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(idEx), lang});

        List<Exercise> lEx = new ArrayList<>();

        while (cursor.moveToNext()) {
            Exercise e = new Exercise();
            e.setId(cursor.getInt(0));
            e.setName(cursor.getString(1));
            e.setDes(cursor.getString(2));
            e.setNumSeries(cursor.getInt(3));
            e.setNumReps(cursor.getInt(4));
            e.setNumKgs(cursor.getDouble(5));
            e.setLink(cursor.getString(6));
            lEx.add(e);
        }
        cursor.close();
        db.close();
        return lEx;
    }

    public void removeRoutine(String mail, String desc) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM ROUTINES WHERE MAIL = ? AND DESCRIPTION = ?";
        try {
            db.execSQL(sql, new Object[]{mail, desc});
        } catch (SQLException e) {
            Log.e("ERROR_REMOVE", "removeRoutine: Couldn't remove " +
                    "that routine ", e);
        }
        db.close();
    }

    public void removeRoutineEx(int rID, int exID) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM ROUTINE_EXERCISE WHERE ID_ROUT = ? AND ID_EJ = ?";
        try {
            db.execSQL(sql, new Object[]{rID, exID});
        } catch (SQLException e) {
            Log.e("ERROR_REMOVE", "removeRoutine: " +
                    "Couldn't remove that routine ", e);
        }
        db.close();
    }

    public List<Exercise> selectAllExercises(String lang) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ID, NAME, DES, NUM_SERIES, NUM_REPS, KG, LINK " +
                "FROM EXERCISES WHERE LANG = ?";
        Cursor cursor = db.rawQuery(query, new String[]{lang});

        List<Exercise> lEx = new ArrayList<>();

        while (cursor.moveToNext()) {
            Exercise e = new Exercise();
            e.setId(cursor.getInt(0));
            e.setName(cursor.getString(1));
            e.setDes(cursor.getString(2));
            e.setNumSeries(cursor.getInt(3));
            e.setNumReps(cursor.getInt(4));
            e.setNumKgs(cursor.getDouble(5));
            e.setLink(cursor.getString(6));
            lEx.add(e);
        }
        cursor.close();
        db.close();
        return lEx;
    }

    public List<Routine> selectRoutines(String mail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM ROUTINES WHERE MAIL = ?";
        Cursor cursor = db.rawQuery(query, new String[]{mail});

        List<Routine> lRoutines = new ArrayList<>();

        while (cursor.moveToNext()) {
            Routine r = new Routine();
            r.setId(cursor.getInt(0));
            r.setMail(cursor.getString(1));
            r.setDesc(cursor.getString(2));
            lRoutines.add(r);
        }
        cursor.close();
        db.close();
        return lRoutines;
    }

}
