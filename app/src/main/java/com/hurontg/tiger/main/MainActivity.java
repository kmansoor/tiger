package com.hurontg.tiger.main;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.hurontg.tiger.R;
import com.hurontg.tiger.core.HttpService;
import com.hurontg.tiger.domain.Parent;
import com.hurontg.tiger.domain.Staff;
import com.hurontg.tiger.parent.LocationTrackerServiceRxOkHTTP;
import com.hurontg.tiger.parent.ParentActiveFragment;
import com.hurontg.tiger.parent.ParentPendingActivationFragment;
import com.hurontg.tiger.parent.ParentSetupFragment;
import com.hurontg.tiger.staff.MapsActivity;
import com.hurontg.tiger.staff.StaffSetupFragment;
import com.hurontg.tiger.util.Constants;
import com.hurontg.tiger.util.PrefsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements
    MainSetupFragment.OnSetupRoleSelectedListener, ParentSetupFragment.OnParentSetupListener,
    StaffSetupFragment.OnStaffSetupListener {

  private static final String TAG = "MainActivity";
  private final int REQUEST_START_TRACKING_PERMISSION = 29;
  private HttpService mHttp;
  private Disposable mDisposable = null;
  private AlarmManager alarmMgr;
  private PendingIntent alarmIntent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mHttp = new HttpService();

    String applicationRole = PrefsUtil.getApplicationRole(this);

    if (applicationRole.equals(Constants.USER_ROLE_NOT_SELECTED)) {
      replaceFragment(new MainSetupFragment());
      return;
    } else if (applicationRole.equals(Constants.USER_ROLE_PARENT)) {
      String parentStatus = PrefsUtil.getParentStatus(this);

      if (parentStatus.equals(Constants.ACTIVE)) {
        startTrackingLocation();
      } else if (parentStatus.equals(Constants.PENDING_ACTIVATION)) {
        String token = PrefsUtil.getParentAuthToken(this);
        mDisposable = mHttp.getParentStatus(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(status -> {
              if (status.getStatus().equals(Constants.ACTIVE)) {
                PrefsUtil.setParentStatus(this, Constants.ACTIVE);
                PrefsUtil.setRegions(this, status.getRegionsAsJson().toString());
                startTrackingLocation();
              } else if (status.getStatus().equals(Constants.PENDING_ACTIVATION)) {
                replaceFragment(new ParentPendingActivationFragment());
              } else if (status.getStatus().equals(Constants.REJECTED)) {
                Toast.makeText(this, "Your request has been rejected, please contact OGS administration", Toast.LENGTH_LONG).show();
              }
            }, err -> {
              Log.e(TAG, err.getMessage());
              Toast.makeText(this, err.getMessage(), Toast.LENGTH_LONG).show();
            });
      }
    } else if (applicationRole.equals(Constants.USER_ROLE_STAFF)) {
      String staffStatus = PrefsUtil.getStaffStatus(this);

      if (staffStatus.equals(Constants.ACTIVE)) {
        startDefaultStaffActivity();
      } else {
        replaceFragment(new StaffSetupFragment());
      }
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (mDisposable != null && !mDisposable.isDisposed()) {
      mDisposable.dispose();
    }
  }

  public void replaceFragment(Fragment fragment) {
    replaceFragment(fragment, false);
  }

  public void replaceFragment(Fragment fragment, boolean replace) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.fragment_container, fragment);
    if (replace) {
      fragmentTransaction.addToBackStack(null);
    }
    fragmentTransaction.commit();
  }

  @Override
  public void onSetupRoleSelected(int id) {
    switch(id) {
      case R.id.parent_setup:
        replaceFragment(new ParentSetupFragment(), true);
        break;
      case R.id.staff_setup:
        replaceFragment(new StaffSetupFragment(), true);
        break;
    }
  }

  @Override
  public void onCreateParentSetup(Parent parent) {
    try {
      JSONObject json = parent.toJsonObject();

      mDisposable = mHttp.postParent(json)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(parentResponse -> {
                PrefsUtil.setUserRole(this, Constants.USER_ROLE_PARENT);
                PrefsUtil.setParentStatus(this, Constants.PENDING_ACTIVATION);
                PrefsUtil.setAuthToken(this, parentResponse.getToken());
                replaceFragment(new ParentPendingActivationFragment());
            },
            err -> {
              Log.e(TAG, err.getMessage());
              Toast.makeText(this, "An unexpected error, please try again", Toast.LENGTH_SHORT).show();
            });
    } catch (JSONException e) {
      Toast.makeText(this, "An unexpected error, please try again", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onCreateStaffSetup(Staff staff) {
    try {
      JSONObject json = staff.toJsonObject();

      mDisposable = mHttp.postStaff(json)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(tokenResponse -> {
                PrefsUtil.setUserRole(this, Constants.USER_ROLE_STAFF);
                PrefsUtil.setStaffStatus(this, Constants.ACTIVE);
                PrefsUtil.setAuthToken(this, tokenResponse.getToken());
                startDefaultStaffActivity();
              },
              err -> {
                Log.e(TAG, err.getMessage());
                Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
              });
    } catch (JSONException e) {
      Toast.makeText(this, "An unexpected error, please try again", Toast.LENGTH_SHORT).show();
    }

  }

  private void startTrackingLocation() {
    if (ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]
              {Manifest.permission.ACCESS_FINE_LOCATION},
          REQUEST_START_TRACKING_PERMISSION);
    } else {
      setAlarms();

      Intent intent = new Intent(this, LocationTrackerServiceRxOkHTTP.class);
      startService(intent);

      replaceFragment(new ParentActiveFragment());
    }
  }

  private void setAlarms() {
    // Start tracking
    alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(this, AlarmReceiver.class);
    intent.putExtra(Constants.ALARM_TYPE, Constants.ALARM_TYPE_START_MONITORING);
    alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    calendar.set(Calendar.HOUR_OF_DAY, 15);
    calendar.set(Calendar.MINUTE, 00);

    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
        AlarmManager.INTERVAL_DAY, alarmIntent);

    // Stop tracking
    intent = new Intent(this, AlarmReceiver.class);
    intent.putExtra(Constants.ALARM_TYPE, Constants.ALARM_TYPE_STOP_MONITORING);
    alarmIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    calendar.set(Calendar.HOUR_OF_DAY, 16);
    calendar.set(Calendar.MINUTE, 00);

    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
        AlarmManager.INTERVAL_DAY, alarmIntent);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    switch (requestCode) {
      case REQUEST_START_TRACKING_PERMISSION:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          startTrackingLocation();
        }
    }
  }

  private void startDefaultStaffActivity() {
    Intent intent = new Intent(this, MapsActivity.class);
    startActivity(intent);
  }

//  private void doServiceControl() {
//    ApplicationWrapper.nextError(new Date().toString());
//    Intent intent = new Intent(this, LocationTrackerServiceRxOkHTTP.class);
//    boolean serviceRunning = isMyServiceRunning(LocationTrackerServiceRxOkHTTP.class);
//    if (!serviceRunning) {
//      startService(intent);
////      mServiceButton.setText(R.string.stop_service);
//
//    } else {
//      stopService(intent);
////      mServiceButton.setText(R.string.start_service);
//    }
//  }
//
//  private boolean isMyServiceRunning(Class<?> serviceClass) {
//    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//      if (serviceClass.getName().equals(service.service.getClassName())) {
//        return true;
//      }
//    }
//    return false;
//  }
}
