/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.android.gms.ads.internal.client.zze
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.google.android.gms.ads;

public class AdError {
  public static final String UNDEFINED_DOMAIN = "undefined";
  private final int zza;
  private final String zzb;
  private final String zzc;
  private final AdError zzd;

  public AdError(int n, String string, String string2) {
    this(n, string, string2, null);
  }

  public AdError(int n, String string, String string2, AdError adError) {
    this.zza = n;
    this.zzb = string;
    this.zzc = string2;
    this.zzd = adError;
  }

  public AdError getCause() {
    return this.zzd;
  }

  public int getCode() {
    return this.zza;
  }

  public String getDomain() {
    return this.zzc;
  }

  public String getMessage() {
    return this.zzb;
  }

  public String toString() {
    throw new UnsupportedOperationException("Stub");
  }
}
