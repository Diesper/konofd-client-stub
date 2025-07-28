package com.google.firebase.app.internal.cpp;

import android.app.Activity;

public class CppThreadDispatcher {
  public CppThreadDispatcher() {
    super();
  }

  public static void runOnBackgroundThread(final CppThreadDispatcherContext cppThreadDispatcherContext) {
    new Thread((Runnable) new CppThreadDispatcher.CppThreadDispatcher$2(cppThreadDispatcherContext)).start();
  }

  public static void runOnMainThread(final Activity activity, final CppThreadDispatcherContext cppThreadDispatcherContext) {
    activity.runOnUiThread((Runnable) new CppThreadDispatcher.CppThreadDispatcher$1(cppThreadDispatcherContext));
  }

  static final class CppThreadDispatcher$1 implements Runnable {
    final CppThreadDispatcherContext val$context;

    CppThreadDispatcher$1(final CppThreadDispatcherContext val$context) {
      super();
      this.val$context = val$context;
    }

    @Override
    public void run() {
      this.val$context.execute();
    }
  }

  static final class CppThreadDispatcher$2 implements Runnable {
    final CppThreadDispatcherContext val$context;

    CppThreadDispatcher$2(final CppThreadDispatcherContext val$context) {
      super();
      this.val$context = val$context;
    }

    @Override
    public void run() {
      this.val$context.execute();
    }
  }
}
