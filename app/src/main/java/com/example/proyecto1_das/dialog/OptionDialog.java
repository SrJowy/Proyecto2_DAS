package com.example.proyecto1_das.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.proyecto1_das.R;
import com.example.proyecto1_das.db.ExternalDB;
import com.example.proyecto1_das.db.MyDB;
import com.example.proyecto1_das.exercises.ExerciseActivity;
import com.example.proyecto1_das.utils.FileUtils;

public class OptionDialog extends DialogFragment {

    public interface DialogListener {
        void onDialogRes(String res, View v);
    }

    private DialogListener listener;

    private String title;
    private CharSequence[] elements;
    private int optionId;
    private boolean hasChoices;
    private String[] args;

    private View v;

    public OptionDialog(String title, CharSequence[] elements, int optionId,
                        boolean hasChoices, String[] args, View v) {
        this.title = title;
        this.elements = elements;
        this.optionId = optionId;
        this.hasChoices = hasChoices;
        this.args = args;
        this.v = v;
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
                        listener.onDialogRes("00", v);
                    }
                } else if (optionId == 1) {
                    if (i == 0) {
                        MyDB myDB = new MyDB(getContext());
                        myDB.removeRoutineEx(Integer.parseInt(args[0]),
                                Integer.parseInt(args[1]));
                        myDB.close();
                        listener.onDialogRes("00", v);
                    }
                } else if (optionId == 2) {
                    if (i == 0) {
                        String[] keys =  new String[3];
                        Object[] params = new String[3];
                        keys[0] = "param";
                        keys[1] = "mail";
                        keys[2] = "date";
                        params[0] = "removeDiary";
                        params[1] = args[0];
                        params[2] = args[1];
                        Data param = ExternalDB.createParam(keys, params);
                        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(param).build();
                        WorkManager.getInstance(getContext()).getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                                .observe(this, workInfo -> {
                                    if (workInfo != null && workInfo.getState().isFinished()) {
                                        Log.i("TAG", "onCreateDialog: " + "eentra");
                                        if (workInfo.getState() != WorkInfo.State.SUCCEEDED) {
                                            MessageDialog d = new MessageDialog("ERROR",
                                                    getString(R.string.error_server));
                                            d.show(getChildFragmentManager(), "errorDialog");
                                        }
                                    }
                                });
                        WorkManager.getInstance(getContext()).enqueue(oneTimeWorkRequest);
                        listener.onDialogRes("00", v);
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
