package com.google.firebase.auth;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import java.util.Collections;
import java.util.List;

public class FirebaseAuth {
  public static FirebaseAuth getInstance() {
    Log.i("Firebase", "FirebaseAuth.getInstance");
    return new FirebaseAuth();
  }

  public static FirebaseAuth getInstance(final FirebaseApp firebaseApp) {
    Log.i("Firebase", "FirebaseAuth.getInstance");
    return new FirebaseAuth();
  }

  public FirebaseUser getCurrentUser() {
    return new FirebaseUser();
  }

  public String getLanguageCode() {
    return "";
  }

  public void setLanguageCode(final String zzi) {
  }

  public void useAppLanguage() {
  }

  public void addAuthStateListener(final AuthStateListener authStateListener) {
  }

  public void addIdTokenListener(final IdTokenListener idTokenListener) {
  }

  public void removeAuthStateListener(final AuthStateListener authStateListener) {
  }

  public void removeIdTokenListener(final IdTokenListener idTokenListener) {
  }

  public void signOut() {
  }

  public Task<SignInMethodQueryResult> fetchSignInMethodsForEmail(final String s) {
    class SignInMethodQueryResultImpl implements SignInMethodQueryResult {
      @Override
      public List<String> getSignInMethods() {
        return Collections.emptyList();
      }
    }

    return Tasks.forResult(new SignInMethodQueryResultImpl());
  }

  public Task<AuthResult> signInWithCustomToken(final String s) {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<AuthResult> signInWithCredential(AuthCredential zza) {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<AuthResult> signInAnonymously() {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<AuthResult> signInWithEmailAndPassword(final String s, final String s2) {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<AuthResult> signInWithEmailLink(final String s, final String s2) {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<AuthResult> createUserWithEmailAndPassword(final String s, final String s2) {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<Void> sendPasswordResetEmail(final String s) {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<AuthResult> startActivityForSignInWithProvider(final Activity activity, final FederatedAuthProvider federatedAuthProvider) {
    throw new UnsupportedOperationException("Stub");
  }

  public interface AuthStateListener {
    void onAuthStateChanged(final FirebaseAuth p0);
  }

  public interface IdTokenListener {
    void onIdTokenChanged(final FirebaseAuth p0);
  }
}
