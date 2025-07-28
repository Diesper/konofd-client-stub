package com.google.firebase.auth;

public class TwitterAuthProvider {
  public static final String PROVIDER_ID = "twitter.com";
  public static final String TWITTER_SIGN_IN_METHOD = "twitter.com";

  private TwitterAuthProvider() {
    super();
  }

  public static AuthCredential getCredential(final String s, final String s2) {
    throw new UnsupportedOperationException("Stub");
  }
}
