package com.adjust.sdk;

import org.json.JSONObject;

public class AdjustEventFailure {
  public String adid;
  public String callbackId;
  public String eventToken;
  public JSONObject jsonResponse;
  public String message;
  public String timestamp;
  public boolean willRetry;

  public String toString() {
    return "AdjustEventFailure{" +
        "adid='" + adid + '\'' +
        ", callbackId='" + callbackId + '\'' +
        ", eventToken='" + eventToken + '\'' +
        ", jsonResponse=" + jsonResponse +
        ", message='" + message + '\'' +
        ", timestamp='" + timestamp + '\'' +
        ", willRetry=" + willRetry +
        '}';
  }
}
