package com.android.billingclient.api;

import android.util.Log;

public final class AcknowledgePurchaseParams {
  private String zza;

  private AcknowledgePurchaseParams() {
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

    public AcknowledgePurchaseParams build() {
      return new AcknowledgePurchaseParams();
    }

    public Builder setPurchaseToken(String var1) {
      Log.d("AcknowledgePurchaseParams", "setPurchaseToken: " + var1);
      this.zza = var1;
      return this;
    }
  }
}
