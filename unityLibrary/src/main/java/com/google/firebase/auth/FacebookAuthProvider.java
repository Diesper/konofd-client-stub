package com.google.firebase.auth;

public class FacebookAuthProvider {
  public static final String FACEBOOK_SIGN_IN_METHOD = "facebook.com";
  public static final String PROVIDER_ID = "facebook.com";

  private FacebookAuthProvider() {
    super();
  }

  public static AuthCredential getCredential(final String s) {
    throw new UnsupportedOperationException("Stub");
  }
}
