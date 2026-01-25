package com.adjust.sdk;

import android.util.Log;

public class AdjustEvent {
  public AdjustEvent(String eventToken) {
    Log.d("AdjustEvent", "AdjustEvent: eventToken=" + eventToken);
  }

  public void addCallbackParameter(String key, String value) {
    Log.d("AdjustEvent", "addCallbackParameter: key=" + key + ", value=" + value);
  }

  public void addPartnerParameter(String key, String value) {
    Log.d("AdjustEvent", "addPartnerParameter: key=" + key + ", value=" + value);
  }

  public boolean isValid() {
    Log.d("AdjustEvent", "isValid");
    return false;
  }

  public void setCallbackId(String callbackId) {
    Log.d("AdjustEvent", "setCallbackId: callbackId=" + callbackId);
  }

  public void setOrderId(String orderId) {
    Log.d("AdjustEvent", "setOrderId: orderId=" + orderId);
  }

  public void setRevenue(double revenue, String currency) {
    Log.d("AdjustEvent", "setRevenue: revenue=" + revenue + ", currency=" + currency);
  }
}
