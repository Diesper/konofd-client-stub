package com.android.billingclient.api;

import android.util.Log;

public final class QueryPurchaseHistoryParams {
  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private Builder() {
      super();
    }

    public QueryPurchaseHistoryParams build() {
      return new QueryPurchaseHistoryParams();
    }

    public Builder setProductType(String var1) {
      Log.d("QueryPurchaseHistoryParams", "setProductType: " + var1);
      return this;
    }
  }
}
