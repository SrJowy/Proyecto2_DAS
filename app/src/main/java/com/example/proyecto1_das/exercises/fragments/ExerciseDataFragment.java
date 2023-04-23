package com.example.proyecto1_das.exercises.fragments;

import static android.Manifest.permission.CAMERA;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto1_das.PhotoActivity;
import com.example.proyecto1_das.R;
import com.example.proyecto1_das.data.Exercise;
import com.example.proyecto1_das.db.ExternalDB;
import com.example.proyecto1_das.utils.LocaleUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDataFragment extends Fragment {
    private int exID;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_data, container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String lang = LocaleUtils.getLanguage(getContext());

        String url = "http://" + ExternalDB.getIp() + ":5000/exercise";
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("id_ex", exID);
            requestBody.put("lang", lang);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Intent intent = new Intent(getActivity(),
                                PhotoActivity.class);
                        intent.putExtra("exID", exID);
                        startActivity(intent);
                    } else {

                        Toast.makeText(getActivity(),
                                getString(R.string.permission_denied),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                requestBody, response -> {
                    List<Exercise> lEx = transformJson(response);

                    if (!lEx.isEmpty()) {
                        Exercise e = lEx.get(0);
                        TextView tExName = getView().findViewById(R.id.exName);
                        tExName.setText(e.getName());

                        TextView tExDesc = getView().findViewById(R.id.exDesc);
                        tExDesc.setText(e.getDes());

                        TextView tNumSeries = getView().findViewById(R.id.numSeriesData);
                        tNumSeries.setText(Integer.toString(e.getNumSeries()));

                        TextView tNumReps = getView().findViewById(R.id.numRepsData);
                        tNumReps.setText(Integer.toString(e.getNumReps()));

                        TextView tKgs = getView().findViewById(R.id.exKGs);
                        tKgs.setText(Double.toString(e.getNumKgs()));

                        Button b = getView().findViewById(R.id.moreinfobutton);
                        b.setOnClickListener(c -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(e.getLink()));
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            startActivity(intent);
                        });

                        Button photoButton = getView().findViewById(R.id.save_foto);
                        photoButton.setOnClickListener(c -> {
                            if (ContextCompat.checkSelfPermission(getContext(), CAMERA) !=
                                    PackageManager.PERMISSION_GRANTED) {
                                requestPermissionLauncher.launch(CAMERA);
                            } else {
                                Intent intent = new Intent(getActivity(),
                                        PhotoActivity.class);
                                intent.putExtra("exID", exID);
                                startActivity(intent);
                            }
                        });

                        setImage(exID);
                    }

                }, error -> {
                    Log.e("EDF", "onCreate: ", error);
                });

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);


    }

    private List<Exercise> transformJson(JSONObject response) {
        List<Exercise> lEx = new ArrayList<>();
        try {
            JSONArray array = (JSONArray) response.get("result");
            for (int i = 0; i < array.length(); i++) {
                Exercise e = new Exercise();
                JSONObject json = (JSONObject) array.get(i);
                e.setId((Integer) json.get("ID"));
                e.setName((String) json.get("NAME"));
                e.setDes((String) json.get("DES"));
                e.setNumSeries((Integer) json.get("NUM_SERIES"));
                e.setNumReps((Integer) json.get("NUM_REPS"));
                e.setNumKgs(Double.valueOf(json.get("KG").toString()));
                e.setLink((String) json.get("LINK"));
                lEx.add(e);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return lEx;
    }

    public void setData(int exID) {
        this.exID = exID;
    }

    public void setData2(int exID) {
        String lang = LocaleUtils.getLanguage(getContext());

        String url = "http://" + ExternalDB.getIp() + ":5000/exercise";
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("id_ex", exID);
            requestBody.put("lang", lang);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                requestBody, response -> {
                    List<Exercise> lEx = transformJson(response);

                    if (!lEx.isEmpty()) {
                        Exercise e = lEx.get(0);
                        TextView tExName = getView().findViewById(R.id.exName);
                        tExName.setText(e.getName());

                        TextView tExDesc = getView().findViewById(R.id.exDesc);
                        tExDesc.setText(e.getDes());

                        TextView tNumSeries = getView().findViewById(R.id.numSeriesData);
                        tNumSeries.setText(Integer.toString(e.getNumSeries()));

                        TextView tNumReps = getView().findViewById(R.id.numRepsData);
                        tNumReps.setText(Integer.toString(e.getNumReps()));

                        TextView tKgs = getView().findViewById(R.id.exKGs);
                        tKgs.setText(Double.toString(e.getNumKgs()));

                        Button b = getView().findViewById(R.id.moreinfobutton);
                        b.setOnClickListener(c -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(e.getLink()));
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            startActivity(intent);
                        });

                        Button photoButton = getView().findViewById(R.id.save_foto);
                        photoButton.setOnClickListener(c -> {
                            if (ContextCompat.checkSelfPermission(getContext(), CAMERA) !=
                                    PackageManager.PERMISSION_GRANTED) {
                                requestPermissionLauncher.launch(CAMERA);
                            } else {
                                Intent intent = new Intent(getActivity(),
                                        PhotoActivity.class);
                                intent.putExtra("exID", exID);
                                startActivity(intent);
                            }
                        });

                        setImage(exID);
                    }

                }, error -> {
                    Log.e("EDF", "onCreate: ", error);
                });

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private void setImage(int id) {
        ImageView iv = getView().findViewById(R.id.imageView);
        switch (id) {
            case 1: {
                iv.setImageResource(R.drawable.benchpress);
                break;
            }
            case 2: {
                iv.setImageResource(R.drawable.tricepspolea);
                break;
            }
            case 3: {
                iv.setImageResource(R.drawable.pressinclinado);
                break;
            }

        }

    }
}