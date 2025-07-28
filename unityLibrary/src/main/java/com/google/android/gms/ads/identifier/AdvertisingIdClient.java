package com.google.android.gms.ads.identifier;

import android.content.Context;

public class AdvertisingIdClient {
  public static Info getAdvertisingIdInfo(Context object) {
    return new Info("", false);
  }

  public static final class Info {
    private final String zza;
    private final boolean zzb;

    @Deprecated
    public Info(String string, boolean bl) {
      this.zza = string;
      this.zzb = bl;
    }

    public String getId() {
      return this.zza;
    }

    public boolean isLimitAdTrackingEnabled() {
      return this.zzb;
    }

    public String toString() {
      String string = this.zza;
      boolean bl = this.zzb;
      StringBuilder stringBuilder = new StringBuilder(String.valueOf(string).length() + 7);
      stringBuilder.append("{");
      stringBuilder.append(string);
      stringBuilder.append("}");
      stringBuilder.append(bl);
      return stringBuilder.toString();
    }
  }

}
