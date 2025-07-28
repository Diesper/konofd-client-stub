package com.safedk.android.utils;

import android.util.Log;

public class Logger {
  private static boolean debugMode = true;

  private Logger() {
    super();
  }

  public static int d(String var0) {
    byte var2 = 0;
    int var1 = var2;
    if(debugMode) {
      String[] var3 = var0.split("\\|");
      if(var3.length > 1) {
        var1 = Log.d(var3[0], var3[1]);
      } else {
        var1 = var2;
        if(var3.length == 1) {
          var1 = Log.d("UnknownClass", var3[0]);
        }
      }
    }

    return var1;
  }

  public static int d(String var0, String var1) {
    int var2;
    if(debugMode) {
      var2 = Log.d(var0, var1);
    } else {
      var2 = 0;
    }

    return var2;
  }

  public static int d(String var0, String var1, Throwable var2) {
    int var3;
    if(debugMode) {
      var3 = Log.d(var0, var1, var2);
    } else {
      var3 = 0;
    }

    return var3;
  }

  public static int e(String var0, String var1) {
    int var2;
    try {
      var2 = Log.e(var0, var1);
    } catch(Throwable var3) {
      var2 = 0;
    }

    return var2;
  }

  public static int e(String var0, String var1, Throwable var2) {
    int var3;
    try {
      var3 = Log.e(var0, var1, var2);
    } catch(Throwable var4) {
      var3 = 0;
    }

    return var3;
  }

  public static boolean getDebugMode() {
    return debugMode;
  }

  public static int i(String var0, String var1) {
    return Log.i(var0, var1);
  }

  public static int i(String var0, String var1, Throwable var2) {
    return Log.i(var0, var1, var2);
  }

  public static void printStackTrace() {
    StackTraceElement[] var2 = Thread.currentThread().getStackTrace();
    int var1 = var2.length;

    for(int var0 = 0; var0 < var1; ++var0) {
      Log.d("StackTrace", var2[var0].toString());
    }

  }

  public static void printStackTrace(String var0) {
    StackTraceElement[] var3 = Thread.currentThread().getStackTrace();
    int var2 = var3.length;

    for(int var1 = 0; var1 < var2; ++var1) {
      StackTraceElement var4 = var3[var1];
      Log.d(var0, "printStackTrace " + var4.toString());
    }

  }

  public static void setDebugMode(boolean var0) {
    // debugMode = var0;
  }

  public static int v(String var0, String var1) {
    int var2;
    if(debugMode) {
      var2 = Log.v(var0, var1);
    } else {
      var2 = 0;
    }

    return var2;
  }

  public static int v(String var0, String var1, Throwable var2) {
    int var3;
    if(debugMode) {
      var3 = Log.v(var0, var1, var2);
    } else {
      var3 = 0;
    }

    return var3;
  }

  public static int w(String var0, String var1) {
    return Log.w(var0, var1);
  }

  public static int w(String var0, String var1, Throwable var2) {
    return Log.w(var0, var1, var2);
  }

  public static int w(String var0, Throwable var1) {
    return Log.w(var0, var1);
  }
}
