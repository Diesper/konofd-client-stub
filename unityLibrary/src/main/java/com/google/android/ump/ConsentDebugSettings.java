package com.google.android.ump;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ConsentDebugSettings {
  private final boolean zza;
  private final int zzb;

  /* synthetic */ ConsentDebugSettings(boolean bl, Builder builder) {
    this.zza = bl;
    this.zzb = Builder.zza((Builder) builder);
  }

  public int getDebugGeography() {
    return this.zzb;
  }

  public boolean isTestDevice() {
    return this.zza;
  }


  public static class Builder {
    private final List zza = new ArrayList();
    private final Context zzb;
    private int zzc = 0;
    private boolean zzd;

    public Builder(Context context) {
      this.zzb = context.getApplicationContext();
    }

    static /* bridge */ /* synthetic */ int zza(Builder builder) {
      return builder.zzc;
    }

    public Builder addTestDeviceHashedId(String string) {
      this.zza.add(string);
      return this;
    }

    public ConsentDebugSettings build() {
      boolean bl;
      Context context = this.zzb;
      List list = this.zza;
      return new ConsentDebugSettings(false, this);
    }

    public Builder setDebugGeography(int n) {
      this.zzc = n;
      return this;
    }

    public Builder setForceTesting(boolean bl) {
      this.zzd = bl;
      return this;
    }
  }

}
