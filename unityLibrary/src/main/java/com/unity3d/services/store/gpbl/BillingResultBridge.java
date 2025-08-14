package com.unity3d.services.store.gpbl;

public class BillingResultBridge {
  private static final String getResponseCodeMethodName = "getResponseCode";
  private final Object _billingResult;

  public BillingResultBridge(Object var1) {
    this._billingResult = var1;
  }

  protected String getClassName() {
    return "com.android.billingclient.api.BillingResult";
  }

  public BillingResultResponseCode getResponseCode() {
    return BillingResultResponseCode.OK;
  }
}
