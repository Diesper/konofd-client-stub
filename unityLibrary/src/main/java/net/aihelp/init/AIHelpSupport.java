package net.aihelp.init;

import android.content.Context;
import android.util.Log;

import net.aihelp.config.UserConfig;
import net.aihelp.ui.listener.OnAIHelpInitializedCallback;

public class AIHelpSupport {
  public static void enableLogging(final boolean b) {
    Log.d("AIHelpSupport", "enableLogging: " + b);
  }

  public static void init(final Context context, final String s, final String s2, final String s3, final String s4) {
    Log.d("AIHelpSupport", "init: context=" + context + ", s=" + s + ", s2=" + s2 + ", s3=" + s3 + ", s4=" + s4);
  }

  public static void setOnAIHelpInitializedCallback(final OnAIHelpInitializedCallback onAIHelpInitializedCallback) {
    Log.d("AIHelpSupport", "setOnAIHelpInitializedCallback: " + onAIHelpInitializedCallback);
    onAIHelpInitializedCallback.onAIHelpInitialized(true, "");
  }

  public static void updateUserInfo(final UserConfig userConfig) {
    Log.d("AIHelpSupport", "updateUserInfo: " + userConfig);
  }
}
