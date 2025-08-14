package com.android.billingclient.api;

import android.util.Log;

public final class ConsumeParams {
  private String zza;

  private ConsumeParams() {
    super();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public String getPurchaseToken() {
    return this.zza;
  }

  public static final class Builder {
    private String zza;

    private Builder() {
      super();
    }

    public ConsumeParams build() {
      return new ConsumeParams();
    }

    public Builder setPurchaseToken(String var1) {
      Log.d("ConsumeParams", "setPurchaseToken: " + var1);
      this.zza = var1;
      return this;
    }
  }
}
