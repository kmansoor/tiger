package com.hurontg.tiger.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hurontg.tiger.parent.LocationTrackerServiceRxOkHTTP;
import com.hurontg.tiger.util.Constants;

public class AlarmReceiver extends BroadcastReceiver {

  private final String TAG = "AlarmReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    String type = intent.getStringExtra(Constants.ALARM_TYPE);
    Log.e(TAG, "Alarm Received: " + type);

    Intent locationServiceIntent = new Intent(context, LocationTrackerServiceRxOkHTTP.class);

    if (type.equals(Constants.ALARM_TYPE_START_MONITORING)) {
      context.startService(locationServiceIntent);
    } else if(type.equals(Constants.ALARM_TYPE_STOP_MONITORING)) {
      context.stopService(locationServiceIntent);
    } else {
      Log.e("TAG", "Unknown Alarm type received");
    }
  }

}
