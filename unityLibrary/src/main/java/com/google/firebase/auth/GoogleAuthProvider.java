package com.google.firebase.auth;

public class GoogleAuthProvider {
  public static final String GOOGLE_SIGN_IN_METHOD = "google.com";
  public static final String PROVIDER_ID = "google.com";

  private GoogleAuthProvider() {
    super();
  }

  public static AuthCredential getCredential(final String s, final String s2) {
    throw new UnsupportedOperationException("Stub");
  }
}
