package com.example.proyecto1_das.db;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExternalDB extends Worker {

    public ExternalDB(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String action = getInputData().getString("param");
        assert action != null;
        if (action.equals("signUp")) {
            String dir = "http://192.168.1.150:5000/users/create";
            HttpURLConnection urlConnection = null;

            String mail = getInputData().getString("usr");
            String pass = getInputData().getString("pass");
            String name = getInputData().getString("name");
            String surname = getInputData().getString("surname");

            try {
                URL dest = new URL(dir);
                urlConnection = (HttpURLConnection) dest.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");
                JSONObject paramJson = new JSONObject();
                paramJson.put("mail", mail);
                paramJson.put("pass", pass);
                paramJson.put("name", name);
                paramJson.put("surname", surname);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(paramJson.toString());
                out.close();
                int statusCode = urlConnection.getResponseCode();
                if (statusCode == 200) {
                    BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String line;
                    StringBuilder result = new StringBuilder();
                    while((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    inputStream.close();

                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(result.toString());
                    Log.i("JSON", "doWork: " + json);

                    Boolean success = (Boolean) json.get("success");
                    Data.Builder b = new Data.Builder();
                    return Result.success(b.putBoolean("success", success).build());
                }
            } catch(Exception e) {
                Log.e("EXCEPTION", "doWork: ", e);
                return Result.failure();
            }
        } else if (action.equals("userExists")) {
            String dir = "http://192.168.1.150:5000/users";
            HttpURLConnection urlConnection = null;

            String mail = getInputData().getString("mail");
            Log.i("MAIL", "doWork: " + mail);
            try {
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("mail", mail);
                String params = builder.build().getEncodedQuery();

                dir += "?" + params;
                Log.i("RUI", "doWork: " + dir);
                URL dest = new URL(dir);
                urlConnection = (HttpURLConnection) dest.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setRequestMethod("GET");

                int statusCode = urlConnection.getResponseCode();
                if (statusCode == 200) {
                    BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String line;
                    StringBuilder result = new StringBuilder();
                    while((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    inputStream.close();

                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(result.toString());
                    Log.i("JSON", "doWork: " + json);

                    JSONArray array = (JSONArray) json.get("result");
                    int len = 0;
                    if (array.size() > 0) len = array.size();
                    Log.i("JSON", "doWork: " + len);
                    Data.Builder b = new Data.Builder();
                    return Result.success(b.putInt("len", len).build());
                }
            } catch(Exception e) {
                Log.e("EXCEPTION", "doWork: ", e);
                return Result.failure();
            }
        } else if (action.equals("signIn")) {
            String dir = "http://192.168.1.150:5000/users";
            HttpURLConnection urlConnection = null;

            String mail = getInputData().getString("mail");
            String password = getInputData().getString("pass");
            Log.i("MAIL", "doWork: " + mail + " " + password);
            try {
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("mail", mail).appendQueryParameter("pass", password);
                String params = builder.build().getEncodedQuery();

                dir += "?" + params;
                Log.i("RUI", "doWork: " + dir);
                URL dest = new URL(dir);
                urlConnection = (HttpURLConnection) dest.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setRequestMethod("GET");

                int statusCode = urlConnection.getResponseCode();
                if (statusCode == 200) {
                    BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String line;
                    StringBuilder result = new StringBuilder();
                    while((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    inputStream.close();

                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(result.toString());
                    Log.i("JSON", "doWork: " + json);

                    JSONArray array = (JSONArray) json.get("result");
                    boolean success = false;
                    if (array.size() > 0) success = true;
                    Log.i("JSON", "doWork: " + success);
                    Data.Builder b = new Data.Builder();
                    return Result.success(b.putBoolean("success", success).build());
                }
            } catch(Exception e) {
                Log.e("EXCEPTION", "doWork: ", e);
                return Result.failure();
            }
        }

        return Result.success();
    }

    public static Data createParam(String[] keys, Object[] params) {
        Data.Builder b = new Data.Builder();
        for (int i = 0; i < keys.length; i++) {
            if (params[i] instanceof Integer) {
                b.putInt(keys[i], (Integer) params[i]);
            } else if (params[i] instanceof String) {
                b.putString(keys[i], (String) params[i]);
            }
        }
        return b.build();
    }
}
