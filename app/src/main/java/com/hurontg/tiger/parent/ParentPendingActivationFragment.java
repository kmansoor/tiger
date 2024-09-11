package com.hurontg.tiger.parent;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hurontg.tiger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParentPendingActivationFragment extends Fragment {


  public ParentPendingActivationFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_parent_pending_activation, container, false);
  }

}
