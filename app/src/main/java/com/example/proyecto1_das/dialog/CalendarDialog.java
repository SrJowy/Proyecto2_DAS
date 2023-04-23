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

import java.util.ArrayList;
import java.util.List;

public class CalendarDialog extends DialogFragment {

    public interface DialogListener {
        void onRoutineClick(String routine, String day, View assocNumView);
    }

    private DialogListener listener;

    private final String title;

    private final String day;

    private final View assocNum;

    private final List<Routine> lRoutines;

    public CalendarDialog(String title, String day, View assocNum, List<Routine> lRout) {
        this.day = day;
        this.title = title;
        this.assocNum = assocNum;
        this.lRoutines = lRout;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.routine_list_menu, null);
        List<String> lRoutinesString = getRoutineDesc();

        ListView listView = v.findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, lRoutinesString);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            listener.onRoutineClick((String) adapterView.getAdapter().getItem(i), day, assocNum);
            dismiss();
        });

        builder.setView(v);
        builder.setNegativeButton(getString(R.string.exit), (dialogInterface, i) -> dismiss());

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
