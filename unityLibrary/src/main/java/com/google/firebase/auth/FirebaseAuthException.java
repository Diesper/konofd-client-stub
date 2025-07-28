package com.google.firebase.auth;

import com.google.firebase.FirebaseException;

public class FirebaseAuthException extends FirebaseException {
  private final String zza;

  public FirebaseAuthException(final String s, final String s2) {
    super(s2);
    this.zza = s;
  }

  public String getErrorCode() {
    return this.zza;
  }
}
