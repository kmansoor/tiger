package com.hurontg.tiger.parent;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.hurontg.tiger.R;
import com.hurontg.tiger.domain.Child;
import com.hurontg.tiger.domain.Parent;
import com.hurontg.tiger.util.Utils;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParentSetupFragment extends Fragment implements View.OnClickListener,
    AddChildDialogFragment.OnAddListener, ChildListAdapter.OnClickListener {
  private EditText mName, mEmail, mPhone, mPassword1, mPassword2;
  private RecyclerView mRecyclerView;
  private ChildListAdapter mAdapter;
  private LinkedList<Child> children = new LinkedList<>();
  private OnParentSetupListener mListener;

  public ParentSetupFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      mListener = (OnParentSetupListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement OnParentSetupListener");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_parent_setup, container, false);
    View addChild = view.findViewById(R.id.add_child_button);
    addChild.setOnClickListener(this);
    View submit = view.findViewById(R.id.submit);
    submit.setOnClickListener(this);
    mName = view.findViewById(R.id.name);
    mEmail = view.findViewById(R.id.email);
    mPhone = view.findViewById(R.id.phone);
    mPassword1 = view.findViewById(R.id.password1);
    mPassword2 = view.findViewById(R.id.password2);

    mRecyclerView = view.findViewById(R.id.recyclerview_children);
    mAdapter = new ChildListAdapter(getContext(), children, this);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    return view;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.add_child_button:
        showDialog(null, -1);
        break;
      case R.id.submit:
        doSubmit();
        break;
    }
  }

  @Override
  public void onAdd(Child child, int position) {
    if (isValidChild(child)) {
      if (position == -1) {
        this.children.add(child);
      } else {
        children.set(position, child);
      }
      mAdapter.notifyDataSetChanged();
    }
  }

  private boolean isValidChild(Child child) {
    StringBuilder sb = new StringBuilder();

    Utils.putErrorMessage(child.getName().isEmpty(), "Name not entered", sb);
    Utils.putErrorMessage(child.getGrade().isEmpty(), "Grade not entered", sb);
    Utils.putErrorMessage(child.getSection().isEmpty(), "Section not entered", sb);

    if (sb.length() > 0) {
      sb.insert(0, "Errors: ");
      Toast.makeText(getContext(), sb.toString(), Toast.LENGTH_LONG).show();
      return false;
    } else {
      return true;
    }
  }

  private void showDialog(Child child, int position) {
    DialogFragment dialog = AddChildDialogFragment.instance(child, position);
    dialog.show(getChildFragmentManager(), "addChild");
  }

  private void doSubmit() {
    if (canSubmit()) {
      Parent parent = new Parent(mName.getText().toString().trim(), mEmail.getText().toString().trim(),
          mPhone.getText().toString().trim(), mPassword1.getText().toString().trim(), children);
      mListener.onCreateParentSetup(parent);
    }
  }

  private boolean canSubmit() {
    StringBuilder sb = new StringBuilder();

    Utils.putErrorMessage(!Utils.isValidName(mName.getText()), "Name is not valid", sb);
    Utils.putErrorMessage(!Utils.isValidEmail(mEmail.getText()), "Email is not valid", sb);
    Utils.putErrorMessage(!Utils.isValidPhone(mPhone.getText()), "Phone is not valid", sb);
    Utils.putErrorMessage(!Utils.isValidPassword(mPassword1.getText(), mPassword2.getText()), "Password is not valid", sb);
    Utils.putErrorMessage(children.size() == 0, "No Child added", sb);

    if (sb.length() > 0) {
      sb.insert(0, "Errors: ");
      Toast.makeText(getContext(), sb.toString(), Toast.LENGTH_LONG).show();
      return false;
    } else {
      return true;
    }
  }

  @Override
  public void onEdit(int position) {
    showDialog(children.get(position), position);
  }

  @Override
  public void onDelete(int position) {
    Log.e("ParentSetupFragment", "onDelete: " + position);
  }

  public interface OnParentSetupListener {
    void onCreateParentSetup(Parent parent);
  }
}
