package com.hurontg.tiger.staff;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class ConfirmationDialog extends DialogFragment {
  public String prompt;
  public String uuid;

  public interface ConfirmationDialogListener {
    void onDialogPositiveClick(DialogFragment dialog);
    void onDialogNegativeClick(DialogFragment dialog);
  }

  // Use this instance of the interface to deliver action events
  ConfirmationDialogListener listener;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    try {
      listener = (ConfirmationDialogListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(getActivity().toString()
          + " must implement NoticeDialogListener");
    }
  }

  public static ConfirmationDialog instance(String prompt, String uuid) {
    ConfirmationDialog dialog = new ConfirmationDialog();
    dialog.prompt = prompt;
    dialog.uuid = uuid;
    return dialog;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage(prompt)
        .setPositiveButton("Yes", (dialog, id) -> listener.onDialogPositiveClick(ConfirmationDialog.this))
        .setNegativeButton("No", (dialog, id) -> listener.onDialogNegativeClick(ConfirmationDialog.this));
    return builder.create();
  }


}