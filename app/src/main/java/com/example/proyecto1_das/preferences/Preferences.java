package com.example.proyecto1_das.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.example.proyecto1_das.R;

import java.util.Objects;

public class Preferences extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private PrefListener prefListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        prefListener = (PrefListener) context;
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState,
                                    @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.pref_config);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s) {
            case "theme":
                prefListener.changeTheme(sharedPreferences.getString(s, "light"));
                break;
            case "lang":
                prefListener.changeLang(sharedPreferences.getString(s, "en"));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getPreferenceManager()
                .getSharedPreferences())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getPreferenceManager()
                .getSharedPreferences())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public interface PrefListener {
        void changeLang(String lang);

        void changeTheme(String theme);
    }
}
