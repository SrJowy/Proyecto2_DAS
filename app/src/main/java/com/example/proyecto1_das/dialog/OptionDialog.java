package com.example.proyecto1_das.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.proyecto1_das.R;
import com.example.proyecto1_das.db.MyDB;
import com.example.proyecto1_das.exercises.ExerciseActivity;

public class OptionDialog extends DialogFragment {

    public interface DialogListener {
        void onDialogRes(String res);
    }

    private DialogListener listener;

    private String title;
    private CharSequence[] elements;
    private int optionId;
    private boolean hasChoices;
    private String[] args;

    public OptionDialog(String title, CharSequence[] elements, int optionId,
                        boolean hasChoices, String[] args) {
        this.title = title;
        this.elements = elements;
        this.optionId = optionId;
        this.hasChoices = hasChoices;
        this.args = args;
    }

    public void setListener(DialogListener l) {
        this.listener = l;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        if (hasChoices) {
            builder.setSingleChoiceItems(elements, -1,
                    (dialogInterface, i) -> {
                if (optionId == 0) {
                    if (i == 0) {

                    }
                } else {

                }
            });
            builder.setPositiveButton("OK", (dialogInterface, i) -> {

            });
        } else {
            builder.setItems(elements, (dialogInterface, i) -> {
                if (optionId == 0) {
                    if (i == 0) {
                        MyDB myDB = new MyDB(getContext());
                        myDB.removeRoutine(args[0], args[1]);
                        myDB.close();
                        listener.onDialogRes("00");
                    }
                } else if (optionId == 1) {
                    if (i == 0) {
                        MyDB myDB = new MyDB(getContext());
                        myDB.removeRoutineEx(Integer.parseInt(args[0]),
                                Integer.parseInt(args[1]));
                        myDB.close();
                        listener.onDialogRes("00");
                    }
                }
            });
            builder.setNegativeButton(getString(R.string.exit), (dialogInterface, i) -> {
                dismiss();
            });
        }

        return builder.create();
    }
}
