package com.google.firebase.auth.internal.cpp;

import com.google.firebase.auth.FirebaseAuth;

public class JniAuthStateListener implements FirebaseAuth.AuthStateListener {
  public JniAuthStateListener(final long cppAuthData) {
  }

  private native void nativeOnAuthStateChanged(final long p0);

  public void disconnect() {
  }

  public void onAuthStateChanged(final FirebaseAuth firebaseAuth) {
  }
}
