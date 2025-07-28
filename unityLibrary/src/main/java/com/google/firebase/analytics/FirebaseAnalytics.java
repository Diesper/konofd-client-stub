/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.os.Bundle
 *  com.google.android.gms.common.internal.Preconditions
 *  com.google.android.gms.internal.measurement.zzef
 *  com.google.android.gms.measurement.internal.zzhy
 *  com.google.android.gms.tasks.Task
 *  com.google.android.gms.tasks.Tasks
 *  com.google.firebase.analytics.FirebaseAnalytics$ConsentStatus
 *  com.google.firebase.analytics.FirebaseAnalytics$ConsentType
 *  com.google.firebase.analytics.zza
 *  com.google.firebase.analytics.zzb
 *  com.google.firebase.analytics.zzc
 *  com.google.firebase.analytics.zzd
 *  com.google.firebase.installations.FirebaseInstallations
 */
package com.google.firebase.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.tasks.Task;

import java.util.Map;

public final class FirebaseAnalytics {
  private static volatile FirebaseAnalytics zza;

  public FirebaseAnalytics() {
  }

  public static FirebaseAnalytics getInstance(Context context) {
    if(zza != null) return zza;
    synchronized(FirebaseAnalytics.class) {
      FirebaseAnalytics firebaseAnalytics;
      if(zza != null) return zza;
      zza = firebaseAnalytics = new FirebaseAnalytics();
    }
    return zza;
  }

  public Task<String> getAppInstanceId() {
    throw new UnsupportedOperationException("Stub");
  }

  public String getFirebaseInstanceId() {
    throw new UnsupportedOperationException("Stub");
  }

  public Task<Long> getSessionId() {
    throw new UnsupportedOperationException("Stub");
  }

  public void logEvent(String string, Bundle bundle) {
  }

  public void resetAnalyticsData() {
  }

  public void setAnalyticsCollectionEnabled(boolean bl) {
  }

  public void setConsent(Map<ConsentType, ConsentStatus> consentStatus) {
  }

  @Deprecated
  public void setCurrentScreen(Activity activity, String string, String string2) {
  }

  public void setDefaultEventParameters(Bundle bundle) {
  }

  public void setSessionTimeoutDuration(long l) {
  }

  public void setUserId(String string) {
  }

  public void setUserProperty(String string, String string2) {
  }

  public enum ConsentStatus {
    DENIED,
    GRANTED
  }

  public enum ConsentType {
    AD_STORAGE,
    ANALYTICS_STORAGE
  }

  public static class Event {
    public static final String ADD_PAYMENT_INFO = "add_payment_info";
    public static final String ADD_SHIPPING_INFO = "add_shipping_info";
    public static final String ADD_TO_CART = "add_to_cart";
    public static final String ADD_TO_WISHLIST = "add_to_wishlist";
    public static final String AD_IMPRESSION = "ad_impression";
    public static final String APP_OPEN = "app_open";
    public static final String BEGIN_CHECKOUT = "begin_checkout";
    public static final String CAMPAIGN_DETAILS = "campaign_details";
    public static final String EARN_VIRTUAL_CURRENCY = "earn_virtual_currency";
    public static final String GENERATE_LEAD = "generate_lead";
    public static final String JOIN_GROUP = "join_group";
    public static final String LEVEL_END = "level_end";
    public static final String LEVEL_START = "level_start";
    public static final String LEVEL_UP = "level_up";
    public static final String LOGIN = "login";
    public static final String POST_SCORE = "post_score";
    public static final String PURCHASE = "purchase";
    public static final String REFUND = "refund";
    public static final String REMOVE_FROM_CART = "remove_from_cart";
    public static final String SCREEN_VIEW = "screen_view";
    public static final String SEARCH = "search";
    public static final String SELECT_CONTENT = "select_content";
    public static final String SELECT_ITEM = "select_item";
    public static final String SELECT_PROMOTION = "select_promotion";
    public static final String SHARE = "share";
    public static final String SIGN_UP = "sign_up";
    public static final String SPEND_VIRTUAL_CURRENCY = "spend_virtual_currency";
    public static final String TUTORIAL_BEGIN = "tutorial_begin";
    public static final String TUTORIAL_COMPLETE = "tutorial_complete";
    public static final String UNLOCK_ACHIEVEMENT = "unlock_achievement";
    public static final String VIEW_CART = "view_cart";
    public static final String VIEW_ITEM = "view_item";
    public static final String VIEW_ITEM_LIST = "view_item_list";
    public static final String VIEW_PROMOTION = "view_promotion";
    public static final String VIEW_SEARCH_RESULTS = "view_search_results";

    protected Event() {
    }
  }


  public static class Param {
    public static final String ACHIEVEMENT_ID = "achievement_id";
    public static final String ACLID = "aclid";
    public static final String AD_FORMAT = "ad_format";
    public static final String AD_PLATFORM = "ad_platform";
    public static final String AD_SOURCE = "ad_source";
    public static final String AD_UNIT_NAME = "ad_unit_name";
    public static final String AFFILIATION = "affiliation";
    public static final String CAMPAIGN = "campaign";
    public static final String CHARACTER = "character";
    public static final String CONTENT = "content";
    public static final String CONTENT_TYPE = "content_type";
    public static final String COUPON = "coupon";
    public static final String CP1 = "cp1";
    public static final String CREATIVE_NAME = "creative_name";
    public static final String CREATIVE_SLOT = "creative_slot";
    public static final String CURRENCY = "currency";
    public static final String DESTINATION = "destination";
    public static final String DISCOUNT = "discount";
    public static final String END_DATE = "end_date";
    public static final String EXTEND_SESSION = "extend_session";
    public static final String FLIGHT_NUMBER = "flight_number";
    public static final String GROUP_ID = "group_id";
    public static final String INDEX = "index";
    public static final String ITEMS = "items";
    public static final String ITEM_BRAND = "item_brand";
    public static final String ITEM_CATEGORY = "item_category";
    public static final String ITEM_CATEGORY2 = "item_category2";
    public static final String ITEM_CATEGORY3 = "item_category3";
    public static final String ITEM_CATEGORY4 = "item_category4";
    public static final String ITEM_CATEGORY5 = "item_category5";
    public static final String ITEM_ID = "item_id";
    public static final String ITEM_LIST_ID = "item_list_id";
    public static final String ITEM_LIST_NAME = "item_list_name";
    public static final String ITEM_NAME = "item_name";
    public static final String ITEM_VARIANT = "item_variant";
    public static final String LEVEL = "level";
    public static final String LEVEL_NAME = "level_name";
    public static final String LOCATION = "location";
    public static final String LOCATION_ID = "location_id";
    public static final String MEDIUM = "medium";
    public static final String METHOD = "method";
    public static final String NUMBER_OF_NIGHTS = "number_of_nights";
    public static final String NUMBER_OF_PASSENGERS = "number_of_passengers";
    public static final String NUMBER_OF_ROOMS = "number_of_rooms";
    public static final String ORIGIN = "origin";
    public static final String PAYMENT_TYPE = "payment_type";
    public static final String PRICE = "price";
    public static final String PROMOTION_ID = "promotion_id";
    public static final String PROMOTION_NAME = "promotion_name";
    public static final String QUANTITY = "quantity";
    public static final String SCORE = "score";
    public static final String SCREEN_CLASS = "screen_class";
    public static final String SCREEN_NAME = "screen_name";
    public static final String SEARCH_TERM = "search_term";
    public static final String SHIPPING = "shipping";
    public static final String SHIPPING_TIER = "shipping_tier";
    public static final String SOURCE = "source";
    public static final String START_DATE = "start_date";
    public static final String SUCCESS = "success";
    public static final String TAX = "tax";
    public static final String TERM = "term";
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String TRAVEL_CLASS = "travel_class";
    public static final String VALUE = "value";
    public static final String VIRTUAL_CURRENCY_NAME = "virtual_currency_name";

    protected Param() {
    }

  }

  public static class UserProperty {
    public static final String ALLOW_AD_PERSONALIZATION_SIGNALS = "allow_personalized_ads";
    public static final String SIGN_UP_METHOD = "sign_up_method";

    protected UserProperty() {
    }
  }
}
