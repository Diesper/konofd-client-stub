package com.android.billingclient.api;

import android.app.Activity;
import android.util.Log;

import java.util.Collections;

public class BillingClientImpl extends BillingClient {
  @Override
  public void acknowledgePurchase(AcknowledgePurchaseParams var1, AcknowledgePurchaseResponseListener var2) {
    Log.d("BillingClientImpl", "acknowledgePurchase: " + var1);
  }

  @Override
  public void consumeAsync(ConsumeParams var1, ConsumeResponseListener var2) {
    Log.d("BillingClientImpl", "consumeAsync: " + var1);
  }

  @Override
  public void createAlternativeBillingOnlyReportingDetailsAsync(AlternativeBillingOnlyReportingDetailsListener var1) {
    Log.d("BillingClientImpl", "createAlternativeBillingOnlyReportingDetailsAsync: " + var1);
  }

  @Override
  public void createExternalOfferReportingDetailsAsync(ExternalOfferReportingDetailsListener var1) {
    Log.d("BillingClientImpl", "createExternalOfferReportingDetailsAsync: " + var1);
  }

  @Override
  public void endConnection() {
    Log.d("BillingClientImpl", "endConnection called");
  }

  @Override
  public void getBillingConfigAsync(GetBillingConfigParams var1, BillingConfigResponseListener var2) {
    Log.d("BillingClientImpl", "getBillingConfigAsync: " + var1);
    var2.onBillingConfigResponse(BillingResult.newBuilder().setResponseCode(0).build(), null);
  }

  @Override
  public int getConnectionState() {
    Log.d("BillingClientImpl", "getConnectionState");
    return 2;
  }

  @Override
  public void isAlternativeBillingOnlyAvailableAsync(AlternativeBillingOnlyAvailabilityListener var1) {
    Log.d("BillingClientImpl", "isAlternativeBillingOnlyAvailableAsync: " + var1);
    var1.onAlternativeBillingOnlyAvailabilityResponse(BillingResult.newBuilder().setResponseCode(0).build());
  }

  @Override
  public void isExternalOfferAvailableAsync(ExternalOfferAvailabilityListener var1) {
    Log.d("BillingClientImpl", "isExternalOfferAvailableAsync: " + var1);
    var1.onExternalOfferAvailabilityResponse(BillingResult.newBuilder().setResponseCode(0).build());
  }

  @Override
  public BillingResult isFeatureSupported(String var1) {
    Log.d("BillingClientImpl", "isFeatureSupported: " + var1);
    return BillingResult.newBuilder().build();
  }

  @Override
  public boolean isReady() {
    Log.d("BillingClientImpl", "isReady");
    return true;
  }

  @Override
  public BillingResult launchBillingFlow(Activity var1, BillingFlowParams var2) {
    Log.d("BillingClientImpl", "launchBillingFlow: " + var1 + ", " + var2);
    return BillingResult.newBuilder().setResponseCode(0).build();
  }

  @Override
  public void queryProductDetailsAsync(QueryProductDetailsParams var1, ProductDetailsResponseListener var2) {
    Log.d("BillingClientImpl", "queryProductDetailsAsync: " + var1);
    var2.onProductDetailsResponse(BillingResult.newBuilder().setResponseCode(0).build(), Collections.emptyList());
  }

  @Override
  public void queryPurchaseHistoryAsync(QueryPurchaseHistoryParams var1, PurchaseHistoryResponseListener var2) {
    Log.d("BillingClientImpl", "queryPurchaseHistoryAsync: " + var1);
    var2.onPurchaseHistoryResponse(BillingResult.newBuilder().setResponseCode(0).build(), Collections.emptyList());
  }

  @Override
  public void queryPurchaseHistoryAsync(String var1, PurchaseHistoryResponseListener var2) {
    Log.d("BillingClientImpl", "queryPurchaseHistoryAsync: " + var1);
    var2.onPurchaseHistoryResponse(BillingResult.newBuilder().setResponseCode(0).build(), Collections.emptyList());
  }

  @Override
  public void startConnection(BillingClientStateListener var1) {
    Log.d("BillingClientImpl", "startConnection: " + var1);
    var1.onBillingSetupFinished(BillingResult.newBuilder().setResponseCode(0).build());
  }
}
