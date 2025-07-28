package com.google.firebase.app.internal.cpp;

public class Log {
  public static void shutdown() {
  }

  private native void nativeLog(final int p0, final String p1, final String p2);
}
