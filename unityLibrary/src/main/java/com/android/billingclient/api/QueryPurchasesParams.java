package com.android.billingclient.api;

import android.util.Log;

public final class QueryPurchasesParams {
  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private Builder() {
      super();
    }

    public QueryPurchasesParams build() {
      return new QueryPurchasesParams();
    }

    public Builder setProductType(String var1) {
      Log.d("QueryPurchasesParams", "setProductType: " + var1);
      return this;
    }
  }
}
