package com.google.unity.ads;

import android.app.Activity;

public class UnityRewardedAd {
  private final Activity activity;
  private final UnityRewardedAdCallback callback;

  public UnityRewardedAd(Activity activity, UnityRewardedAdCallback unityRewardedAdCallback) {
    this.activity = activity;
    this.callback = unityRewardedAdCallback;
  }
}
