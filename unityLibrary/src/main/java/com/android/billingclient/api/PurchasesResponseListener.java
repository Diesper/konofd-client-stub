package com.android.billingclient.api;

import java.util.List;

public interface PurchasesResponseListener {
  void onQueryPurchasesResponse(BillingResult var1, List<Purchase> var2);
}
