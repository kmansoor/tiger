package com.hurontg.tiger.parent;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hurontg.tiger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParentActiveFragment extends Fragment {


  public ParentActiveFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_parent_active, container, false);
    view.findViewById(R.id.pause_location)
        .setOnClickListener(button -> {
          Intent intent = new Intent(getActivity(), LocationTrackerServiceRxOkHTTP.class);
          getActivity().stopService(intent);
        });

    return view;
  }

}
