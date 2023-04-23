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
import java.util.ArrayList;

/*
 *  Manages the connection to the backend
 */
public class ExternalDB extends Worker {

    private static final String IP = "161.35.34.173";

    public static String getIp() {
        return IP;
    }

    public ExternalDB(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String action = getInputData().getString("param");
        assert action != null;
        switch (action) {
            case "signUp": {

                /*
                 *  HTTP Request to insert a user into USERS table
                 */

                String dir = "http://" + IP + ":5000/users/create";
                HttpURLConnection urlConnection;

                String mail = getInputData().getString("usr");
                String pass = getInputData().getString("pass");
                String name = getInputData().getString("name");
                String surname = getInputData().getString("surname");
                String token = getInputData().getString("token");

                try {
                    URL dest = new URL(dir);
                    urlConnection = (HttpURLConnection) dest.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    JSONObject paramJson = new JSONObject();
                    paramJson.put("mail", mail);
                    paramJson.put("pass", pass);
                    paramJson.put("name", name);
                    paramJson.put("surname", surname);
                    paramJson.put("token", token);
                    PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                    out.print(paramJson.toString());
                    out.close();
                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == 200) {
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
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
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }
            case "userExists": {

                /*
                 *  HTTP Request to check if a user exists
                 */

                String dir = "http://" + IP + ":5000/users";
                HttpURLConnection urlConnection = null;

                String mail = getInputData().getString("mail");
                Log.i("MAIL", "doWork: " + mail);
                try {
                    Uri.Builder builder = new Uri.Builder().appendQueryParameter("mail"
                            , mail);
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
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
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
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }
            case "signIn": {

                /*
                 *  HTTP Request to retrieve data from USERS table
                 */

                String dir = "http://" + IP + ":5000/users";
                HttpURLConnection urlConnection = null;

                String mail = getInputData().getString("mail");
                String password = getInputData().getString("pass");
                Log.i("MAIL", "doWork: " + mail + " " + password);
                try {
                    Uri.Builder builder = new Uri.Builder().appendQueryParameter("mail"
                            , mail).appendQueryParameter("pass", password);
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
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
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
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }
            case "routineDate": {

                /*
                 *  HTTP Request to insert data in DIARY table
                 */

                String dir = "http://" + IP + ":5000/diary/create";
                HttpURLConnection urlConnection = null;

                String mail = getInputData().getString("mail");
                String routine = getInputData().getString("routine");
                String date = getInputData().getString("date");

                try {
                    URL dest = new URL(dir);
                    urlConnection = (HttpURLConnection) dest.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    JSONObject paramJson = new JSONObject();
                    paramJson.put("mail", mail);
                    paramJson.put("routine", routine);
                    paramJson.put("date", date);
                    Log.i("TAG", "doWork: " + date);
                    PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                    out.print(paramJson);
                    out.close();
                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == 200) {
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
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
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }
            case "selectDiary": {

                /*
                 *  HTTP Request to retrieve data from DIARY table
                 */

                String dir = "http://" + IP + ":5000/diary";
                HttpURLConnection urlConnection = null;

                String mail = getInputData().getString("mail");
                String date = getInputData().getString("date");
                try {
                    Uri.Builder builder = new Uri.Builder().appendQueryParameter("mail"
                            , mail).appendQueryParameter("date", date);
                    String params = builder.build().getEncodedQuery();

                    dir += "?" + params;

                    URL dest = new URL(dir);
                    urlConnection = (HttpURLConnection) dest.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("GET");

                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == 200) {
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            result.append(line);
                        }
                        inputStream.close();

                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(result.toString());

                        JSONArray array = (JSONArray) json.get("result");
                        String[] diary = new String[0];
                        for (Object o : array) {
                            diary = new String[3];
                            JSONObject jsonObject = (JSONObject) o;
                            diary[0] = (String) jsonObject.get("MAIL");
                            diary[1] = (String) jsonObject.get("ROUTINE");
                            diary[2] = (String) jsonObject.get("DATE_ROUTINE");
                        }
                        Data.Builder b = new Data.Builder();
                        return Result.success(b.putStringArray("diary", diary).build());
                    }
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }
            case "removeDiary": {

                /*
                 *  HTTP Request to remove data from DIARY table
                 */

                String dir = "http://" + IP + ":5000/diary";
                HttpURLConnection urlConnection = null;

                String mail = getInputData().getString("mail");
                String date = getInputData().getString("date");
                try {
                    Uri.Builder builder = new Uri.Builder().appendQueryParameter("mail"
                            , mail).appendQueryParameter("date", date);
                    String params = builder.build().getEncodedQuery();

                    dir += "?" + params;

                    URL dest = new URL(dir);
                    urlConnection = (HttpURLConnection) dest.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("DELETE");

                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == 200) {
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            result.append(line);
                        }
                        inputStream.close();

                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(result.toString());

                        Boolean success = (Boolean) json.get("success");
                        Data.Builder b = new Data.Builder();
                        return Result.success(b.putBoolean("success", success).build());
                    }
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }
            case "findDiaries": {

                /*
                 *  HTTP Request to retrieve data from DIARY table
                 */

                String dir = "http://" + IP + ":5000/diary";
                HttpURLConnection urlConnection = null;

                String mail = getInputData().getString("mail");
                String month = getInputData().getString("month");
                String year = getInputData().getString("year");
                try {
                    Uri.Builder builder = new Uri.Builder().appendQueryParameter("mail"
                                    , mail)
                            .appendQueryParameter("month", month)
                            .appendQueryParameter("year", year);
                    String params = builder.build().getEncodedQuery();

                    dir += "?" + params;

                    URL dest = new URL(dir);
                    urlConnection = (HttpURLConnection) dest.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("GET");

                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == 200) {
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            result.append(line);
                        }
                        inputStream.close();

                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(result.toString());
                        JSONArray array = (JSONArray) json.get("result");
                        ArrayList<String> days = new ArrayList<>();
                        for (Object o : array) {
                            JSONObject jsonObject = (JSONObject) o;
                            String date = (String) jsonObject.get("DATE_F");
                            String day = date.substring(8, 10);
                            if (day.charAt(0) == '0')
                                day = Character.toString(day.charAt(1));
                            days.add(day);
                        }

                        String[] stringArray = days.toArray(new String[days.size()]);
                        Data.Builder b = new Data.Builder();
                        return Result.success(b.putStringArray("days", stringArray).build());
                    }
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }
            case "insertRoutine": {

                /*
                 *  HTTP Request to insert data in ROUTINE table
                 */

                String dir = "http://" + IP + ":5000/routine/create";
                HttpURLConnection urlConnection = null;

                String mail = getInputData().getString("mail");
                String rName = getInputData().getString("rName");
                String rDesc = getInputData().getString("rDesc");
                try {
                    URL dest = new URL(dir);
                    urlConnection = (HttpURLConnection) dest.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    JSONObject paramJson = new JSONObject();
                    paramJson.put("mail", mail);
                    paramJson.put("routine_name", rName);
                    paramJson.put("routine_desc", rDesc);
                    PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                    out.print(paramJson);
                    out.close();
                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == 200) {
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            result.append(line);
                        }
                        inputStream.close();

                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(result.toString());

                        Boolean success = (Boolean) json.get("success");
                        Data.Builder b = new Data.Builder();
                        return Result.success(b.putBoolean("success", success).build());
                    }
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }
            case "loadRoutines": {

                /*
                 *  HTTP Request to retrieve data from ROUTINE table
                 */

                String dir = "http://" + IP + ":5000/routine";
                HttpURLConnection urlConnection = null;

                String mail = getInputData().getString("mail");

                try {
                    Uri.Builder builder = new Uri.Builder().appendQueryParameter("mail"
                            , mail);
                    String params = builder.build().getEncodedQuery();

                    dir += "?" + params;

                    URL dest = new URL(dir);
                    urlConnection = (HttpURLConnection) dest.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("GET");

                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == 200) {
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            result.append(line);
                        }
                        inputStream.close();

                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(result.toString());
                        JSONArray array = (JSONArray) json.get("result");
                        ArrayList<String> days = new ArrayList<>();
                        Data.Builder b = new Data.Builder();
                        int i = 0;
                        for (Object o : array) {
                            JSONObject jsonObject = (JSONObject) o;

                            String[] routineData = new String[3];
                            routineData[0] = (String) jsonObject.get("MAIL");
                            routineData[1] = (String) jsonObject.get("NAME");
                            routineData[2] = (String) jsonObject.get("DESCRIP");
                            b.putStringArray(Integer.toString(i), routineData);
                            i++;
                        }

                        return Result.success(b.putInt("size", i).build());
                    }
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }
            case "removeRoutine": {

                /*
                 *  HTTP Request to remove data from ROUTINE table
                 */

                String dir = "http://" + IP + ":5000/routine";
                HttpURLConnection urlConnection = null;

                String mail = getInputData().getString("mail");
                String name = getInputData().getString("name");
                try {
                    Uri.Builder builder = new Uri.Builder().appendQueryParameter("mail"
                            , mail).appendQueryParameter("name", name);
                    String params = builder.build().getEncodedQuery();

                    dir += "?" + params;

                    URL dest = new URL(dir);
                    urlConnection = (HttpURLConnection) dest.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("DELETE");

                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == 200) {
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            result.append(line);
                        }
                        inputStream.close();

                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(result.toString());

                        Boolean success = (Boolean) json.get("success");
                        Data.Builder b = new Data.Builder();
                        return Result.success(b.putBoolean("success", success).build());
                    }
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }
            case "removeRoutineEx": {

                /*
                 *  HTTP Request to delete data from ROUTINE_EXERCISE table
                 */

                String dir = "http://" + IP + ":5000/routine-ex";
                HttpURLConnection urlConnection = null;

                String mail = getInputData().getString("mail");
                String name = getInputData().getString("name");
                String idEj = getInputData().getString("idEj");
                try {
                    Uri.Builder builder = new Uri.Builder().appendQueryParameter("mail"
                                    , mail)
                            .appendQueryParameter("name", name).appendQueryParameter(
                                    "id_ej", idEj);
                    String params = builder.build().getEncodedQuery();

                    dir += "?" + params;

                    URL dest = new URL(dir);
                    urlConnection = (HttpURLConnection) dest.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("DELETE");

                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == 200) {
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            result.append(line);
                        }
                        inputStream.close();

                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(result.toString());

                        Boolean success = (Boolean) json.get("success");
                        Data.Builder b = new Data.Builder();
                        return Result.success(b.putBoolean("success", success).build());
                    }
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }
            case "insertEjRoutine": {

                /*
                 *  HTTP Request to insert data in ROUTINE_EXERCISE table
                 */

                String dir = "http://" + IP + ":5000/routine-ex/create";
                HttpURLConnection urlConnection = null;

                String mail = getInputData().getString("mail");
                String rName = getInputData().getString("rName");
                Integer exID = Integer.parseInt(getInputData().getString("exID"));
                try {
                    URL dest = new URL(dir);
                    urlConnection = (HttpURLConnection) dest.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    JSONObject paramJson = new JSONObject();
                    paramJson.put("mail", mail);
                    paramJson.put("routine_name", rName);
                    paramJson.put("ex_id", exID);
                    PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                    out.print(paramJson);
                    out.close();
                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == 200) {
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            result.append(line);
                        }
                        inputStream.close();

                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(result.toString());

                        Boolean success = (Boolean) json.get("success");
                        Data.Builder b = new Data.Builder();
                        return Result.success(b.putBoolean("success", success).build());
                    }
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
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
