package com.google.android.gms.ads;

import android.content.Context;
import android.webkit.WebView;

import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MobileAds {
  public static final String ERROR_DOMAIN = "com.google.android.gms.ads";

  private MobileAds() {
  }

  public static void disableMediationAdapterInitialization(Context context) {
  }

  public static void enableSameAppKey(boolean bl) {
  }

  public static InitializationStatus getInitializationStatus() {
    throw new UnsupportedOperationException("Stub");
  }

  private static String getInternalVersion() {
    throw new UnsupportedOperationException("Stub");
  }

  public static RequestConfiguration getRequestConfiguration() {
    return new RequestConfiguration.Builder().build();
  }

  public static VersionInfo getVersion() {
    return new VersionInfo(0, 0, 0);
  }

  public static void initialize(Context context) {
  }

  public static void initialize(Context context, OnInitializationCompleteListener onInitializationCompleteListener) {
  }

  public static void openAdInspector(Context context, OnAdInspectorClosedListener onAdInspectorClosedListener) {
  }

  public static void openDebugMenu(Context context, String string) {
  }

  public static void registerRtbAdapter(Class<?> clazz) {
  }

  public static void registerWebView(WebView webView) {
  }

  public static void setAppMuted(boolean bl) {
  }

  public static void setAppVolume(float f) {
  }

  private static void setPlugin(String string) {
  }

  public static void setRequestConfiguration(RequestConfiguration requestConfiguration) {
  }
}
