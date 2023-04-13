package com.example.proyecto1_das.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.example.proyecto1_das.MainActivity;
import com.example.proyecto1_das.R;

public class ThemeUtils {

    public static void changeTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("com.example" +
                        ".proyecto1_das_preferences",
                Context.MODE_PRIVATE);
        String theme = prefs.getString("theme", "light");
        if (theme.equals("light")) {
            context.setTheme(R.style.Theme_Proyecto1_DAS);
        } else {
            context.setTheme(R.style.Theme_Proyecto1_DAS_Dark);
        }
    }

    public static void changeActionBar(AppCompatActivity activity) {
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(activity,
                R.color.act_bar)));
        SpannableString s = new SpannableString("Pocket Routine");
        s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity,
                        R.color.white)),
                0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(s);
    }

    public static boolean isLightThemeSet(Context context) {
        int[] attrs = { com.google.android.material.R.attr.colorTertiary };
        @SuppressLint("ResourceType") TypedArray ta1 = context.obtainStyledAttributes(
                R.style.Theme_Proyecto1_DAS, attrs);
        int colorPrimary1 = ta1.getResourceId(0, 0);
        ta1.recycle();

        @SuppressLint("ResourceType") TypedArray a =
                context.obtainStyledAttributes(attrs);
        int colorPrimaryCurrent = a.getResourceId(0, 0);
        a.recycle();

        return colorPrimary1 == colorPrimaryCurrent;
    }

    public static void initAppTheme(Context context) {
        String theme = "light";
        int nightModeFlags = context.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            theme = "dark";
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "com.example.proyecto1_das_preferences",
                Context.MODE_PRIVATE);
        if (!sharedPreferences.contains("theme")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("theme", theme);
            editor.apply();
        }
    }
}
