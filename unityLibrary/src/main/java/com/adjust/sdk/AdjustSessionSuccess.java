package com.adjust.sdk;

import org.json.JSONObject;

public class AdjustSessionSuccess {
  public String adid;
  public JSONObject jsonResponse;
  public String message;
  public String timestamp;

  public String toString() {
    return "AdjustSessionSuccess{" +
            "adid='" + adid + '\'' +
            ", jsonResponse=" + jsonResponse +
            ", message='" + message + '\'' +
            ", timestamp='" + timestamp + '\'' +
            '}';
  }
}
