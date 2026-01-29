package com.android.billingclient.api;

import android.app.Activity;
import android.content.Context;

public abstract class BillingClient {
  public BillingClient() {
    super();
  }

  public static Builder newBuilder(Context var0) {
    return new Builder(var0);
  }

  public abstract void acknowledgePurchase(AcknowledgePurchaseParams var1, AcknowledgePurchaseResponseListener var2);

  public abstract void consumeAsync(ConsumeParams var1, ConsumeResponseListener var2);

  public abstract void createAlternativeBillingOnlyReportingDetailsAsync(AlternativeBillingOnlyReportingDetailsListener var1);

  public abstract void createExternalOfferReportingDetailsAsync(ExternalOfferReportingDetailsListener var1);

  public abstract void endConnection();

  public abstract void getBillingConfigAsync(GetBillingConfigParams var1, BillingConfigResponseListener var2);

  public abstract int getConnectionState();

  public abstract void isAlternativeBillingOnlyAvailableAsync(AlternativeBillingOnlyAvailabilityListener var1);

  public abstract void isExternalOfferAvailableAsync(ExternalOfferAvailabilityListener var1);

  public abstract BillingResult isFeatureSupported(String var1);

  public abstract boolean isReady();

  public abstract BillingResult launchBillingFlow(Activity var1, BillingFlowParams var2);

  public abstract void queryProductDetailsAsync(QueryProductDetailsParams var1, ProductDetailsResponseListener var2);

  public abstract void queryPurchaseHistoryAsync(QueryPurchaseHistoryParams var1, PurchaseHistoryResponseListener var2);

  @Deprecated
  public abstract void queryPurchaseHistoryAsync(String var1, PurchaseHistoryResponseListener var2);

  // public abstract void queryPurchasesAsync(QueryPurchasesParams var1, PurchasesResponseListener var2);

  @Deprecated
  public abstract void queryPurchasesAsync(String var1, PurchasesResponseListener var2);

  // @Deprecated
  // public abstract void querySkuDetailsAsync(SkuDetailsParams var1, SkuDetailsResponseListener var2);
  //
  // public abstract BillingResult showAlternativeBillingOnlyInformationDialog(Activity var1, AlternativeBillingOnlyInformationDialogListener var2);
  //
  // public abstract BillingResult showExternalOfferInformationDialog(Activity var1, ExternalOfferInformationDialogListener var2);
  //
  // public abstract BillingResult showInAppMessages(Activity var1, InAppMessageParams var2, InAppMessageResponseListener var3);

  public abstract void startConnection(BillingClientStateListener var1);

  public static class Builder {
    private volatile String zza;
    private final Context zzc;
    volatile PurchasesUpdatedListener purchasesUpdatedListener;
    private volatile AlternativeBillingListener zzg;
    private volatile UserChoiceBillingListener zzh;
    private volatile boolean zzj;
    private volatile boolean zzk;

    private Builder(Context var1) {
      this.zzc = var1;
    }

    public BillingClient build() {
      return new BillingClientImpl(this);
    }

    @Deprecated
    public Builder enableAlternativeBilling(AlternativeBillingListener var1) {
      this.zzg = var1;
      return this;
    }

    public Builder enableAlternativeBillingOnly() {
      this.zzj = true;
      return this;
    }

    public Builder enableExternalOffer() {
      this.zzk = true;
      return this;
    }

    public Builder enablePendingPurchases() {
      return this;
    }

    public Builder enableUserChoiceBilling(UserChoiceBillingListener var1) {
      this.zzh = var1;
      return this;
    }

    public Builder setListener(PurchasesUpdatedListener var1) {
      this.purchasesUpdatedListener = var1;
      return this;
    }
  }
}
