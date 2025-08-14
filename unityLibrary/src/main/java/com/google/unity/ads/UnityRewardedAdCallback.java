package com.google.unity.ads;

import com.google.android.gms.ads.LoadAdError;

public interface UnityRewardedAdCallback extends UnityPaidEventListener, UnityFullScreenContentCallback {
  void onRewardedAdFailedToLoad(LoadAdError var1);

  void onRewardedAdLoaded();

  void onUserEarnedReward(String var1, float var2);
}
