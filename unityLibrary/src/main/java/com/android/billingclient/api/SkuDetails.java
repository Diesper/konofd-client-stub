package com.android.billingclient.api;


import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

@Deprecated
public class SkuDetails {
  private final String zza;
  private final JSONObject zzb;

  public SkuDetails(String var1) throws JSONException {
    super();
    this.zza = var1;
    JSONObject var2 = new JSONObject(var1);
    this.zzb = var2;
    if(!TextUtils.isEmpty(var2.optString("productId"))) {
      if(TextUtils.isEmpty(var2.optString("type"))) {
        throw new IllegalArgumentException("SkuType cannot be empty.");
      }
    } else {
      throw new IllegalArgumentException("SKU cannot be empty.");
    }
  }

  public boolean equals(Object var1) {
    if(this == var1) {
      return true;
    } else if(!(var1 instanceof SkuDetails)) {
      return false;
    } else {
      SkuDetails var2 = (SkuDetails) var1;
      return TextUtils.equals(this.zza, var2.zza);
    }
  }

  public String getDescription() {
    return this.zzb.optString("description");
  }

  public String getFreeTrialPeriod() {
    return this.zzb.optString("freeTrialPeriod");
  }

  public String getIconUrl() {
    return this.zzb.optString("iconUrl");
  }

  public String getIntroductoryPrice() {
    return this.zzb.optString("introductoryPrice");
  }

  public long getIntroductoryPriceAmountMicros() {
    return this.zzb.optLong("introductoryPriceAmountMicros");
  }

  public int getIntroductoryPriceCycles() {
    return this.zzb.optInt("introductoryPriceCycles");
  }

  public String getIntroductoryPricePeriod() {
    return this.zzb.optString("introductoryPricePeriod");
  }

  public String getOriginalJson() {
    return this.zza;
  }

  public String getOriginalPrice() {
    return this.zzb.has("original_price") ? this.zzb.optString("original_price") : this.getPrice();
  }

  public long getOriginalPriceAmountMicros() {
    return this.zzb.has("original_price_micros") ? this.zzb.optLong("original_price_micros") : this.getPriceAmountMicros();
  }

  public String getPrice() {
    return this.zzb.optString("price");
  }

  public long getPriceAmountMicros() {
    return this.zzb.optLong("price_amount_micros");
  }

  public String getPriceCurrencyCode() {
    return this.zzb.optString("price_currency_code");
  }

  public String getSku() {
    return this.zzb.optString("productId");
  }

  public String getSubscriptionPeriod() {
    return this.zzb.optString("subscriptionPeriod");
  }

  public String getTitle() {
    return this.zzb.optString("title");
  }

  public String getType() {
    return this.zzb.optString("type");
  }

  public int hashCode() {
    return this.zza.hashCode();
  }

  public String toString() {
    return "SkuDetails: ".concat(String.valueOf(this.zza));
  }

  public int zza() {
    return this.zzb.optInt("offer_type");
  }

  public String zzb() {
    return this.zzb.optString("offer_id");
  }

  public String zzc() {
    String var2 = this.zzb.optString("offerIdToken");
    String var1 = var2;
    if(var2.isEmpty()) {
      var1 = this.zzb.optString("offer_id_token");
    }

    return var1;
  }

  public final String zzd() {
    return this.zzb.optString("packageName");
  }

  public String zze() {
    return this.zzb.optString("serializedDocid");
  }

  final String zzf() {
    return this.zzb.optString("skuDetailsToken");
  }
}
