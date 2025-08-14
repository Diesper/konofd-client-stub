package com.android.billingclient.api;

import android.util.Log;

import java.util.List;

public class BillingFlowParams {
  public static final String EXTRA_PARAM_KEY_ACCOUNT_ID = "accountId";

  private BillingFlowParams() {
    super();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    public BillingFlowParams build() {
      return new BillingFlowParams();
    }

    public Builder setIsOfferPersonalized(boolean var1) {
      return this;
    }

    public Builder setObfuscatedAccountId(String var1) {
      return this;
    }

    public Builder setObfuscatedProfileId(String var1) {
      return this;
    }

    public Builder setProductDetailsParamsList(List var1) {
      return this;
    }

    @Deprecated
    public Builder setSkuDetails(SkuDetails var1) {
      return this;
    }

    public Builder setSubscriptionUpdateParams(BillingFlowParams.SubscriptionUpdateParams var1) {
      return this;
    }
  }

  public static class SubscriptionUpdateParams {
    private SubscriptionUpdateParams() {
      super();
    }

    public static BillingFlowParams.SubscriptionUpdateParams.Builder newBuilder() {
      return new BillingFlowParams.SubscriptionUpdateParams.Builder();
    }

    public static class Builder {
      public BillingFlowParams.SubscriptionUpdateParams build() {
        return new BillingFlowParams.SubscriptionUpdateParams();
      }

      public Builder setOldPurchaseToken(String var1) {
        Log.d("SubscriptionUpdateParams", "setOldPurchaseToken: " + var1);
        return this;
      }

      @Deprecated
      public Builder setOldSkuPurchaseToken(String var1) {
        Log.d("SubscriptionUpdateParams", "setOldSkuPurchaseToken: " + var1);
        return this;
      }

      public Builder setOriginalExternalTransactionId(String var1) {
        Log.d("SubscriptionUpdateParams", "setOriginalExternalTransactionId: " + var1);
        return this;
      }

      @Deprecated
      public Builder setReplaceProrationMode(int var1) {
        Log.d("SubscriptionUpdateParams", "setReplaceProrationMode: " + var1);
        return this;
      }

      @Deprecated
      public Builder setReplaceSkusProrationMode(int var1) {
        Log.d("SubscriptionUpdateParams", "setReplaceSkusProrationMode: " + var1);
        return this;
      }

      public Builder setSubscriptionReplacementMode(int var1) {
        Log.d("SubscriptionUpdateParams", "setSubscriptionReplacementMode: " + var1);
        return this;
      }
    }
  }
}
