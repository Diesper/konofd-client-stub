package jp.assasans.konofd.stub;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class NativeLoaderBridge {
  public static void showToast(CharSequence message) {
    Context context = MyApp.getAppContext();
    Handler mainHandler = new Handler(Looper.getMainLooper());
    mainHandler.post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
  }
}
