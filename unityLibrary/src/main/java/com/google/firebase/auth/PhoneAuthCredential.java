package com.google.firebase.auth;


import android.annotation.SuppressLint;
import android.os.Parcel;

import androidx.annotation.NonNull;

@SuppressLint("ParcelCreator")
public class PhoneAuthCredential extends AuthCredential {
  @Override
  public void writeToParcel(@NonNull Parcel parcel, int i) {
    throw new UnsupportedOperationException("Stub");
  }

  public String getProvider() {
    return "phone";
  }

  public String getSignInMethod() {
    return "phone";
  }

  public String getSmsCode() {
    return "";
  }

  @Override
  public AuthCredential zza() {
    return null;
  }
}
