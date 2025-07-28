package com.google.android.gms.common;

import android.content.Context;

public class GoogleApiAvailability {
  public static GoogleApiAvailability getInstance() {
    return new GoogleApiAvailability();
  }

  public int isGooglePlayServicesAvailable(final Context context) {
    return 0;
  }
}
