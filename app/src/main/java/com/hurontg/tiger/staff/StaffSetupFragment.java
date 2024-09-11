package com.hurontg.tiger.staff;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hurontg.tiger.R;
import com.hurontg.tiger.core.HttpService;
import com.hurontg.tiger.domain.Staff;
import com.hurontg.tiger.util.Utils;

import org.jetbrains.annotations.NotNull;

import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaffSetupFragment extends Fragment {

  private EditText mName, mEmail, mPhone, mPassword;
  private HttpService mHttp;
  private Disposable mDisposable = null;
  private OnStaffSetupListener mListener;

  public StaffSetupFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      mListener = (OnStaffSetupListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement OnParentSetupListener");
    }
  }

  @Override
  public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_staff_setup, container, false);
    Button submit = view.findViewById(R.id.submit);
    submit.setOnClickListener(submitEvt -> doSubmit());

    mName = view.findViewById(R.id.name);
    mEmail = view.findViewById(R.id.email);
    mPhone = view.findViewById(R.id.phone);
    mPassword = view.findViewById(R.id.password);

    return view;
  }

  private void doSubmit() {
    if (canSubmit()) {
      Staff staff = new Staff(mName.getText().toString().trim(), mEmail.getText().toString().trim(),
          mPhone.getText().toString().trim(), mPassword.getText().toString().trim());

      mListener.onCreateStaffSetup(staff);
    }
  }

  private boolean canSubmit() {
    StringBuilder sb = new StringBuilder();

    Utils.putErrorMessage(!Utils.isValidName(mName.getText()), "Name is not valid", sb);
    Utils.putErrorMessage(!Utils.isValidEmail(mEmail.getText()), "Email is not valid", sb);
    Utils.putErrorMessage(!Utils.isValidPhone(mPhone.getText()), "Phone is not valid", sb);
    Utils.putErrorMessage(TextUtils.isEmpty(mPassword.getText()), "Password is empty", sb);

    if (sb.length() > 0) {
      sb.insert(0, "Errors: ");
      Toast.makeText(getContext(), sb.toString(), Toast.LENGTH_LONG).show();
      return false;
    } else {
      return true;
    }
  }

  public interface OnStaffSetupListener {
    void onCreateStaffSetup(Staff staff);
  }
}
