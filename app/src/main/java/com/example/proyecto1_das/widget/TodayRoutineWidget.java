package com.example.proyecto1_das.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto1_das.R;
import com.example.proyecto1_das.data.Exercise;
import com.example.proyecto1_das.db.ExternalDB;
import com.example.proyecto1_das.exercises.fragments.ExerciseFragment;
import com.example.proyecto1_das.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/*
 * Implements the functionality associated to the Widget of the app
 */
public class TodayRoutineWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        FileUtils fileUtils = new FileUtils();
        String mail;

        // The mail of the user is read from the file saved
        mail = fileUtils.readFile(context, "config.txt");

        if (mail.isEmpty()) {
            // If the user is not authenticated within the app
            mail = "undefined";
        }

        // A http request is done to retrieve today's diary
        String url = "http://" + ExternalDB.getIp() + ":5000/diary?mail="+mail;
        Log.d("TRW", "updateAppWidget: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.i("TRW", "onCreate: " + response);

                    try {
                        JSONArray json = response.getJSONArray("result");
                        RemoteViews views = new RemoteViews(context.getPackageName(),
                                R.layout.today_routine_widget);
                        LocalDate localDate = LocalDate.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        String fec = localDate.format(formatter);

                        views.setTextViewText(R.id.mainFec, fec);
                        if (json.length() != 0) {
                            // If there's data on the db
                            JSONObject o = json.getJSONObject(0);
                            // Set the routine desc in the widget
                            views.setTextViewText(R.id.mainTitle,
                                    o.getString("ROUTINE"));

                        }
                        appWidgetManager.updateAppWidget(appWidgetId, views);

                    } catch (Exception e) {
                        Log.e("TRW", "updateAppWidget: ", e);
                    }

                }, error -> {
            Log.e("TRW", "onCreate: ", error);
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}