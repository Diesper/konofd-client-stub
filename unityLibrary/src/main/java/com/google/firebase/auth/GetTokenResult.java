package com.google.firebase.auth;

import java.util.Map;

public class GetTokenResult {
  private String zza;
  private Map<String, Object> zzb;

  public GetTokenResult(final String zza, final Map<String, Object> zzb) {
    super();
    this.zza = zza;
    this.zzb = zzb;
  }

  private final long zza(final String s) {
    final Integer n = (Integer) this.zzb.get(s);
    if(n == null) {
      return 0L;
    }
    return n;
  }

  public long getAuthTimestamp() {
    return this.zza("auth_time");
  }

  public Map<String, Object> getClaims() {
    return this.zzb;
  }

  public long getExpirationTimestamp() {
    return this.zza("exp");
  }

  public long getIssuedAtTimestamp() {
    return this.zza("iat");
  }

  public String getSignInProvider() {
    final Map map = (Map) this.zzb.get("firebase");
    if(map != null) {
      return (String) map.get("sign_in_provider");
    }
    return null;
  }

  public String getSignInSecondFactor() {
    final Map map = (Map) this.zzb.get("firebase");
    if(map != null) {
      return (String) map.get("sign_in_second_factor");
    }
    return null;
  }

  public String getToken() {
    return this.zza;
  }
}
