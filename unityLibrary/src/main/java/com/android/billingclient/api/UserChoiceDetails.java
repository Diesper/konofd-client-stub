package com.android.billingclient.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserChoiceDetails {
  private final String mOriginalJson;
  private final JSONObject mParsedJson;
  private final List<Product> mProducts;

  UserChoiceDetails(String var1) throws JSONException {
    super();
    this.mOriginalJson = var1;
    JSONObject var2 = new JSONObject(var1);
    this.mParsedJson = var2;
    this.mProducts = toProductList(var2.optJSONArray("products"));
  }

  private static List<Product> toProductList(JSONArray var0) {
    ArrayList<Product> var3 = new ArrayList<>();
    if(var0 != null) {
      for(int var1 = 0; var1 < var0.length(); ++var1) {
        JSONObject var2 = var0.optJSONObject(var1);
        if(var2 != null) {
          var3.add(new Product(var2));
        }
      }
    }

    return var3;
  }

  public String getExternalTransactionToken() {
    return this.mParsedJson.optString("externalTransactionToken");
  }

  public String getOriginalExternalTransactionId() {
    String var2 = this.mParsedJson.optString("originalExternalTransactionId");
    String var1 = var2;
    if(var2.isEmpty()) {
      var1 = null;
    }

    return var1;
  }

  public List<Product> getProducts() {
    return this.mProducts;
  }

  public static class Product {
    private final String id;
    private final String offerToken;
    private final String type;

    Product(String var1, String var2, String var3) {
      this.id = var1;
      this.type = var2;
      this.offerToken = var3;
    }

    private Product(JSONObject var1) {
      this.id = var1.optString("productId");
      this.type = var1.optString("productType");
      String var2 = var1.optString("offerToken");
      if(var2.isEmpty()) var2 = null;

      this.offerToken = var2;
    }

    public boolean equals(Object var1) {
      if(this == var1) {
        return true;
      } else if(!(var1 instanceof Product)) {
        return false;
      } else {
        Product var2 = (Product) var1;
        return this.id.equals(var2.getId()) && this.type.equals(var2.getType()) && Objects.equals(this.offerToken, var2.getOfferToken());
      }
    }

    public String getId() {
      return this.id;
    }

    public String getOfferToken() {
      return this.offerToken;
    }

    public String getType() {
      return this.type;
    }

    public int hashCode() {
      return Objects.hash(this.id, this.type, this.offerToken);
    }

    public String toString() {
      return String.format("{id: %s, type: %s, offer token: %s}", this.id, this.type, this.offerToken);
    }
  }
}
