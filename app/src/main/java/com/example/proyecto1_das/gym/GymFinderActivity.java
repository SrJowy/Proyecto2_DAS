package com.example.proyecto1_das.gym;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto1_das.R;
import com.example.proyecto1_das.dialog.MessageDialog;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class GymFinderActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 34;
    private static final int NOTIFICATION_DELAY = 1000 * 20;
    private static final String API_KEY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.initialize(getBaseContext());
        ThemeUtils.changeTheme(this);
        ThemeUtils.changeActionBar(this);
        ThemeUtils.setBackArrow(this);
        setContentView(R.layout.activity_gym_finder);

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient provider =
                    LocationServices.getFusedLocationProviderClient(this);
            // Ask for user's location
            provider.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            String locationS = String.format(location.getLatitude() + "," + location.getLongitude());
                            String radius = "1500"; // 1.5 km
                            String type = "gym";
                            String url = String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?keyword=%s&location=%s&radius=%s&type=%s&key=%s", type, locationS, radius, type, API_KEY);
                            final RequestQueue queue = Volley.newRequestQueue(this);
                            // HTTP Post request to get JSON with all gym locations in a 1.5 km
                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                                    response -> {
                                        // Parse JSON data
                                        try {
                                            JSONArray jsonA = response.getJSONArray(
                                                        "results");
                                            ArrayList<String> gyms = new ArrayList<>();
                                            for (int i = 0; i < jsonA.length(); i++) {
                                                JSONObject jsonO = jsonA.getJSONObject(i);
                                                String gymName = jsonO.getString("name");
                                                String gymStreet = jsonO.getString("vicinity");
                                                double rate = jsonO.getDouble("rating");
                                                String data = gymName + " | " + gymStreet + " (" + rate + "/5)";
                                                gyms.add(data);
                                            }

                                            // Find list view and set the data
                                            ListView lv = findViewById(R.id.list_gyms);
                                            ArrayAdapter<String> adapter =
                                                    new ArrayAdapter<>(this,
                                                            android.R.layout.simple_list_item_1, gyms);
                                            lv.setAdapter(adapter);
                                            lv.setOnItemClickListener((parent, view, position, id) -> {
                                                Calendar calendar = Calendar.getInstance();
                                                // Set the time when the notification will be delivered
                                                calendar.setTimeInMillis(System.currentTimeMillis() + NOTIFICATION_DELAY);
                                                Intent intent = new Intent(this, GymNotification.class);
                                                intent.putExtra("gymName", ((TextView) view).getText()
                                                        .toString().split(" \\| ")[0]);
                                                // Create the pending intent to push the notification
                                                PendingIntent pendingIntent;
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                    pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);
                                                } else {
                                                    pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                                                }
                                                // Set the alarm to execute the code in 20 seconds
                                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                                if (alarmManager != null) {
                                                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                                    Toast.makeText(this, getString(R.string.msg_success),
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    },
                                    error -> {
                                        Log.e("GFA", "onCreate: ", error);
                                        MessageDialog d = new MessageDialog("ERROR", getString(R.string.error_server));
                                        d.show(getSupportFragmentManager(), "errorDialog");
                                    });
                            queue.add(request);
                        } else {
                            MessageDialog d = new MessageDialog("ERROR", getString(R.string.error_server));
                            d.show(getSupportFragmentManager(), "errorDialog");
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        Log.e("GFA", "onCreate: ", e);
                        MessageDialog d = new MessageDialog("ERROR", getString(R.string.error_server));
                        d.show(getSupportFragmentManager(), "errorDialog");
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}