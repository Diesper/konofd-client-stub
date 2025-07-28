package com.google.firebase.auth;

public class GithubAuthProvider {
  public static final String GITHUB_SIGN_IN_METHOD = "github.com";
  public static final String PROVIDER_ID = "github.com";

  private GithubAuthProvider() {
    super();
  }

  public static AuthCredential getCredential(final String s) {
    throw new UnsupportedOperationException("Stub");
  }
}
