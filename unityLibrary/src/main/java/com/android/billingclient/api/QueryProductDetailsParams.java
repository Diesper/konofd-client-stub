package com.android.billingclient.api;

import android.util.Log;

import java.util.List;

public final class QueryProductDetailsParams {
  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private Builder() {
    }

    public QueryProductDetailsParams build() {
      return new QueryProductDetailsParams();
    }

    public Builder setProductList(List<Product> var1) {
      Log.d("BillingClient", "setProductList: " + var1);
      return this;
    }
  }

  public static class Product {
    private final String productId;
    private final String productType;

    private Product(Builder var1) {
      this.productId = var1.zza;
      this.productType = var1.zzb;
    }

    public static Builder newBuilder() {
      return new Builder();
    }

    public final String zza() {
      return this.productId;
    }

    public final String zzb() {
      return this.productType;
    }

    @Override
    public String toString() {
      return "Product{" +
        "productId='" + productId + '\'' +
        ", productType='" + productType + '\'' +
        '}';
    }

    public static class Builder {
      private String zza;
      private String zzb;

      private Builder() {
      }

      public QueryProductDetailsParams.Product build() {
        return new QueryProductDetailsParams.Product(this);
      }

      public Builder setProductId(String var1) {
        this.zza = var1;
        return this;
      }

      public Builder setProductType(String var1) {
        this.zzb = var1;
        return this;
      }
    }
  }
}
