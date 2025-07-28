package com.google.firebase.messaging;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

public class FirebaseMessaging {
  public static FirebaseMessaging getInstance() {
    Log.i("Firebase", "FirebaseMessaging.getInstance");
    return new FirebaseMessaging();
  }

  public boolean isAutoInitEnabled() {
    return false;
  }

  public void setAutoInitEnabled(final boolean enabled) {
  }

  public Task<Void> subscribeToTopic(final String s) {
    return Tasks.forResult(null);
  }

  public Task<Void> unsubscribeFromTopic(final String s) {
    return Tasks.forResult(null);
  }

  public boolean deliveryMetricsExportToBigQueryEnabled() {
    return false;
  }

  public void setDeliveryMetricsExportToBigQuery(final boolean deliveryMetricsExportToBigQuery) {
  }

  public Task<String> getToken() {
    return Tasks.forResult("42");
  }

  public Task<Void> deleteToken() {
    return Tasks.forResult(null);
  }
}
