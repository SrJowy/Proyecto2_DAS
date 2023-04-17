package com.example.proyecto1_das.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {
    public boolean sessionExists(Context context, String file) {
        String ret = readFile(context, file);

        if (ret.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    /*
     * Code extracted and adapted from StackOverflow (User: Iarsaars)
     * https://stackoverflow.com/questions/14376807/read-write-string-from-to-a-file-in-android
     */
    public String readFile(Context context, String file) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(file);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret.replaceAll("\\s", "");
    }
}
