package com.google.firebase.auth;

public class EmailAuthProvider {
  public static final String EMAIL_LINK_SIGN_IN_METHOD = "emailLink";
  public static final String EMAIL_PASSWORD_SIGN_IN_METHOD = "password";
  public static final String PROVIDER_ID = "password";

  private EmailAuthProvider() {
    super();
  }

  public static AuthCredential getCredential(final String s, final String s2) {
    throw new UnsupportedOperationException("Stub");
  }

  public static AuthCredential getCredentialWithLink(final String s, final String s2) {
    throw new UnsupportedOperationException("Stub");
  }
}
