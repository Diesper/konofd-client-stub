package com.google.android.gms.ads;

public final class LoadAdError
  extends AdError {
  private final ResponseInfo zza;

  public LoadAdError(int n, String string, String string2, AdError adError, ResponseInfo responseInfo) {
    super(n, string, string2, adError);
    this.zza = responseInfo;
  }

  public ResponseInfo getResponseInfo() {
    return this.zza;
  }
}
