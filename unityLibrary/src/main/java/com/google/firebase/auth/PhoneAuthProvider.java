package com.google.firebase.auth;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.FirebaseException;

import java.util.concurrent.TimeUnit;

public class PhoneAuthProvider {
  public static PhoneAuthProvider getInstance(final FirebaseAuth firebaseAuth) {
    Log.i("Firebase", "PhoneAuthProvider.getInstance");
    return new PhoneAuthProvider();
  }

  public static PhoneAuthCredential getCredential(String string, String string2) {
    throw new UnsupportedOperationException("Stub");
  }

  public void verifyPhoneNumber(String string, long l, TimeUnit timeUnit, Activity activity, OnVerificationStateChangedCallbacks onVerificationStateChangedCallbacks, ForceResendingToken forceResendingToken) {
    throw new UnsupportedOperationException("Stub");
  }

  public static class ForceResendingToken {
    ForceResendingToken() {
      super();
    }

    public static ForceResendingToken zza() {
      return new ForceResendingToken();
    }
  }

  public abstract static class OnVerificationStateChangedCallbacks {
    public OnVerificationStateChangedCallbacks() {
      super();
    }

    public void onCodeAutoRetrievalTimeOut(final String s) {
    }

    public void onCodeSent(final String s, final PhoneAuthProvider.ForceResendingToken phoneAuthProvider$ForceResendingToken) {
    }

    public abstract void onVerificationCompleted(final PhoneAuthCredential p0);

    public abstract void onVerificationFailed(final FirebaseException p0);
  }

}
