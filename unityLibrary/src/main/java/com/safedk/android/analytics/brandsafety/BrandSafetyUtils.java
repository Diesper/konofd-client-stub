package com.safedk.android.analytics.brandsafety;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;

public class BrandSafetyUtils {
  public static void onShouldOverrideUrlLoading(String var0, WebView var1, String var2, boolean var3) {
    Log.d("BrandSafetyUtils", "onShouldOverrideUrlLoading: " + var0 + ", " + var1 + ", " + var2 + ", " + var3);
  }

  public static void detectAdClick(Intent var0, String var1) {
    Log.d("BrandSafetyUtils", "detectAdClick: " + var0 + ", " + var1);
  }

  public static void detectAdClick(Uri var0, String var1) {
    Log.d("BrandSafetyUtils", "detectAdClick: " + var0 + ", " + var1);
  }
}
