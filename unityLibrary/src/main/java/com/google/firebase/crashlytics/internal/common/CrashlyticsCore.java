package com.google.firebase.crashlytics.internal.common;

import android.util.Log;

public class CrashlyticsCore {
  private final DataCollectionArbiter dataCollectionArbiter = new DataCollectionArbiter();

  public void log(final String s) {
    Log.i("Crashlytics", "CrashlyticsCore.log: " + s);
  }

  public void logException(final Throwable t) {
    Log.e("Crashlytics", "CrashlyticsCore.logException", t);
  }

  public void logFatalException(final Throwable t) {
    Log.e("Crashlytics", "CrashlyticsCore.logFatalException", t);
  }
}
