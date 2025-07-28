package com.google.firebase.app.internal.cpp;

import android.app.Activity;

public class GoogleApiAvailabilityHelper {
  public static boolean makeGooglePlayServicesAvailable(Activity lock) {
    return false;
  }

  private static void onComplete(final int n, final String s) {
  }

  private static native void onCompleteNative(final int p0, final String p1);

  public static void stopCallbacks() {
  }
}
