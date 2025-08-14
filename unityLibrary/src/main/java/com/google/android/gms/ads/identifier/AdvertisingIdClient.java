package com.google.android.gms.ads.identifier;

import android.content.Context;

import androidx.annotation.NonNull;

public class AdvertisingIdClient {
  public static String advertisingId;

  // See [Wonder.Util.UserData$$GetADID] -> [Wonder.Util.UserData._GetADID_d__34$$MoveNext]
  // It is used to set [Wonder.Util.UserData::advertisingId], which is used in UUID (user token) generation.
  //
  // Returned value must be URL-encoded.
  public static Info getAdvertisingIdInfo(Context object) {
    return new Info(advertisingId, false);
  }

  public static final class Info {
    private final String id;
    private final boolean isLimitAdTrackingEnabled;

    public Info(String id, boolean isLimitAdTrackingEnabled) {
      this.id = id;
      this.isLimitAdTrackingEnabled = isLimitAdTrackingEnabled;
    }

    public String getId() {
      return this.id;
    }

    public boolean isLimitAdTrackingEnabled() {
      return this.isLimitAdTrackingEnabled;
    }

    @NonNull
    public String toString() {
      return "{" + this.id + "}" + this.isLimitAdTrackingEnabled;
    }
  }
}
