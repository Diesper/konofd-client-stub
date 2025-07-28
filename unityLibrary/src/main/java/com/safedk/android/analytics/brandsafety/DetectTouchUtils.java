package com.safedk.android.analytics.brandsafety;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DetectTouchUtils {
  public static void activityOnTouch(String param0, MotionEvent param1) {
    Log.d("DetectTouchUtils", "activityOnTouch: " + param0 + ", " + param1);
  }

  public static void viewOnTouch(String var0, View var1, MotionEvent var2) {
    Log.d("DetectTouchUtils", "viewOnTouch: " + var0 + ", " + var1 + ", " + var2);
  }
}
