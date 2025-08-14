package com.android.billingclient.api;

import org.json.JSONException;
import org.json.JSONObject;

public final class BillingConfig {
  private final String countryCode;
  private final String jsonString;
  private final JSONObject parsedJson;

  BillingConfig(String var1) throws JSONException {
    super();
    this.jsonString = var1;
    JSONObject var2 = new JSONObject(var1);
    this.parsedJson = var2;
    this.countryCode = var2.optString("countryCode");
  }

  public String getCountryCode() {
    return this.countryCode;
  }
}
