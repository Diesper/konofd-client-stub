package com.adjust.sdk;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

public class Adjust {
  private static AdjustInstance defaultInstance;

  private Adjust() {
  }

  public static void addSessionCallbackParameter(String var0, String var1) {
    Log.d("Adjust", "addSessionCallbackParameter: " + var0 + ", " + var1);
  }

  public static void addSessionPartnerParameter(String var0, String var1) {
    Log.d("Adjust", "addSessionPartnerParameter: " + var0 + ", " + var1);
  }

  @Deprecated
  public static void appWillOpenUrl(Uri var0) {
    Log.d("Adjust", "appWillOpenUrl: " + var0);
  }

  public static void appWillOpenUrl(Uri var0, Context var1) {
    Log.d("Adjust", "appWillOpenUrl: " + var0 + ", " + var1);
  }

  public static void disableThirdPartySharing(Context var0) {
    Log.d("Adjust", "disableThirdPartySharing: " + var0);
  }

  public static void gdprForgetMe(Context var0) {
    Log.d("Adjust", "gdprForgetMe: " + var0);
  }

  public static String getAdid() {
    Log.d("Adjust", "getAdid");
    return "";
  }

  public static String getAmazonAdId(Context var0) {
    Log.d("Adjust", "getAmazonAdId");
    return "";
  }

  public static AdjustAttribution getAttribution() {
    Log.d("Adjust", "getAttribution");
    return new AdjustAttribution();
  }

  public static AdjustInstance getDefaultInstance() {
    AdjustInstance var0;
    try {
      if(defaultInstance == null) {
        var0 = new AdjustInstance();
        defaultInstance = var0;
      }

      var0 = defaultInstance;
    } finally {
    }

    return var0;
  }

  public static void getGoogleAdId(Context var0, OnDeviceIdsRead var1) {
    Log.d("Adjust", "getGoogleAdId");
  }

  public static String getSdkVersion() {
    Log.d("Adjust", "getSdkVersion");
    return "0.0.0";
  }

  public static boolean isEnabled() {
    Log.d("Adjust", "isEnabled");
    return false;
  }

  public static void onCreate(AdjustConfig var0) {
    Log.d("Adjust", "onCreate: " + var0);
  }

  public static void onPause() {
    Log.d("Adjust", "onPause");
  }

  public static void onResume() {
    Log.d("Adjust", "onResume");
  }

  public static void removeSessionCallbackParameter(String var0) {
    Log.d("Adjust", "removeSessionCallbackParameter: " + var0);
  }

  public static void removeSessionPartnerParameter(String var0) {
    Log.d("Adjust", "removeSessionPartnerParameter: " + var0);
  }

  public static void resetSessionCallbackParameters() {
    Log.d("Adjust", "resetSessionCallbackParameters");
  }

  public static void resetSessionPartnerParameters() {
    Log.d("Adjust", "resetSessionPartnerParameters");
  }

  public static void sendFirstPackages() {
    Log.d("Adjust", "sendFirstPackages");
  }

  public static void setEnabled(boolean var0) {
    Log.d("Adjust", "setEnabled: " + var0);
  }

  public static void setOfflineMode(boolean var0) {
    Log.d("Adjust", "setOfflineMode: " + var0);
  }

  public static void setPushToken(String var0) {
    Log.d("Adjust", "setPushToken: " + var0);
  }

  public static void setPushToken(String var0, Context var1) {
    Log.d("Adjust", "setPushToken: " + var0 + ", " + var1);
  }

  public static void setReferrer(String var0, Context var1) {
    Log.d("Adjust", "setReferrer: " + var0);
  }

  public static void setTestOptions(AdjustTestOptions var0) {
    Log.d("Adjust", "setTestOptions: " + var0);
  }

  public static void trackAdRevenue(AdjustAdRevenue var0) {
    Log.d("Adjust", "trackAdRevenue: " + var0);
  }

  public static void trackAdRevenue(String var0, JSONObject var1) {
    Log.d("Adjust", "trackAdRevenue: " + var0 + ", " + var1);
  }

  public static void trackEvent(AdjustEvent var0) {
    Log.d("Adjust", "trackEvent: " + var0);
  }

  public static void trackMeasurementConsent(boolean var0) {
    Log.d("Adjust", "trackMeasurementConsent: " + var0);
  }

  public static void trackPlayStoreSubscription(AdjustPlayStoreSubscription var0) {
    Log.d("Adjust", "trackPlayStoreSubscription: " + var0);
  }

  public static void trackThirdPartySharing(AdjustThirdPartySharing var0) {
    Log.d("Adjust", "trackThirdPartySharing: " + var0);
  }
}
