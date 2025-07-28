package net.codestage.actk.androidnative;

import android.util.Log;

import java.util.concurrent.TimeUnit;

public class ACTkAndroidRoutines {
  public ACTkAndroidRoutines() {
    super();
  }

  public static long GetSystemCurrentTimeMs() {
    Log.d("ACTkAndroidRoutines", "GetSystemCurrentTimeMs");
    return System.currentTimeMillis();
  }

  public static long GetSystemNanoTime() {
    Log.d("ACTkAndroidRoutines", "GetSystemNanoTime");
    return System.nanoTime();
  }

  public static long GetSystemNanoTimeMs() {
    Log.d("ACTkAndroidRoutines", "GetSystemNanoTimeMs");
    return TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
  }
}
