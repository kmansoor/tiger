package com.hurontg.tiger.parent;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hurontg.tiger.R;
import com.hurontg.tiger.domain.Child;

public class AddChildDialogFragment extends DialogFragment {
  public Child mChild = null;
  public int mPosition = -1;
  private OnAddListener mListener;
  private EditText mName, mGrade, mSection;

  public static AddChildDialogFragment instance(Child child, int position) {
    AddChildDialogFragment dialog = new AddChildDialogFragment();
    dialog.mChild = child;
    dialog.mPosition = position;
    return dialog;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    try {
      mListener = (OnAddListener) getParentFragment();
    } catch (ClassCastException e) {
      throw new ClassCastException("Calling fragment must implement OnAddListener interface");
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = requireActivity().getLayoutInflater();

    View view = inflater.inflate(R.layout.dialog_add_child, null);
    mName = view.findViewById(R.id.name);
    mGrade = view.findViewById(R.id.grade);
    mSection = view.findViewById(R.id.section);

    String message = "Add Child";
    String positiveButton = "Add";
    if (mChild != null) {
      mName.setText(mChild.getName());
      mGrade.setText(mChild.getGrade());
      mSection.setText(mChild.getSection());
      message = "Update Child";
      positiveButton = "Update";
    }

    builder.setMessage(message)
        .setPositiveButton(positiveButton, (dialog, id) -> {
          mListener.onAdd(new Child(mName.getText().toString(), mGrade.getText().toString(), mSection.getText().toString()), mPosition);
        })
        .setNegativeButton(R.string.cancel, (dialog, id) -> Toast.makeText(getContext(), "Cancel",Toast.LENGTH_SHORT).show());
    builder.setView(view);

    return builder.create();
  }

  public interface OnAddListener {
    /**
     * @param position -1 if new, else index of the child being edited
     */
    void onAdd(Child child, int position);
  }
}