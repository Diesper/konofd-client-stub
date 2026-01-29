package com.android.billingclient.api;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("LongLogTag")
public final class ProductDetails {
  private String productId;
  private String productType;

  ProductDetails(String productId, String productType) {
    Log.d("ProductDetails", "our ctor: " + productId + ", " + productType);
    this.productId = productId;
    this.productType = productType;
  }

  public ProductDetails(String originalJson) {
    Log.d("ProductDetails", "ctor: " + originalJson);
  }

  public String getDescription() {
    Log.d("ProductDetails", "getDescription");
    return "description";
  }

  public String getName() {
    Log.d("ProductDetails", "getName");
    return "name";
  }

  public OneTimePurchaseOfferDetails getOneTimePurchaseOfferDetails() {
    Log.d("ProductDetails", "getOneTimePurchaseOfferDetails");
    return new OneTimePurchaseOfferDetails();
  }

  public String getProductId() {
    Log.d("ProductDetails", "getProductId -> " + productId);
    return productId;
  }

  public String getProductType() {
    Log.d("ProductDetails", "getProductType -> " + productType);
    return productType;
  }

  public List<SubscriptionOfferDetails> getSubscriptionOfferDetails() {
    Log.d("ProductDetails", "getSubscriptionOfferDetails");
    return null;
  }

  public String getTitle() {
    Log.d("ProductDetails", "getTitle");
    return "title";
  }

  public static final class SubscriptionOfferDetails {
    SubscriptionOfferDetails() {
    }

    public SubscriptionOfferDetails(JSONObject var1) {
      Log.d("SubscriptionOfferDetails", "ctor: " + var1);
    }

    public String getBasePlanId() {
      Log.d("SubscriptionOfferDetails", "getBasePlanId");
      return "base_plan_id";
    }

    public String getOfferId() {
      Log.d("SubscriptionOfferDetails", "getOfferId");
      return "offer_id";
    }

    public List getOfferTags() {
      Log.d("SubscriptionOfferDetails", "getOfferTags");
      return new ArrayList<>();
    }

    public String getOfferToken() {
      Log.d("SubscriptionOfferDetails", "getOfferToken");
      return "offer_token";
    }

    public PricingPhases getPricingPhases() {
      Log.d("SubscriptionOfferDetails", "getPricingPhases");
      return new PricingPhases();
    }
  }

  public static final class PricingPhases {
    PricingPhases() {
    }

    PricingPhases(JSONArray var1) {
      Log.d("PricingPhases", "ctor: " + var1);
    }

    public List<PricingPhase> getPricingPhaseList() {
      Log.d("PricingPhases", "getPricingPhaseList");
      return new ArrayList<>();
    }
  }

  public static final class PricingPhase {
    public PricingPhase(JSONObject ignored) {
      Log.d("PricingPhase", "ctor: " + ignored);
    }

    public int getBillingCycleCount() {
      Log.d("PricingPhase", "getBillingCycleCount");
      return 0;
    }

    public String getBillingPeriod() {
      Log.d("PricingPhase", "getBillingPeriod");
      return null;
    }

    public String getFormattedPrice() {
      Log.d("PricingPhase", "getFormattedPrice");
      return null;
    }

    public long getPriceAmountMicros() {
      Log.d("PricingPhase", "getPriceAmountMicros");
      return 0L;
    }

    public String getPriceCurrencyCode() {
      Log.d("PricingPhase", "getPriceCurrencyCode");
      return null;
    }

    public int getRecurrenceMode() {
      Log.d("PricingPhase", "getRecurrenceMode");
      return 0;
    }
  }

  public static final class OneTimePurchaseOfferDetails {
    OneTimePurchaseOfferDetails() {
    }

    public OneTimePurchaseOfferDetails(JSONObject ignored) {
      Log.d("OneTimePurchaseOfferDetails", "ctor: " + ignored);
    }

    public String getFormattedPrice() {
      Log.d("OneTimePurchaseOfferDetails", "getFormattedPrice");
      return "2112 JPY";
    }

    public long getPriceAmountMicros() {
      Log.d("OneTimePurchaseOfferDetails", "getPriceAmountMicros");
      return 100L;
    }

    public String getPriceCurrencyCode() {
      Log.d("OneTimePurchaseOfferDetails", "getPriceCurrencyCode");
      return "JPY";
    }
  }
}
