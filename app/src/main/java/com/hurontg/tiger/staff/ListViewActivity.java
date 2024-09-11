package com.hurontg.tiger.staff;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.hurontg.tiger.R;
import com.hurontg.tiger.core.HttpService;
import com.hurontg.tiger.domain.RealtimeLocation;
import com.hurontg.tiger.util.Constants;
import com.hurontg.tiger.util.PrefsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ListViewActivity extends BaseMenuActivity implements LocationListAdapter.OnClickListener {
  private final String TAG = "ListViewActivity";
  private final LinkedList<String> mLocationList = new LinkedList<>();
  private RecyclerView mRecyclerView;
  private LocationListAdapter mAdapter;
  private Disposable mDisposable;
  private boolean mTakeMore = true;
  private HttpService mHttp = new HttpService();
  private int mCurrentCampus;
  private List<RealtimeLocation> mLocationData;
  private String mAuthToken;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_view);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    mAuthToken = PrefsUtil.getAuthToken(this);
    mCurrentCampus = PrefsUtil.getCampus(this);

    mRecyclerView = findViewById(R.id.recyclerview);
    mAdapter = new LocationListAdapter(this, mLocationList, this);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    trackOnMap();
  }

  @Override
  public void onDestroy() {
    this.mTakeMore = false;
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    boolean b = super.onCreateOptionsMenu(menu);
    setMapMenuItemsState(menu);
    return b;
  }

  private void setMapMenuItemsState(Menu menu) {
    menu.findItem(R.id.satellite_map).setVisible(false);
    menu.findItem(R.id.my_location).setVisible(false);
    menu.findItem(R.id.listview_activity).setVisible(false);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.normal_map:
        startMapsActivity();
        return true;
      case R.id.admin_activity:
        startAdminActivity();
        return true;
      case R.id.campus_primary:
        setCampus(Constants.CAMPUS_PRIMARY);
        return true;
      case R.id.campus_secondary:
        setCampus(Constants.CAMPUS_SECONDARY);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void setCampus(int campus) {
    if (campus == mCurrentCampus) return;

    mCurrentCampus = campus;
    PrefsUtil.setCampus(this, campus);
    drawList();
  }

  private void trackOnMap() {
    mDisposable = Observable.interval(0, 3, TimeUnit.SECONDS, Schedulers.io())
        .takeWhile((evt) -> mTakeMore)
        .switchMap(evt -> mHttp.getLocationData(mAuthToken))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(locationData -> {
              mLocationData = locationData;
              drawList();
            },
            error -> Log.e(TAG, "onError: " + error),
            () -> Log.e(TAG, "***** trackOnMap::onComplete: " + Thread.currentThread().getName())
        );
  }

  private void drawList() {
    List<RealtimeLocation> currentCampusList = new ArrayList<>();
    LatLng campus = Constants.getLatLngByCampus(mCurrentCampus);
    float[] result = new float[1];

    for(RealtimeLocation rtl: mLocationData) {
      if (rtl.isCampusMatch(mCurrentCampus)) {
        Location.distanceBetween(rtl.getLatLng().latitude, rtl.getLatLng().longitude, campus.latitude, campus.longitude, result);
        rtl.setDistanceToCampus(result[0]);
        currentCampusList.add(rtl);
      }
    }

    Collections.sort(currentCampusList, ((o1, o2) -> {
      if (o1.getDistanceToCampus() < o2.getDistanceToCampus()) return -1;
      else if (o1.getDistanceToCampus() > o2.getDistanceToCampus()) return 1;
      else return 0;
    }));

    mLocationList.clear();

    for(RealtimeLocation rtl: currentCampusList) {
      mLocationList.add(rtl.getTitle());
    }
    mAdapter.notifyDataSetChanged();
    getSupportActionBar().setTitle(Constants.getCampusTitle(mCurrentCampus));
  }

  @Override
  public void onClick(int position) {
    Toast.makeText(this, mLocationList.get(position), Toast.LENGTH_LONG).show();
  }
}
