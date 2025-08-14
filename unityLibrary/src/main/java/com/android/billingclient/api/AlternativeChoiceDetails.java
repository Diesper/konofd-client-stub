package com.android.billingclient.api;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Deprecated
public final class AlternativeChoiceDetails {
  private final String zza;
  private final JSONObject zzb;
  private final List zzc;

  AlternativeChoiceDetails(String var1) throws JSONException {
    super();
    this.zza = var1;
    JSONObject var5 = new JSONObject(var1);
    this.zzb = var5;
    JSONArray var4 = var5.optJSONArray("products");
    ArrayList var3 = new ArrayList();
    if(var4 != null) {
      for(int var2 = 0; var2 < var4.length(); ++var2) {
        var5 = var4.optJSONObject(var2);
        if(var5 != null) {
          var3.add(new Product(var5));
        }
      }
    }

    this.zzc = var3;
  }

  public String getExternalTransactionToken() {
    return this.zzb.optString("externalTransactionToken");
  }

  public String getOriginalExternalTransactionId() {
    String var2 = this.zzb.optString("originalExternalTransactionId");
    String var1 = var2;
    if(var2.isEmpty()) {
      var1 = null;
    }

    return var1;
  }

  public List getProducts() {
    return this.zzc;
  }

  public class Product {
    private final String zza;
    private final String zzb;
    private final String zzc;

    private Product(JSONObject var1) {
      this.zza = var1.optString("productId");
      this.zzb = var1.optString("productType");
      String var2 = var1.optString("offerToken");
      if(var2.isEmpty()) var2 = null;

      this.zzc = var2;
    }

    public boolean equals(Object var1) {
      if(this == var1) {
        return true;
      } else if(!(var1 instanceof Product)) {
        return false;
      } else {
        Product var2 = (Product) var1;
        return this.zza.equals(var2.getId()) && this.zzb.equals(var2.getType()) && Objects.equals(this.zzc, var2.getOfferToken());
      }
    }

    public String getId() {
      return this.zza;
    }

    public String getOfferToken() {
      return this.zzc;
    }

    public String getType() {
      return this.zzb;
    }

    public int hashCode() {
      return Objects.hash(this.zza, this.zzb, this.zzc);
    }

    public String toString() {
      return String.format("{id: %s, type: %s, offer token: %s}", this.zza, this.zzb, this.zzc);
    }
  }
}
