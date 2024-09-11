package com.hurontg.tiger.main;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hurontg.tiger.R;
import com.hurontg.tiger.util.Constants;
import com.hurontg.tiger.util.PrefsUtil;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainSetupFragment extends Fragment implements View.OnClickListener {

  private OnSetupRoleSelectedListener mListener;

  public MainSetupFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      mListener = (OnSetupRoleSelectedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement OnArticleSelectedListener");
    }
  }

  @Override
  public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    String applicationRole = PrefsUtil.getApplicationRole(getContext());

    if (!applicationRole.equals(Constants.USER_ROLE_NOT_SELECTED)) {
      Intent intent = new Intent(getActivity(), MainActivity.class);
      startActivity(intent);
      return null;
    }

    View view = inflater.inflate(R.layout.fragment_main_setup, container, false);

    View parentSetupButton = view.findViewById(R.id.parent_setup);
    parentSetupButton.setOnClickListener(this);
    View staffSetupButton = view.findViewById(R.id.staff_setup);
    staffSetupButton.setOnClickListener(this);

    return view;
  }

  @Override
  public void onClick(View v) {
    mListener.onSetupRoleSelected(v.getId());
  }

  public interface OnSetupRoleSelectedListener {
    void onSetupRoleSelected(int id);
  }
}
