package com.example.proyecto1_das.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.proyecto1_das.R;

public class MessageDialog extends DialogFragment {

    private String title;

    private String message;

    public MessageDialog(String title, String message) {
        this.title = title;
        this.message = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            dismiss();
        });

        return builder.create();
    }
}
