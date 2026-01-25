/*
 * Decompiled with CFR 0.152.
 */
package com.unity.androidnotifications;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class UnityNotificationManager
  extends BroadcastReceiver {
  public static final String KEY_BIG_CONTENT_DESCRIPTION = "com.unity.BigContentDescription";
  public static final String KEY_BIG_CONTENT_TITLE = "com.unity.BigContentTytle";
  public static final String KEY_BIG_LARGE_ICON = "com.unity.BigLargeIcon";
  public static final String KEY_BIG_PICTURE = "com.unity.BigPicture";
  public static final String KEY_BIG_SHOW_WHEN_COLLAPSED = "com.unity.BigShowWhenCollapsed";
  public static final String KEY_BIG_SUMMARY_TEXT = "com.unity.BigSummaryText";
  public static final String KEY_CHANNEL_ID = "channelID";
  public static final String KEY_FIRE_TIME = "fireTime";
  public static final String KEY_ID = "id";
  public static final String KEY_INTENT_DATA = "data";
  public static final String KEY_LARGE_ICON = "largeIcon";
  public static final String KEY_NOTIFICATION = "unityNotification";
  public static final String KEY_NOTIFICATION_DISMISSED = "com.unity.NotificationDismissed";
  public static final String KEY_NOTIFICATION_ID = "com.unity.NotificationID";
  public static final String KEY_REPEAT_INTERVAL = "repeatInterval";
  public static final String KEY_SHOW_IN_FOREGROUND = "com.unity.showInForeground";
  public static final String KEY_SMALL_ICON = "smallIcon";
  static final String NOTIFICATION_CHANNELS_SHARED_PREFS = "UNITY_NOTIFICATIONS";
  static final String NOTIFICATION_CHANNELS_SHARED_PREFS_KEY = "ChannelIDs";
  static final String NOTIFICATION_IDS_SHARED_PREFS = "UNITY_STORED_NOTIFICATION_IDS";
  static final String NOTIFICATION_IDS_SHARED_PREFS_KEY = "UNITY_NOTIFICATION_IDS";
  private static final int PERMISSION_STATUS_ALLOWED = 1;
  private static final int PERMISSION_STATUS_DENIED = 2;
  private static final int PERMISSION_STATUS_NOTIFICATIONS_BLOCKED_FOR_APP = 5;
  static final String TAG_UNITY = "UnityNotifications";

  static UnityNotificationManager mUnityNotificationManager;

  private Context mContext = null;

  private Notification buildNotificationForSending(Class clazz, Notification.Builder builder) {
    throw new UnsupportedOperationException("Stub");
  }

  private Intent buildNotificationIntent() {
    throw new UnsupportedOperationException("Stub");
  }

  private boolean canScheduleExactAlarms(AlarmManager alarmManager) {
    throw new UnsupportedOperationException("Stub");
  }

  private void finalizeNotificationForDisplay(Notification.Builder builder) {
    throw new UnsupportedOperationException("Stub");
  }

  private Set<String> findInvalidNotificationIds(Set<String> intent) {
    throw new UnsupportedOperationException("Stub");
  }

  private int generateUniqueId() {
    throw new UnsupportedOperationException("Stub");
  }

  private PendingIntent getActivityPendingIntent(int n, Intent intent, int n2) {
    throw new UnsupportedOperationException("Stub");
  }

  private Bundle getAppMetadata() {
    throw new UnsupportedOperationException("Stub");
  }

  private PendingIntent getBroadcastPendingIntent(int n, Intent intent, int n2) {
    throw new UnsupportedOperationException("Stub");
  }

  public static String getNotificationChannelId(Notification notification) {
    if(Build.VERSION.SDK_INT < 26) return null;
    return notification.getChannelId();
  }

  public static Integer getNotificationColor(Notification notification) {
    if(Build.VERSION.SDK_INT < 26) return notification.color;
    if(notification.extras.containsKey("android.colorized")) return notification.color;
    return null;
  }

  public static int getNotificationGroupAlertBehavior(Notification notification) {
    if(Build.VERSION.SDK_INT < 26) return 0;
    return notification.getGroupAlertBehavior();
  }

  public static UnityNotificationManager getNotificationManagerImpl(Activity object, NotificationCallback notificationCallback) {
    synchronized(UnityNotificationManager.class) {
      if(mUnityNotificationManager == null) {
        UnityNotificationManager unityNotificationManager;
        mUnityNotificationManager = unityNotificationManager = new UnityNotificationManager();
      }
      mUnityNotificationManager.initialize((Activity) object, notificationCallback);
      return mUnityNotificationManager;
    }
  }

  static UnityNotificationManager getNotificationManagerImpl(Context object) {
    synchronized(UnityNotificationManager.class) {
      if(mUnityNotificationManager == null) {
        UnityNotificationManager unityNotificationManager;
        mUnityNotificationManager = unityNotificationManager = new UnityNotificationManager();
      }
      return mUnityNotificationManager;
    }
  }

  private Object getNotificationOrBuilderForIntent(Intent var1_1) {
    throw new UnsupportedOperationException("Stub");
  }

  private Set<String> getScheduledNotificationIDs() {
    throw new UnsupportedOperationException("Stub");
  }

  private static String getSharedPrefsNameByChannelId(String string) {
    return String.format("unity_notification_channel_%s", string);
  }

  static String getSharedPrefsNameByNotificationId(String string) {
    return String.format("u_notification_data_%s", string);
  }

  private void initialize(Activity activity, NotificationCallback object) {
    this.mContext = activity.getApplicationContext();
    Log.d("UnityNotificationManager", "initialize: mContext=" + this.mContext);
  }

  private static boolean isInForeground() {
    ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
    ActivityManager.getMyMemoryState((ActivityManager.RunningAppProcessInfo) runningAppProcessInfo);
    boolean bl = runningAppProcessInfo.importance == 100 || runningAppProcessInfo.importance == 200;
    return bl;
  }

  private static NotificationChannelWrapper notificationChannelToWrapper(Object object) {
    throw new UnsupportedOperationException("Stub");
  }

  private void notify(int n, Notification notification) {
    throw new UnsupportedOperationException("Stub");
  }

  private void scheduleNotificationIntentAlarm(long l, long l2, PendingIntent pendingIntent) {
    throw new UnsupportedOperationException("Stub");
  }

  public static void setNotificationColor(Notification.Builder builder, int n) {
    if(n == 0) return;
    builder.setColor(n);
    if(Build.VERSION.SDK_INT < 26) return;
    builder.setColorized(true);
  }

  public static void setNotificationGroupAlertBehavior(Notification.Builder builder, int n) {
    if(Build.VERSION.SDK_INT < 26) return;
    builder.setGroupAlertBehavior(n);
  }

  public static void setNotificationIcon(Notification.Builder builder, String string, String string2) {
    if(string2 != null && (string2.length() != 0 || builder.getExtras().getString(string) == null)) {
      builder.getExtras().putString(string, string2);
    } else {
      builder.getExtras().remove(string);
    }
  }

  public static void setNotificationUsesChronometer(Notification.Builder builder, boolean bl) {
    builder.setUsesChronometer(bl);
  }

  public void cancelAllNotifications() {
    this.getNotificationManager().cancelAll();
  }

  public void cancelAllPendingNotificationIntents() {
  }

  public void cancelDisplayedNotification(int n) {
    this.getNotificationManager().cancel(n);
  }

  public void cancelPendingNotification(int n) {
  }

  void cancelPendingNotificationIntent(int n) {
  }

  public boolean checkIfPendingNotificationIsRegistered(int n) {
    throw new UnsupportedOperationException("Stub");
  }

  public int checkNotificationStatus(int n) {
    throw new UnsupportedOperationException("Stub");
  }

  public Notification.Builder createNotificationBuilder(String string) {
    throw new UnsupportedOperationException("Stub");
  }

  void deleteExpiredNotificationIntent(String string) {
    throw new UnsupportedOperationException("Stub");
  }

  public void deleteNotificationChannel(String string) {
    throw new UnsupportedOperationException("Stub");
  }

  public NotificationChannelWrapper getNotificationChannel(String object) {
    throw new UnsupportedOperationException("Stub");
  }

  public NotificationChannelWrapper[] getNotificationChannels() {
    throw new UnsupportedOperationException("Stub");
  }

  public Notification getNotificationFromIntent(Intent object) {
    throw new UnsupportedOperationException("Stub");
  }

  public NotificationManager getNotificationManager() {
    return (NotificationManager) this.mContext.getSystemService("notification");
  }

  public int getTargetSdk() {
    return 22;
  }

  List<Notification.Builder> loadSavedNotifications() {
    Log.d("UnityNotificationManager", "loadSavedNotifications: loading saved notifications");
    return Collections.emptyList();
  }

  public void onReceive(Context context, Intent intent) {
    UnityNotificationManager.getNotificationManagerImpl(context).onReceive(intent);
  }

  public void onReceive(Intent object) {
    Log.d("UnityNotificationManager", "onReceive: intent=" + object);
  }

  void performNotificationHousekeeping(Set<String> iterator) {
    Log.d("UnityNotificationManager", "performNotificationHousekeeping: set=" + iterator);
  }

  void performNotificationScheduling(int n, Notification.Builder builder, boolean bl) {
    Log.d("UnityNotificationManager", "performNotificationScheduling: id=" + n + ", builder=" + builder + ", isScheduled=" + bl);
  }

  public void registerNotificationChannel(String var1, String var2, int var3, String var4, boolean var5, boolean var6, boolean var7, boolean var8, long[] var9, int var10, String var11) {
    Log.d("UnityNotificationManager", "registerNotificationChannel: channelID=" + var1 + ", name=" + var2 + ", importance=" + var3 + ", description=" + var4 + ", showBadge=" + var5 + ", enableLights=" + var6 + ", enableVibration=" + var7 + ", bypassDnd=" + var8 + ", groupId=" + var11);
  }

  public void registerNotificationChannel(String string, String string2, int n, String string3, boolean bl, boolean bl2, boolean bl3, boolean bl4, long[] lArray, int n2) {
    Log.d("UnityNotificationManager", "registerNotificationChannel: channelID=" + string + ", name=" + string2 + ", importance=" + n + ", description=" + string3 + ", showBadge=" + bl + ", enableLights=" + bl2 + ", enableVibration=" + bl3 + ", bypassDnd=" + bl4);
  }

  void saveNotification(Notification notification, boolean bl) {
    Log.d("UnityNotificationManager", "saveNotification: notification=" + notification + ", isScheduled=" + bl);
  }

  void saveScheduledNotificationIDs(Set<String> set) {
    Log.d("UnityNotificationManager", "saveScheduledNotificationIDs: set=" + set);
  }

  void scheduleAlarmWithNotification(Notification.Builder builder) {
    Log.d("UnityNotificationManager", "scheduleAlarmWithNotification: builder=" + builder);
  }

  void scheduleAlarmWithNotification(Notification.Builder builder, Intent intent, long l) {
    Log.d("UnityNotificationManager", "scheduleAlarmWithNotification: builder=" + builder + ", intent=" + intent + ", fireTime=" + l);
  }

  public int scheduleNotification(Notification.Builder builder, boolean bl) {
    Log.d("UnityNotificationManager", "scheduleNotification: builder=" + builder + ", isScheduled=" + bl);
    return 0; // Placeholder for actual notification ID
  }

  public void showNotificationSettings(String string) {
    Log.d("UnityNotificationManager", "showNotificationSettings: channelID=" + string);
  }
}
