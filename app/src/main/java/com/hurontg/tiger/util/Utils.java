package com.hurontg.tiger.util;

import android.text.TextUtils;
import android.util.Patterns;

public class Utils {

  public final static boolean isValidName(CharSequence target) {
    return !TextUtils.isEmpty(target);
  }

  public final static boolean isValidEmail(CharSequence target) {
    return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
  }

  public final static boolean isValidPhone(CharSequence target) {
    return !TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches();
  }

  public static boolean isValidPassword(CharSequence password1, CharSequence password2) {
    return
        !TextUtils.isEmpty(password1) && !TextUtils.isEmpty(password2)
            && password1.toString().trim().equals(password2.toString().trim());
  }

  public static void putErrorMessage(boolean error, String message, StringBuilder sb) {
    if (error) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(message);
    }
  }

}
