package com.google.firebase.auth;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Parcel;

import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

@SuppressLint("ParcelCreator")
public class UserProfileChangeRequest extends AbstractSafeParcelable {
  private String zza;
  private String zzb;
  private boolean zzc;
  private boolean zzd;
  private Uri zze;

  UserProfileChangeRequest(final String zza, final String zzb, final boolean zzc, final boolean zzd) {
    super();
    this.zza = zza;
    this.zzb = zzb;
    this.zzc = zzc;
    this.zzd = zzd;
    this.zze = Uri.parse(zzb);
  }

  public String getDisplayName() {
    return this.zza;
  }

  public Uri getPhotoUri() {
    return this.zze;
  }

  public final void writeToParcel(final Parcel parcel, int beginObjectHeader) {
    beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
    SafeParcelWriter.writeString(parcel, 2, this.getDisplayName(), false);
    SafeParcelWriter.writeString(parcel, 3, this.zzb, false);
    SafeParcelWriter.writeBoolean(parcel, 4, this.zzc);
    SafeParcelWriter.writeBoolean(parcel, 5, this.zzd);
    SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
  }

  public final String zza() {
    return this.zzb;
  }

  public final boolean zzb() {
    return this.zzc;
  }

  public final boolean zzc() {
    return this.zzd;
  }


  public static class Builder {
    private String zza;
    private Uri zzb;
    private boolean zzc;
    private boolean zzd;

    public Builder() {
      super();
    }

    public UserProfileChangeRequest build() {
      final String zza = this.zza;
      final Uri zzb = this.zzb;
      String string;
      if(zzb == null) {
        string = null;
      } else {
        string = zzb.toString();
      }
      return new UserProfileChangeRequest(zza, string, this.zzc, this.zzd);
    }

    public String getDisplayName() {
      return this.zza;
    }

    public Uri getPhotoUri() {
      return this.zzb;
    }

    public Builder setDisplayName(final String zza) {
      if(zza == null) {
        this.zzc = true;
      } else {
        this.zza = zza;
      }
      return this;
    }

    public Builder setPhotoUri(final Uri zzb) {
      if(zzb == null) {
        this.zzd = true;
      } else {
        this.zzb = zzb;
      }
      return this;
    }
  }

}
