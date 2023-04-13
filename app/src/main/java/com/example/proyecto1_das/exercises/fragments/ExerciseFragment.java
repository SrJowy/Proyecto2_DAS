package com.example.proyecto1_das.exercises.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto1_das.R;
import com.example.proyecto1_das.data.Exercise;
import com.example.proyecto1_das.db.MyDB;
import com.example.proyecto1_das.exercises.MyItemRecyclerViewAdapter;
import com.example.proyecto1_das.utils.LocaleUtils;

import java.util.ArrayList;
import java.util.List;

public class ExerciseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_list, container, false);

        String rId = "";
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            rId = bundle.getString("RID");
        }

        String lang = LocaleUtils.getLanguage(getContext());

        MyDB myDB = new MyDB(getContext());
        List<Exercise> lExercises = myDB.selectExercisesByRoutineID(rId, lang);
        myDB.close();

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(lExercises,
                    getContext()));
        }
        return view;
    }

}