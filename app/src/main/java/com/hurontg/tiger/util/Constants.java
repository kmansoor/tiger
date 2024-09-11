package com.hurontg.tiger.util;

import com.google.android.gms.maps.model.LatLng;

public class Constants {
  public static final String SERVER_URL_KEY = "SERVER_URL_KEY";
//  public static final String SERVER_URL = "https://htgkdpck.azurewebsites.net/";
  public static final String SERVER_URL = "http://192.168.0.21:3000/";

  public static final String PARENT_SERVER_URL = SERVER_URL + "parent";
  public static final String PARENT_STATUS_URL = PARENT_SERVER_URL + "/status/";
  public static final String PARENT_LOCATION_URL = PARENT_SERVER_URL + "/location";

  public static final String STAFF_SERVER_URL = SERVER_URL + "staff";

  public static final String ADMIN_PENDING_ACTIVATION = SERVER_URL + "admin/clients/pendingactivation";
  public static final String ADMIN_APPLICATION_STATUS = SERVER_URL + "admin/clients/application/status";

  public static final String USER_ROLE = "USER_ROLE";
  public static final String USER_ROLE_NOT_SELECTED = "USER_ROLE_NOT_SELECTED";
  public static final String USER_ROLE_PARENT = "ROLE_PARENT";
  public static final String USER_ROLE_STAFF = "ROLE_STAFF";

  public static final String PARENT_STATUS = "PARENT_STATUS";
  public static final String NOT_INITIALIZED = "NOT_INITIALIZED";
  public static final String PENDING_ACTIVATION = "PENDING_ACTIVATION";
  public static final String ACTIVE = "ACTIVE";
  public static final String INACTIVE = "INACTIVE";
  public static final String REJECTED = "REJECTED";

  public static final String AUTH_TOKEN = "AUTH_TOKEN";

  public static final String STAFF_STATUS = "STAFF_STATUS";

  public static final String CAMPUS = "CAMPUS";
  public static final int CAMPUS_PRIMARY = 1;
  public static final int CAMPUS_SECONDARY = 2;

  public static final String CAMPUS_PRIMARY_TITLE = "OGS - Primary";
  public static final String CAMPUS_SECONDARY_TITLE = "OGS - Secondary";

  public static final String REGIONS = "REGIONS";

  public static final String ALARM_TYPE = "ALARM_TYPE";
  public static final String ALARM_TYPE_START_MONITORING = "ALARM_TYPE_START_MONITORING";
  public static final String ALARM_TYPE_STOP_MONITORING = "ALARM_TYPE_STOP_MONITORING";

  public static final int ZOOM = 19;

  public final static int LOCATION_TRACKING_INTERVAL = 5000;
  public static final long LOCATION_EXPIRATION_DURATION = 1000*60*60;
  public static final float SMALLEST_DISPLACEMENT = 1;

  public static final LatLng getLatLngByCampus(int campus) {
    return campus == CAMPUS_PRIMARY ?
        new LatLng(43.520134, -79.657928) :
        new LatLng(43.521858, -79.656603);
  }

  public static final String getCampusTitle(int campus) {
    return campus == CAMPUS_PRIMARY ?
        Constants.CAMPUS_PRIMARY_TITLE :
        Constants.CAMPUS_SECONDARY_TITLE;
  }
}
