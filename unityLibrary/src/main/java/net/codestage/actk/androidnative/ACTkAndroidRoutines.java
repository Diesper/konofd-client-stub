package net.codestage.actk.androidnative;

import java.util.concurrent.TimeUnit;

public class ACTkAndroidRoutines {
  public ACTkAndroidRoutines() {
    super();
  }

  public static long GetSystemCurrentTimeMs() {
    return System.currentTimeMillis();
  }

  public static long GetSystemNanoTime() {
    return System.nanoTime();
  }

  public static long GetSystemNanoTimeMs() {
    return TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
  }
}
