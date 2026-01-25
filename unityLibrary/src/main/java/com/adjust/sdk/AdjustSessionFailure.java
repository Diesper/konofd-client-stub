package com.adjust.sdk;

import org.json.JSONObject;

public class AdjustSessionFailure {
  public String adid;
  public JSONObject jsonResponse;
  public String message;
  public String timestamp;
  public boolean willRetry;

  public String toString() {
    return "SessionFailure{" +
            "adid='" + adid + '\'' +
            ", jsonResponse=" + jsonResponse +
            ", message='" + message + '\'' +
            ", timestamp='" + timestamp + '\'' +
            ", willRetry=" + willRetry +
            '}';
  }
}
