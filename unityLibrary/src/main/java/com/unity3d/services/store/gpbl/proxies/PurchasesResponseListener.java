package com.unity3d.services.store.gpbl.proxies;

import android.util.Log;

import com.unity3d.services.store.StoreEvent;
import com.unity3d.services.store.gpbl.BillingResultBridge;

import java.util.List;

public class PurchasesResponseListener {
  private final StoreEvent _errorEvent;
  private final Integer _operationId;
  private final StoreEvent _successEvent;

  public PurchasesResponseListener(StoreEvent var1, StoreEvent var2) {
    this(null, var1, var2);
  }

  public PurchasesResponseListener(Integer var1, StoreEvent var2, StoreEvent var3) {
    super();
    this._operationId = var1;
    this._successEvent = var2;
    this._errorEvent = var3;
  }

  public void onBillingResponse(BillingResultBridge var1, List var2) {
    Log.d("PurchasesResponseListener", "onBillingResponse: " + var1 + ", " + var2);
  }
}
