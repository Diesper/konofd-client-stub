package com.google.android.ump;

import android.app.Activity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface ConsentInformation {
  public boolean canRequestAds();

  public int getConsentStatus();

  public PrivacyOptionsRequirementStatus getPrivacyOptionsRequirementStatus();

  public boolean isConsentFormAvailable();

  public void requestConsentInfoUpdate(Activity var1, ConsentRequestParameters var2, OnConsentInfoUpdateSuccessListener var3, OnConsentInfoUpdateFailureListener var4);

  public void reset();

  @Retention(value = RetentionPolicy.SOURCE)
  public static @interface ConsentStatus {
    public static final int NOT_REQUIRED = 1;
    public static final int OBTAINED = 3;
    public static final int REQUIRED = 2;
    public static final int UNKNOWN = 0;
  }

  public static interface OnConsentInfoUpdateFailureListener {
    public void onConsentInfoUpdateFailure(FormError var1);
  }

  public static interface OnConsentInfoUpdateSuccessListener {
    public void onConsentInfoUpdateSuccess();
  }

  public enum PrivacyOptionsRequirementStatus {
    NOT_REQUIRED,
    REQUIRED,
    UNKNOWN
  }
}
