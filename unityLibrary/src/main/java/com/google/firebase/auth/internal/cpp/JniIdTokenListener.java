package com.google.firebase.auth.internal.cpp;

import com.google.firebase.auth.FirebaseAuth;

public class JniIdTokenListener implements FirebaseAuth.IdTokenListener {
  public JniIdTokenListener(final long cppAuthData) {
  }

  private native void nativeOnIdTokenChanged(final long p0);

  public void disconnect() {
  }

  public void onIdTokenChanged(final FirebaseAuth firebaseAuth) {
  }
}
