package com.hurontg.tiger.main;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import com.hurontg.tiger.R;

public class TestBedActivity extends AppCompatActivity {

  private NotificationManager mNotificationManager;
  private static final int NOTIFICATION_ID = 0;
  private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test_bed);

//    mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

//    createNotificationChannel();

//    deliverNotification(this);
  }

  public void createNotificationChannel() {

    // Create a notification manager object.
//    mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    // Notification channels are only available in OREO and higher.
    // So, add a check on SDK version.
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

      // Create the NotificationChannel with all the parameters.
      NotificationChannel notificationChannel = new NotificationChannel
          (PRIMARY_CHANNEL_ID,
              "Stand up notification",
              NotificationManager.IMPORTANCE_HIGH);

      notificationChannel.enableLights(true);
      notificationChannel.setLightColor(Color.RED);
      notificationChannel.enableVibration(true);
      notificationChannel.setDescription
          ("Notifies every 15 minutes to stand up and walk");
      mNotificationManager.createNotificationChannel(notificationChannel);
    }
  }

  private void deliverNotification(Context context) {
    Intent contentIntent = new Intent(context, TestBedActivity.class);

    PendingIntent contentPendingIntent = PendingIntent.getActivity
        (context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_android)
        .setContentTitle("Stand Up Alert")
        .setContentText("You should stand up and walk around now!")
        .setContentIntent(contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setDefaults(NotificationCompat.DEFAULT_ALL);

    mNotificationManager.notify(NOTIFICATION_ID, builder.build());


  }

}
