package com.google.firebase.crashlytics;

import android.util.Log;

import com.google.firebase.crashlytics.internal.common.CrashlyticsCore;

public class FirebaseCrashlytics {
  public final CrashlyticsCore core = new CrashlyticsCore();

  public static FirebaseCrashlytics getInstance() {
    Log.i("Firebase", "FirebaseCrashlytics.getInstance");
    return new FirebaseCrashlytics();
  }

  public void log(final String s) {
    Log.i("Crashlytics", s);
  }

  public void setCustomKey(final String s, final double d) {
    Log.i("Crashlytics", "setCustomKey " + s + " = " + d);
  }

  public void setCustomKey(final String s, final float f) {
    Log.i("Crashlytics", "setCustomKey " + s + " = " + f);
  }

  public void setCustomKey(final String s, final int i) {
    Log.i("Crashlytics", "setCustomKey " + s + " = " + i);
  }

  public void setCustomKey(final String s, final long i) {
    Log.i("Crashlytics", "setCustomKey " + s + " = " + i);
  }

  public void setCustomKey(final String s, final String s2) {
    Log.i("Crashlytics", "setCustomKey " + s + " = " + s2);
  }

  public void setCustomKey(final String s, final boolean b) {
    Log.i("Crashlytics", "setCustomKey " + s + " = " + b);
  }

  public void setUserId(final String userId) {
  }

  public void recordException(final Throwable t) {
    Log.e("Crashlytics", "recordException", t);
  }

  public void setCrashlyticsCollectionEnabled(final boolean b) {
  }
}
