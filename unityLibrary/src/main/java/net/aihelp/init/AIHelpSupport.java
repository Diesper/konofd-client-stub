package net.aihelp.init;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import net.aihelp.config.UserConfig;
import net.aihelp.ui.listener.OnAIHelpInitializedCallback;

import jp.assasans.konofd.stub.MyApp;

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

  // Invoked with [Menu] -> [Support].
  // We can repurpose this for whatever we want.
  public static boolean show(String entranceId) {
    // 1 = help center
    // 2 = faq
    // 3 = single faq
    // 4 = customer service
    // other = error
    Log.d("AIHelpSupport", "show: entranceId=" + entranceId);

    // show toast for testing
    Context context = MyApp.getAppContext();
    Handler mainHandler = new Handler(Looper.getMainLooper());
    mainHandler.post(() -> Toast.makeText(
      context,
      "AIHelpSupport.show() called with entranceId: " + entranceId,
      Toast.LENGTH_SHORT
    ).show());

    return true;
  }
}
