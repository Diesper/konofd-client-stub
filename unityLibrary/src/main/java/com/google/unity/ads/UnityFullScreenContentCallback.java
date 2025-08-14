package com.google.unity.ads;

import com.google.android.gms.ads.AdError;

public interface UnityFullScreenContentCallback {
  void onAdClicked();

  void onAdDismissedFullScreenContent();

  void onAdFailedToShowFullScreenContent(AdError var1);

  void onAdImpression();

  void onAdShowedFullScreenContent();
}
