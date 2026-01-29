package com.android.billingclient.api;

public final class AccountIdentifiers {
  private final String zza;
  private final String zzb;

  AccountIdentifiers(String var1, String var2) {
    super();
    this.zza = var1;
    this.zzb = var2;
  }

  public String getObfuscatedAccountId() {
    return this.zza;
  }

  public String getObfuscatedProfileId() {
    return this.zzb;
  }
}
