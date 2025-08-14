package com.android.billingclient.api;

import android.util.Log;

public class BillingResult {
  private int zza;
  private String zzb;

  public static Builder newBuilder() {
    return new Builder();
  }

  public String getDebugMessage() {
    return this.zzb;
  }

  public int getResponseCode() {
    return this.zza;
  }

  public static class Builder {
    private int zza;
    private String zzb = "";

    public BillingResult build() {
      BillingResult var1 = new BillingResult();
      var1.zza = this.zza;
      var1.zzb = this.zzb;
      return var1;
    }

    public Builder setDebugMessage(String var1) {
      Log.d("BillingClient", "setDebugMessage: " + var1);
      this.zzb = var1;
      return this;
    }

    public Builder setResponseCode(int var1) {
      Log.d("BillingClient", "setResponseCode: " + var1);
      this.zza = var1;
      return this;
    }
  }
}
