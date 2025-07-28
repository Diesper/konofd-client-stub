package com.google.firebase.auth.internal.cpp;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class JniAuthPhoneListener extends PhoneAuthProvider.OnVerificationStateChangedCallbacks {
  public JniAuthPhoneListener(final long cListener) {
    super();
  }

  public void disconnect() {
  }

  private native void nativeOnCodeAutoRetrievalTimeOut(final long p0, final String p1);

  private native void nativeOnCodeSent(final long p0, final String p1, final PhoneAuthProvider.ForceResendingToken p2);

  private native void nativeOnVerificationCompleted(final long p0, final PhoneAuthCredential p1);

  private native void nativeOnVerificationFailed(final long p0, final String p1);

  @Override
  public void onVerificationCompleted(PhoneAuthCredential p0) {
  }

  @Override
  public void onVerificationFailed(FirebaseException p0) {
  }
}
