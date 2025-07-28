package com.google.android.gms.ads;

import java.util.Locale;

public class VersionInfo {
  protected final int zza;
  protected final int zzb;
  protected final int zzc;

  public VersionInfo(int n, int n2, int n3) {
    this.zza = n;
    this.zzb = n2;
    this.zzc = n3;
  }

  public int getMajorVersion() {
    return this.zza;
  }

  public int getMicroVersion() {
    return this.zzc;
  }

  public int getMinorVersion() {
    return this.zzb;
  }

  public String toString() {
    return String.format(Locale.US, "%d.%d.%d", this.zza, this.zzb, this.zzc);
  }
}
