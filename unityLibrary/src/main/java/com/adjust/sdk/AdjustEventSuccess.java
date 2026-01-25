package com.adjust.sdk;

import org.json.JSONObject;

public class AdjustEventSuccess {
  public String adid;
  public String callbackId;
  public String eventToken;
  public JSONObject jsonResponse;
  public String message;
  public String timestamp;

  public String toString() {
    return "AdjustEventSuccess{" +
        "adid='" + adid + '\'' +
        ", callbackId='" + callbackId + '\'' +
        ", eventToken='" + eventToken + '\'' +
        ", jsonResponse=" + jsonResponse +
        ", message='" + message + '\'' +
        ", timestamp='" + timestamp + '\'' +
        '}';
  }
}
