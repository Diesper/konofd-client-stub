package com.google.android.ump;

import android.app.Activity;
import android.content.Context;

public class UserMessagingPlatform {
  public static ConsentInformation getConsentInformation(Context context) {
    class ConsentInformationImpl implements ConsentInformation {
      @Override
      public boolean canRequestAds() {
        return false;
      }

      @Override
      public int getConsentStatus() {
        return 0;
      }

      @Override
      public PrivacyOptionsRequirementStatus getPrivacyOptionsRequirementStatus() {
        return null;
      }

      @Override
      public boolean isConsentFormAvailable() {
        return false;
      }

      @Override
      public void requestConsentInfoUpdate(Activity var1, ConsentRequestParameters var2, OnConsentInfoUpdateSuccessListener var3, OnConsentInfoUpdateFailureListener var4) {
      }

      @Override
      public void reset() {
      }
    }

    return new ConsentInformationImpl();
  }
}
