package com.google.firebase.auth;

import android.app.Activity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FirebaseUser {
  public boolean isAnonymous() {
    return true;
  }

  public Task<GetTokenResult> getIdToken(final boolean b) {
    return Tasks.forResult(new GetTokenResult("", Map.of()));
  }

  public List<? extends UserInfo> getProviderData() {
    return Collections.emptyList();
  }

  public Task<Void> updateEmail(final String s) {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<Void> updatePassword(final String s) {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<Void> updatePhoneNumber(final PhoneAuthCredential phoneAuthCredential) {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<Void> updateProfile(final UserProfileChangeRequest userProfileChangeRequest) {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<AuthResult> linkWithCredential(final AuthCredential authCredential) {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<AuthResult> unlink(final String s) {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<Void> reload() {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<Void> reauthenticate(final AuthCredential authCredential) {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<AuthResult> reauthenticateAndRetrieveData(final AuthCredential authCredential) {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<Void> delete() {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<Void> sendEmailVerification() {
    throw new UnsupportedOperationException("Stub");
  }

  public FirebaseUserMetadata getMetadata() {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<AuthResult> startActivityForLinkWithProvider(final Activity activity, final FederatedAuthProvider federatedAuthProvider) {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<AuthResult> startActivityForReauthenticateWithProvider(final Activity activity, final FederatedAuthProvider federatedAuthProvider) {
    throw new UnsupportedOperationException("Stub");
  }
}
