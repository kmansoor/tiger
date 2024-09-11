package com.hurontg.tiger.staff;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hurontg.tiger.R;
import com.hurontg.tiger.core.HttpService;
import com.hurontg.tiger.domain.RealtimeLocation;
import com.hurontg.tiger.util.Constants;
import com.hurontg.tiger.util.PrefsUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MapsActivity extends BaseMenuActivity implements OnMapReadyCallback {

  private final static String TAG = "MapsActivity";

  private final String CURRENT_MAP_TYPE = "CURRENT_MAP_TYPE";

  private Disposable mDisposable = null;
  private GoogleMap mMap;
  private HttpService mHttp = new HttpService();
  private boolean mTakeMore = true;
  MenuItem mMapNormal, mMapSatellite;
  private int mCurrentMapType = GoogleMap.MAP_TYPE_NORMAL;
  private int mCurrentCampus;
  private List<RealtimeLocation> mLocationData;
  private String mAuthToken;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    if (savedInstanceState != null) {
      mCurrentMapType = savedInstanceState.getInt(CURRENT_MAP_TYPE, GoogleMap.MAP_TYPE_NORMAL);
    }

    mCurrentCampus = PrefsUtil.getCampus(this);
    mAuthToken = PrefsUtil.getAuthToken(this);

    trackOnMap();
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.setMapType(mCurrentMapType);

    moveCameraOnCurrentCampus();
  }

  @Override
  public void onDestroy() {
    mTakeMore = false;
//    if (mDisposable != null && !mDisposable.isDisposed()) {
//      mDisposable.dispose();
//    }
    super.onDestroy();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putInt(CURRENT_MAP_TYPE, mCurrentMapType);
    super.onSaveInstanceState(outState);
  }

  private void setCampus(int campus) {
    PrefsUtil.setCampus(this, campus);
    mCurrentCampus = campus;
    moveCameraOnCurrentCampus();
    drawMap();
  }

  private void moveCameraOnCurrentCampus() {
    LatLng ogs = Constants.getLatLngByCampus(mCurrentCampus);
    float zoom = Constants.ZOOM;

    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ogs, zoom));
  }

  private void trackOnMap() {
    mDisposable = Observable.interval(0,3, TimeUnit.SECONDS, Schedulers.io())
        .takeWhile((evt) -> mTakeMore)
        .switchMap(rtl -> mHttp.getLocationData(mAuthToken))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(locationData -> {
              mLocationData = locationData;
              drawMap();
            },
            onError -> Log.e(TAG, "onError: " + Thread.currentThread().getName()),
            () -> Log.e(TAG, "***** trackOnMap::onComplete: " + Thread.currentThread().getName())
        );
  }

  private void drawMap() {
    if (mMap != null) {
      mMap.clear();

      for (RealtimeLocation rtl: mLocationData) {
        if (rtl.isCampusMatch(mCurrentCampus)) {
          mMap.addMarker(
              new MarkerOptions()
                  .position(rtl.getLatLng())
                  .title(rtl.getTitle()));
        }
      }
      addCampusMarkers();
      getSupportActionBar().setTitle(Constants.getCampusTitle(mCurrentCampus));
    }
  }

  private void addCampusMarkers() {
    for (int i = 1; i < 3; i++) {
      mMap.addMarker(new MarkerOptions()
          .position(Constants.getLatLngByCampus(i))
          .title(Constants.getCampusTitle(i)));
    }
  }

  private void setMapMenuItemsState() {
    if (mCurrentMapType == GoogleMap.MAP_TYPE_NORMAL) {
      mMapNormal.setVisible(false);
      mMapSatellite.setVisible(true);
    } else {
      mMapNormal.setVisible(true);
      mMapSatellite.setVisible(false);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    boolean b = super.onCreateOptionsMenu(menu);

    mMapNormal = menu.findItem(R.id.normal_map);
    mMapSatellite = menu.findItem(R.id.satellite_map);
    setMapMenuItemsState();
    return b;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.normal_map:
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mCurrentMapType = GoogleMap.MAP_TYPE_NORMAL;
        setMapMenuItemsState();
        return true;
      case R.id.satellite_map:
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mCurrentMapType = GoogleMap.MAP_TYPE_SATELLITE;
        setMapMenuItemsState();
        return true;
      case R.id.admin_activity:
        startAdminActivity();
        return true;
      case R.id.listview_activity:
        startListViewActivity();
        return true;
      case R.id.my_location:
        moveCameraOnCurrentCampus();
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

}
