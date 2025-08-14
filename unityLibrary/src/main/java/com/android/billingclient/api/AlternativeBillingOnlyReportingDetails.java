package com.android.billingclient.api;

import org.json.JSONException;
import org.json.JSONObject;

public final class AlternativeBillingOnlyReportingDetails {
  private final String externalTransactionToken;

  AlternativeBillingOnlyReportingDetails(String var1) throws JSONException {
    this.externalTransactionToken = new JSONObject(var1).optString("externalTransactionToken");
  }

  public String getExternalTransactionToken() {
    return this.externalTransactionToken;
  }
}
