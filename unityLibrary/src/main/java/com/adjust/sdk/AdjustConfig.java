package com.adjust.sdk;

import android.content.Context;
import android.util.Log;

public class AdjustConfig {
  public static final String AD_REVENUE_ADMOB = "admob_sdk";
  public static final String AD_REVENUE_APPLOVIN_MAX = "applovin_max_sdk";
  public static final String AD_REVENUE_IRONSOURCE = "ironsource_sdk";
  public static final String AD_REVENUE_MOPUB = "mopub";
  public static final String DATA_RESIDENCY_EU = "data_residency_eu";
  public static final String DATA_RESIDENCY_TR = "data_residency_tr";
  public static final String DATA_RESIDENCY_US = "data_residency_us";
  public static final String ENVIRONMENT_PRODUCTION = "production";
  public static final String ENVIRONMENT_SANDBOX = "sandbox";
  public static final String URL_STRATEGY_CHINA = "url_strategy_china";
  public static final String URL_STRATEGY_INDIA = "url_strategy_india";

  public AdjustConfig(Context context, String appToken, String environment) {
    Log.d("AdjustConfig", "AdjustConfig: context=" + context + ", appToken=" + appToken + ", environment=" + environment);
  }

  public AdjustConfig(Context context, String appToken, String environment, boolean allowSuppressLogLevel) {
    Log.d("AdjustConfig", "AdjustConfig: context=" + context + ", appToken=" + appToken + ", environment=" + environment + ", allowSuppressLogLevel=" + allowSuppressLogLevel);
  }

  public boolean isValid() {
    Log.d("AdjustConfig", "isValid");
    return true;
  }

  public void setAppSecret(long secretId, long info1, long info2, long info3, long info4) {
    Log.d("AdjustConfig", "setAppSecret: secretId=" + secretId + ", info1=" + info1 + ", info2=" + info2 + ", info3=" + info3 + ", info4=" + info4);
  }

  public void setDeepLinkComponent(Class deepLinkComponent) {
    Log.d("AdjustConfig", "setDeepLinkComponent: deepLinkComponent=" + deepLinkComponent);
  }

  public void setDefaultTracker(String defaultTracker) {
    Log.d("AdjustConfig", "setDefaultTracker: defaultTracker=" + defaultTracker);
  }

  public void setDelayStart(double delayStart) {
    Log.d("AdjustConfig", "setDelayStart: delayStart=" + delayStart);
  }

  public void setDeviceKnown(boolean deviceKnown) {
    Log.d("AdjustConfig", "setDeviceKnown: deviceKnown=" + deviceKnown);
  }

  public void setEventBufferingEnabled(Boolean eventBufferingEnabled) {
    Log.d("AdjustConfig", "setEventBufferingEnabled: eventBufferingEnabled=" + eventBufferingEnabled);
  }

  public void setExternalDeviceId(String externalDeviceId) {
    Log.d("AdjustConfig", "setExternalDeviceId:  externalDeviceId=" + externalDeviceId);
  }

  public void setLogLevel(LogLevel logLevel) {
    Log.d("AdjustConfig", "setLogLevel: logLevel=" + logLevel);
  }

  public void setNeedsCost(boolean needsCost) {
    Log.d("AdjustConfig", "setNeedsCost: needsCost=" + needsCost);
  }

  public void setOnAttributionChangedListener(OnAttributionChangedListener onAttributionChangedListener) {
    Log.d("AdjustConfig", "setOnAttributionChangedListener: onAttributionChangedListener=" + onAttributionChangedListener);
  }

  public void setOnDeeplinkResponseListener(OnDeeplinkResponseListener onDeeplinkResponseListener) {
    Log.d("AdjustConfig", "setOnDeeplinkResponseListener: onDeeplinkResponseListener=" + onDeeplinkResponseListener);
  }

  public void setOnEventTrackingFailedListener(OnEventTrackingFailedListener onEventTrackingFailedListener) {
    Log.d("AdjustConfig", "setOnEventTrackingFailedListener: onEventTrackingFailedListener=" + onEventTrackingFailedListener);
  }

  public void setOnEventTrackingSucceededListener(OnEventTrackingSucceededListener onEventTrackingSucceededListener) {
    Log.d("AdjustConfig", "setOnEventTrackingSucceededListener: onEventTrackingSucceededListener=" + onEventTrackingSucceededListener);
  }

  public void setOnSessionTrackingFailedListener(OnSessionTrackingFailedListener onSessionTrackingFailedListener) {
    Log.d("AdjustConfig", "setOnSessionTrackingFailedListener: onSessionTrackingFailedListener=" + onSessionTrackingFailedListener);
  }

  public void setOnSessionTrackingSucceededListener(OnSessionTrackingSucceededListener onSessionTrackingSucceededListener) {
    Log.d("AdjustConfig", "setOnSessionTrackingSucceededListener: onSessionTrackingSucceededListener=" + onSessionTrackingSucceededListener);
  }

  public void setPreinstallFilePath(String preinstallFilePath) {
    Log.d("AdjustConfig", "setPreinstallFilePath: preinstallFilePath=" + preinstallFilePath);
  }

  public void setPreinstallTrackingEnabled(boolean preinstallTrackingEnabled) {
    Log.d("AdjustConfig", "setPreinstallTrackingEnabled: preinstallTrackingEnabled=" + preinstallTrackingEnabled);
  }

  public void setProcessName(String processName) {
    Log.d("AdjustConfig", "setProcessName:  processName=" + processName);
  }

  @Deprecated
  public void setReadMobileEquipmentIdentity(boolean readMobileEquipmentIdentity) {
    Log.d("AdjustConfig", "setReadMobileEquipmentIdentity: readMobileEquipmentIdentity=" + readMobileEquipmentIdentity);
  }

  public void setSdkPrefix(String sdkPrefix) {
    Log.d("AdjustConfig", "setSdkPrefix: sdkPrefix=" + sdkPrefix);
  }

  public void setSendInBackground(boolean sendInBackground) {
    Log.d("AdjustConfig", "setSendInBackground:  sendInBackground=" + sendInBackground);
  }

  public void setUrlStrategy(String urlStrategy) {
    Log.d("AdjustConfig", "setUrlStrategy: urlStrategy=" + urlStrategy);
  }

  public void setUserAgent(String userAgent) {
    Log.d("AdjustConfig", "setUserAgent:  userAgent=" + userAgent);
  }
}
