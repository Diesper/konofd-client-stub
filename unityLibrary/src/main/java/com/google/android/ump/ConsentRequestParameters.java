package com.google.android.ump;

public class ConsentRequestParameters {

  public static class Builder {
    private boolean zza;
    private String zzb;
    private ConsentDebugSettings zzc;

    static /* bridge */ /* synthetic */ ConsentDebugSettings zza(ConsentRequestParameters.Builder builder) {
      return builder.zzc;
    }

    static /* bridge */ /* synthetic */ String zzb(ConsentRequestParameters.Builder builder) {
      return builder.zzb;
    }

    static /* bridge */ /* synthetic */ boolean zzc(ConsentRequestParameters.Builder builder) {
      return builder.zza;
    }

    public ConsentRequestParameters build() {
      return new ConsentRequestParameters();
    }

    public ConsentRequestParameters.Builder setAdMobAppId(String string) {
      this.zzb = string;
      return this;
    }

    public ConsentRequestParameters.Builder setConsentDebugSettings(ConsentDebugSettings consentDebugSettings) {
      this.zzc = consentDebugSettings;
      return this;
    }

    public ConsentRequestParameters.Builder setTagForUnderAgeOfConsent(boolean bl) {
      this.zza = bl;
      return this;
    }
  }

}
