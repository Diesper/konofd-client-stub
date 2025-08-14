package com.unity3d.services.store.gpbl;

public enum BillingResultResponseCode {
  SERVICE_TIMEOUT(-3),
  FEATURE_NOT_SUPPORTED(-2),
  SERVICE_DISCONNECTED(-1),
  OK(0),
  USER_CANCELED(1),
  SERVICE_UNAVAILABLE(2),
  BILLING_UNAVAILABLE(3),
  ITEM_UNAVAILABLE(4),
  DEVELOPER_ERROR(5),
  ERROR(6),
  ITEM_ALREADY_OWNED(7),
  ITEM_NOT_OWNED(8);

  private final int _responseCode;

  BillingResultResponseCode(int var3) {
    this._responseCode = var3;
  }

  public static BillingResultResponseCode fromResponseCode(int var0) {
    BillingResultResponseCode[] var3 = values();
    int var2 = var3.length;

    for(int var1 = 0; var1 < var2; ++var1) {
      BillingResultResponseCode var4 = var3[var1];
      if(var4.getResponseCode() == var0) {
        return var4;
      }
    }

    return null;
  }

  public int getResponseCode() {
    return this._responseCode;
  }
}
