package com.safedk.android.analytics.brandsafety.creatives;

import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

public class CreativeInfoManager {
  public static void onResourceLoaded(String var0, WebView var1, String var2) {
    Log.d("CreativeInfoManager", "onResourceLoaded: " + var0 + ", " + var1 + ", " + var2);
  }

  public static WebResourceResponse onWebViewResponse(String var0, WebView var1, String var2, WebResourceResponse var3) {
    Log.d("CreativeInfoManager", "onWebViewResponse: " + var0 + ", " + var1 + ", " + var2 + ", " + var3);
    return var3;
  }

  public static WebResourceResponse onWebViewResponseWithHeaders(String var0, WebView var1, WebResourceRequest var2, WebResourceResponse var3) {
    Log.d("CreativeInfoManager", "onWebViewResponseWithHeaders: " + var0 + ", " + var1 + ", " + var2 + ", " + var3);
    return var3;
  }
}
