package com.appsflyer.unity;

import android.util.Log;

import com.appsflyer.AppsFlyerConversionListener;

public class AppsFlyerAndroidWrapper {
  public static void addPushNotificationDeepLinkPath(String... stringArray) {
    Log.d("AppsFlyerUnity", "addPushNotificationDeepLinkPath: " + java.util.Arrays.toString(stringArray));
  }

  public static void attributeAndOpenStore(String string, String string2, java.util.Map<String, String> map) {
    Log.d("AppsFlyerUnity", "attributeAndOpenStore: " + string + ", " + string2 + ", " + map);
  }

  public static void createOneLinkInviteListener(java.util.Map<String, String> map, String string) {
    Log.d("AppsFlyerUnity", "createOneLinkInviteListener: " + map + ", " + string);
  }

  public static void enableFacebookDeferredApplinks(boolean bl) {
    Log.d("AppsFlyerUnity", "enableFacebookDeferredApplinks: " + bl);
  }

  public static String getAppsFlyerId() {
    Log.d("AppsFlyerUnity", "getAppsFlyerId");
    return "";
  }

  public static String getAttributionId() {
    Log.d("AppsFlyerUnity", "getAttributionId");
    return "";
  }

  public static void getConversionData(String string) {
    Log.d("AppsFlyerUnity", "getConversionData: " + string);
  }

  private static AppsFlyerConversionListener getConversionListener(String string) {
    Log.d("AppsFlyerUnity", "getConversionListener: " + string);
    return null;
  }

  public static String getHostName() {
    Log.d("AppsFlyerUnity", "getHostName");
    return "";
  }

  public static String getHostPrefix() {
    Log.d("AppsFlyerUnity", "getHostPrefix");
    return "";
  }

  public static String getOutOfStore() {
    Log.d("AppsFlyerUnity", "getOutOfStore");
    return "";
  }

  public static String getSdkVersion() {
    Log.d("AppsFlyerUnity", "getSdkVersion");
    return "";
  }

  public static void handlePushNotifications() {
    Log.d("AppsFlyerUnity", "handlePushNotifications");
  }

  public static void initInAppPurchaseValidatorListener(String string) {
    Log.d("AppsFlyerUnity", "initInAppPurchaseValidatorListener: " + string);
  }

  public static void initSDK(String string, String string2) {
    Log.d("AppsFlyerUnity", "initSDK: " + string + ", " + string2);
  }

  public static boolean isPreInstalledApp() {
    Log.d("AppsFlyerUnity", "isPreInstalledApp");
    return false;
  }

  public static boolean isTrackingStopped() {
    Log.d("AppsFlyerUnity", "isTrackingStopped");
    return false;
  }

  public static void recordCrossPromoteImpression(String string, String string2, java.util.Map<String, String> map) {
    Log.d("AppsFlyerUnity", "recordCrossPromoteImpression: " + string + ", " + string2 + ", " + map);
  }

  public static void setAdditionalData(java.util.HashMap<String, Object> hashMap) {
    Log.d("AppsFlyerUnity", "setAdditionalData: " + hashMap);
  }

  public static void setAndroidIdData(String string) {
    Log.d("AppsFlyerUnity", "setAndroidIdData: " + string);
  }

  public static void setAppInviteOneLinkID(String string) {
    Log.d("AppsFlyerUnity", "setAppInviteOneLinkID: " + string);
  }

  public static void setCollectAndroidID(boolean bl) {
    Log.d("AppsFlyerUnity", "setCollectAndroidID: " + bl);
  }

  public static void setCollectIMEI(boolean bl) {
    Log.d("AppsFlyerUnity", "setCollectIMEI: " + bl);
  }

  public static void setCollectOaid(boolean bl) {
    Log.d("AppsFlyerUnity", "setCollectOaid: " + bl);
  }

  public static void setConsumeAFDeepLinks(boolean bl) {
    Log.d("AppsFlyerUnity", "setConsumeAFDeepLinks: " + bl);
  }

  public static void setCurrencyCode(String string) {
    Log.d("AppsFlyerUnity", "setCurrencyCode: " + string);
  }

  public static void setCustomerIdAndTrack(String string) {
    Log.d("AppsFlyerUnity", "setCustomerIdAndTrack: " + string);
  }

  public static void setCustomerUserId(String string) {
    Log.d("AppsFlyerUnity", "setCustomerUserId: " + string);
  }

  public static void setDeviceTrackingDisabled(boolean bl) {
    Log.d("AppsFlyerUnity", "setDeviceTrackingDisabled: " + bl);
  }

  public static void setDisableAdvertisingIdentifiers(boolean bl) {
    Log.d("AppsFlyerUnity", "setDisableAdvertisingIdentifiers: " + bl);
  }

  public static void setDisableNetworkData(boolean bl) {
    Log.d("AppsFlyerUnity", "setDisableNetworkData: " + bl);
  }

  public static void setHost(String string, String string2) {
    Log.d("AppsFlyerUnity", "setHost: " + string + ", " + string2);
  }

  public static void setImeiData(String string) {
    Log.d("AppsFlyerUnity", "setImeiData: " + string);
  }

  public static void setIsDebug(boolean bl) {
    Log.d("AppsFlyerUnity", "setIsDebug: " + bl);
  }

  public static void setIsUpdate(boolean bl) {
    Log.d("AppsFlyerUnity", "setIsUpdate: " + bl);
  }

//    public static void setLogLevel(AFLogger.LogLevel logLevel) {
//        Log.d("AppsFlyerUnity", "setLogLevel: " + logLevel);
//    }

  public static void setMinTimeBetweenSessions(int n) {
    Log.d("AppsFlyerUnity", "setMinTimeBetweenSessions: " + n);
  }

  public static void setOneLinkCustomDomain(String... stringArray) {
    Log.d("AppsFlyerUnity", "setOneLinkCustomDomain: " + java.util.Arrays.toString(stringArray));
  }

  public static void setOutOfStore(String string) {
    Log.d("AppsFlyerUnity", "setOutOfStore: " + string);
  }

  public static void setPhoneNumber(String string) {
    Log.d("AppsFlyerUnity", "setPhoneNumber: " + string);
  }

  public static void setPreinstallAttribution(String string, String string2, String string3) {
    Log.d("AppsFlyerUnity", "setPreinstallAttribution: " + string + ", " + string2 + ", " + string3);
  }

  public static void setResolveDeepLinkURLs(String... stringArray) {
    Log.d("AppsFlyerUnity", "setResolveDeepLinkURLs: " + java.util.Arrays.toString(stringArray));
  }

  public static void setSharingFilter(String... stringArray) {
    Log.d("AppsFlyerUnity", "setSharingFilter: " + java.util.Arrays.toString(stringArray));
  }

  public static void setSharingFilterForAllPartners() {
    Log.d("AppsFlyerUnity", "setSharingFilterForAllPartners");
  }

  public static void setSharingFilterForPartners(String... stringArray) {
    Log.d("AppsFlyerUnity", "setSharingFilterForPartners: " + java.util.Arrays.toString(stringArray));
  }

//    public static void setUserEmails(AppsFlyerProperties.EmailsCryptType emailsCryptType, String... stringArray) {
//        Log.d("AppsFlyerUnity", "setUserEmails: " + emailsCryptType + ", " + java.util.Arrays.toString(stringArray));
//    }

  public static void setUserEmails(String... stringArray) {
    Log.d("AppsFlyerUnity", "setUserEmails: " + java.util.Arrays.toString(stringArray));
  }

  public static void startTracking() {
    Log.d("AppsFlyerUnity", "startTracking");
  }

  public static void startTracking(boolean bl, String string) {
    Log.d("AppsFlyerUnity", "startTracking: " + bl + ", " + string);
  }

  public static void stopTracking(boolean bl) {
    Log.d("AppsFlyerUnity", "stopTracking: " + bl);
  }

  public static void subscribeForDeepLink(String string) {
    Log.d("AppsFlyerUnity", "subscribeForDeepLink: " + string);
  }

  public static void trackEvent(String string, java.util.HashMap<String, Object> hashMap) {
    Log.d("AppsFlyerUnity", "trackEvent: " + string + ", " + hashMap);
  }

  public static void trackEvent(String string, java.util.HashMap<String, Object> hashMap, boolean bl, String string2) {
    Log.d("AppsFlyerUnity", "trackEvent (detailed): " + string + ", " + hashMap + ", " + bl + ", " + string2);
  }

  public static void trackLocation(double d, double d2) {
    Log.d("AppsFlyerUnity", "trackLocation: " + d + ", " + d2);
  }

  public static void updateServerUninstallToken(String string) {
    Log.d("AppsFlyerUnity", "updateServerUninstallToken: " + string);
  }

  public static void validateAndTrackInAppPurchase(String string, String string2, String string3, String string4, String string5, java.util.HashMap<String, String> hashMap, String string6) {
    Log.d("AppsFlyerUnity", "validateAndTrackInAppPurchase: " + string + ", " + string2 + ", " + string3 + ", " + string4 + ", " + string5 + ", " + hashMap + ", " + string6);
  }

  public static void waitForCustomerUserId(boolean bl) {
    Log.d("AppsFlyerUnity", "waitForCustomerUserId: " + bl);
  }
}
