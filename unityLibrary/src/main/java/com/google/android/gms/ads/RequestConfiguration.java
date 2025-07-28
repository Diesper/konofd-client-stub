/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.android.gms.ads.RequestConfiguration$Builder
 *  com.google.android.gms.ads.zzh
 */
package com.google.android.gms.ads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestConfiguration {
  public static final String MAX_AD_CONTENT_RATING_G = "G";
  public static final String MAX_AD_CONTENT_RATING_MA = "MA";
  public static final String MAX_AD_CONTENT_RATING_PG = "PG";
  public static final String MAX_AD_CONTENT_RATING_T = "T";
  public static final String MAX_AD_CONTENT_RATING_UNSPECIFIED = "";
  public static final int TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE = 0;
  public static final int TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE = 1;
  public static final int TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED = -1;
  public static final int TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE = 0;
  public static final int TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE = 1;
  public static final int TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED = -1;
  public static final List zza = Arrays.asList("MA", "T", "PG", "G");
  private final int zzb;
  private final int zzc;
  private final String zzd;
  private final List zze;

  /* synthetic */ RequestConfiguration(int n, int n2, String string, List list, zzh zzh2) {
    this.zzb = n;
    this.zzc = n2;
    this.zzd = string;
    this.zze = list;
  }

  public String getMaxAdContentRating() {
    String string;
    String string2 = string = this.zzd;
    if(string != null) return string2;
    string2 = MAX_AD_CONTENT_RATING_UNSPECIFIED;
    return string2;
  }

  public int getTagForChildDirectedTreatment() {
    return this.zzb;
  }

  public int getTagForUnderAgeOfConsent() {
    return this.zzc;
  }

  public List<String> getTestDeviceIds() {
    return new ArrayList<String>(this.zze);
  }

  public Builder toBuilder() {
    Builder builder = new Builder();
    builder.setTagForChildDirectedTreatment(this.zzb);
    builder.setTagForUnderAgeOfConsent(this.zzc);
    builder.setMaxAdContentRating(this.zzd);
    builder.setTestDeviceIds(this.zze);
    return builder;
  }

  public static class Builder {
    private int zza = -1;
    private int zzb = -1;
    private String zzc = null;
    private final List zzd = new ArrayList();

    public RequestConfiguration build() {
      return new RequestConfiguration(this.zza, this.zzb, this.zzc, this.zzd, null);
    }

    public RequestConfiguration.Builder setMaxAdContentRating(String string) {
      return this;
    }

    public RequestConfiguration.Builder setTagForChildDirectedTreatment(int n) {
      return this;
    }

    public RequestConfiguration.Builder setTagForUnderAgeOfConsent(int n) {
      return this;
    }

    public RequestConfiguration.Builder setTestDeviceIds(List<String> list) {
      return this;
    }
  }
}
