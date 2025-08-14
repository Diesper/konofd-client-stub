package com.android.billingclient.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Purchase {
  private final String zza;
  private final String zzb;
  private final JSONObject zzc;

  public Purchase(String var1, String var2) throws JSONException {
    super();
    this.zza = var1;
    this.zzb = var2;
    this.zzc = new JSONObject(var1);
  }
}
