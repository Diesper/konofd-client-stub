package com.android.billingclient.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Purchase {
  private final String originalJson;
  private final String signature;

  Purchase() {
    this.originalJson = "";
    this.signature = "";
  }

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
    return "jp.assasans.konofd.stub";
  }

  public List<String> getProducts() {
    return Collections.singletonList("com.nexon.konosuba_quartz_0004");
  }

  public int getPurchaseState() {
    return PurchaseState.PURCHASED;
  }

  public long getPurchaseTime() {
    return 42424242L;
  }

  public String getPurchaseToken() {
    return "purchaseToken";
  }

  public int getQuantity() {
    return 1;
  }

  public String getSignature() {
    return this.signature;
  }

  @Deprecated
  public ArrayList<String> getSkus() {
    return new ArrayList<>(this.getProducts());
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

  @Retention(RetentionPolicy.SOURCE)
  public @interface PurchaseState {
    int PENDING = 2;
    int PURCHASED = 1;
    int UNSPECIFIED_STATE = 0;
  }
}
