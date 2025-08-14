package com.android.billingclient.api;

import org.json.JSONException;
import org.json.JSONObject;

public final class ExternalOfferReportingDetails {
  private final String externalTransactionToken;

  ExternalOfferReportingDetails(String var1) throws JSONException {
    super();
    this.externalTransactionToken = new JSONObject(var1).optString("externalTransactionToken");
  }

  public String getExternalTransactionToken() {
    return this.externalTransactionToken;
  }
}
