package com.google.firebase.auth;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OAuthProvider {
  @Deprecated
  public static AuthCredential getCredential(final String s, final String s2, final String s3) {
    throw new UnsupportedOperationException("Stub");
  }

  public static OAuthProvider.Builder newBuilder(final String anObject, final FirebaseAuth firebaseAuth) {
    throw new UnsupportedOperationException("Stub");
  }

  public static OAuthProvider.CredentialBuilder newCredentialBuilder(final String s) {
    throw new UnsupportedOperationException("Stub");
  }

  public static class Builder {

    public Builder addCustomParameter(final String s, final String s2) {
      return this;
    }

    public Builder addCustomParameters(final Map<String, String> map) {
      return this;
    }

    public OAuthProvider build() {
      throw new UnsupportedOperationException("Stub");
    }

    public List<String> getScopes() {
      return Collections.emptyList();
    }

    public Builder setScopes(final List<String> c) {
      return this;
    }
  }

  public static class CredentialBuilder {
    public AuthCredential build() {
      throw new UnsupportedOperationException("Stub");
    }

    public String getAccessToken() {
      throw new UnsupportedOperationException("Stub");
    }

    public String getIdToken() {
      throw new UnsupportedOperationException("Stub");
    }

    public CredentialBuilder setAccessToken(final String zzc) {
      return this;
    }

    public CredentialBuilder setIdToken(final String zzb) {
      return this;
    }

    public CredentialBuilder setIdTokenWithRawNonce(final String zzb, final String zzd) {
      return this;
    }
  }
}
