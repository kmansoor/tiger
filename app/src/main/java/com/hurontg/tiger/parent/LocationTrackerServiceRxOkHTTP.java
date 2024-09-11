package com.hurontg.tiger.parent;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.hurontg.tiger.R;
import com.hurontg.tiger.core.HttpService;
import com.hurontg.tiger.util.Constants;
import com.hurontg.tiger.util.PrefsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LocationTrackerServiceRxOkHTTP extends Service {
  private final static String TAG = "LTSRxOkHTTP";

  private FusedLocationProviderClient mFusedLocationClient = null;
  private LocationCallback mLocationCallback;
  private HttpService mHttp;
  private Disposable mDisposable = null;
  private String authToken;

  public LocationTrackerServiceRxOkHTTP() {
  }

  @Override
  public void onCreate() {
    mHttp = new HttpService();
    authToken = PrefsUtil.getParentAuthToken(this);

    startForegroundService();
    setupLocationUpdatesRequest();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onDestroy() {
    Log.e(TAG, "onDestroy thread");

    if (mFusedLocationClient != null) {
      mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
    if (mDisposable != null && !mDisposable.isDisposed()) {
      mDisposable.dispose();
    }
    stopForeground(true);
  }

  private void startForegroundService() {
    Intent intent = new Intent();
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

    NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, "77")
        .setContentTitle("OGS Child Pickup")
        .setContentText("Following your location")
        .setSmallIcon(R.drawable.ic_android)
        .setContentIntent(pendingIntent);

    Notification notification = notifyBuilder.build();
    startForeground(1, notification);
  }

  private void setupLocationUpdatesRequest() throws SecurityException {
    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    mLocationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        Location location = locationResult.getLastLocation();

        JSONObject jsonLocation = new JSONObject();
        try {
          jsonLocation.put("lat", location.getLatitude());
          jsonLocation.put("lng", location.getLongitude());
          jsonLocation.put("authToken", authToken);
          mDisposable = mHttp.postLocationData(jsonLocation)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(evt -> evt = false,
                  err -> Log.e(TAG, err.getMessage()));
        } catch (JSONException e) {
          Log.e(TAG, e.getMessage());
        }
      }
    };

    mFusedLocationClient.requestLocationUpdates(
        getLocationRequest(), mLocationCallback, null);
  }

  private LocationRequest getLocationRequest() {
    LocationRequest locationRequest = new LocationRequest();
    locationRequest.setInterval(Constants.LOCATION_TRACKING_INTERVAL);
//    locationRequest.setSmallestDisplacement(Constants.SMALLEST_DISPLACEMENT);
    locationRequest.setExpirationDuration(Constants.LOCATION_EXPIRATION_DURATION);
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    return locationRequest;
  }
}
