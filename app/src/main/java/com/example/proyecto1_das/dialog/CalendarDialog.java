package com.example.proyecto1_das.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.proyecto1_das.R;
import com.example.proyecto1_das.data.Routine;
import com.example.proyecto1_das.db.MyDB;
import com.example.proyecto1_das.utils.FileUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarDialog extends DialogFragment {

    public interface DialogListener {
        void onRoutineClick(String routine, LocalDate date);
    }

    private DialogListener listener;

    private String title;

    private LocalDate date;

    private List<Routine> lRoutines;

    public CalendarDialog(String title, LocalDate date) {
        this.date = date;
        this.title = title;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        MyDB db = new MyDB(getContext());
        FileUtils fileUtils = new FileUtils();
        String mail = fileUtils.readFile(getContext(), "config.txt");
        lRoutines = db.selectRoutines(mail);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.routine_list_menu, null);
        List<String> lRoutinesString = getRoutineDesc();

        ListView listView = v.findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, lRoutinesString);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            listener.onRoutineClick((String) adapterView.getAdapter().getItem(i), date);
        });

        builder.setView(v);

        return builder.create();
    }

    private List<String> getRoutineDesc() {
        List<String> lRoutineDesc = new ArrayList<>();
        for (Routine r:
             lRoutines) {
            lRoutineDesc.add(r.getDesc());
        }
        return lRoutineDesc;
    }

    public void setListener(DialogListener d) {
        this.listener = d;
    }
}
