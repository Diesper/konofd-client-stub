package com.google.firebase;

import android.content.Context;
import android.util.Log;

public class FirebaseApp {
  public static FirebaseApp initializeApp(Context applicationContext, final FirebaseOptions firebaseOptions) {
    return new FirebaseApp();
  }

  public static FirebaseApp initializeApp(Context applicationContext, final FirebaseOptions firebaseOptions, String normalize) {
    return new FirebaseApp();
  }

  public static FirebaseApp getInstance() {
    Log.i("Firebase", "FirebaseApp.getInstance");
    return new FirebaseApp();
  }

  public static FirebaseApp getInstance(String format) {
    return new FirebaseApp();
  }

  public String getName() {
    return "";
  }

  public FirebaseOptions getOptions() {
    return new FirebaseOptions("", "", "", "", "", "", "");
  }

  public void delete() {
  }

  public boolean isDataCollectionDefaultEnabled() {
    return false;
  }

  public void setDataCollectionDefaultEnabled(final boolean b) {
  }
}
