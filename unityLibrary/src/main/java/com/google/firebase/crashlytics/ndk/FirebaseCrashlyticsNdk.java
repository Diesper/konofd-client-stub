package com.google.firebase.crashlytics.ndk;

import android.util.Log;

public class FirebaseCrashlyticsNdk {
  public static FirebaseCrashlyticsNdk getInstance() {
    Log.i("Firebase", "FirebaseCrashlyticsNdk.getInstance");
    return new FirebaseCrashlyticsNdk();
  }

  public void installSignalHandler() {
  }
}
