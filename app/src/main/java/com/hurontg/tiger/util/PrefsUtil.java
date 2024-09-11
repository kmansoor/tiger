package com.hurontg.tiger.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsUtil {
  private static SharedPreferences getSharedPreferences(Context context) {
    return context.getSharedPreferences("TigerPrefs", Context.MODE_PRIVATE);
  }

  public static String getApplicationRole(Context context) {
    return getSharedPreferences(context).getString(Constants.USER_ROLE, Constants.USER_ROLE_NOT_SELECTED);
  }

  public static String getParentStatus(Context context) {
    return getSharedPreferences(context).getString(Constants.PARENT_STATUS, Constants.NOT_INITIALIZED);
  }

  public static String getParentAuthToken(Context context) {
    return getSharedPreferences(context).getString(Constants.AUTH_TOKEN, null);
  }

  public static String getStaffStatus(Context context) {
    return getSharedPreferences(context).getString(Constants.STAFF_STATUS, Constants.NOT_INITIALIZED);
  }

  public static int getCampus(Context context) {
    return getSharedPreferences(context).getInt(Constants.CAMPUS, Constants.CAMPUS_PRIMARY);
  }

  public static void setCampus(Context context, int campus) {
    SharedPreferences.Editor editor = getSharedPreferences(context).edit();
    editor.putInt(Constants.CAMPUS, campus);
    editor.commit();
  }

  public static String getAuthToken(Context context) {
    return getSharedPreferences(context).getString(Constants.AUTH_TOKEN, null);
  }

  public static void setParentStatus(Context context, String status) {
    SharedPreferences.Editor editor = getSharedPreferences(context).edit();
    editor.putString(Constants.PARENT_STATUS, status);
    editor.commit();
  }

  public static void setRegions(Context context, String regions) {
    SharedPreferences.Editor editor = getSharedPreferences(context).edit();
    editor.putString(Constants.REGIONS, regions);
    editor.commit();
  }

  public static void setUserRole(Context context, String role) {
    SharedPreferences.Editor editor = getSharedPreferences(context).edit();
    editor.putString(Constants.USER_ROLE, role);
    editor.commit();
  }

  public static void setAuthToken(Context context, String token) {
    SharedPreferences.Editor editor = getSharedPreferences(context).edit();
    editor.putString(Constants.AUTH_TOKEN, token);
    editor.commit();
  }


  public static void setStaffStatus(Context context, String status) {
    SharedPreferences.Editor editor = getSharedPreferences(context).edit();
    editor.putString(Constants.STAFF_STATUS, status);
    editor.commit();
  }
}
