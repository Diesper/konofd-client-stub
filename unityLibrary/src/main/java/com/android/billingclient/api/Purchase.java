package com.android.billingclient.api;

import java.util.ArrayList;
import java.util.List;

public class Purchase {
  private final String originalJson;
  private final String signature;

  public Purchase(String originalJson, String signature) {
    this.originalJson = originalJson;
    this.signature = signature;
  }

  public AccountIdentifiers getAccountIdentifiers() {
    return new AccountIdentifiers("", "");
  }

  public String getDeveloperPayload() {
    return "developerPayload";
  }

  public String getOrderId() {
    return "orderId";
  }

  public String getOriginalJson() {
    return this.originalJson;
  }

  public String getPackageName() {
    return "packageName";
  }

  public List<String> getProducts() {
    return new ArrayList<>();
  }

  public int getPurchaseState() {
    return 1;
  }

  public long getPurchaseTime() {
    return 42424242L;
  }

  public String getPurchaseToken() {
    return "purchaseToken";
  }

  public int getQuantity() {
    return 42;
  }

  public String getSignature() {
    return this.signature;
  }

  @Deprecated
  public ArrayList<String> getSkus() {
    return new ArrayList<>();
  }

  @Override
  public int hashCode() {
    return this.originalJson != null ? this.originalJson.hashCode() : 0;
  }

  public boolean isAcknowledged() {
    return true;
  }

  public boolean isAutoRenewing() {
    return false;
  }

  @Override
  public String toString() {
    return this.originalJson != null ? this.originalJson : "";
  }
}
