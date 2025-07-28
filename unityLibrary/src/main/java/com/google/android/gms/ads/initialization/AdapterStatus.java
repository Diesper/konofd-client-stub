/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.android.gms.ads.initialization.AdapterStatus$State
 */
package com.google.android.gms.ads.initialization;

public interface AdapterStatus {
  public String getDescription();

  public State getInitializationState();

  public int getLatency();

  public static enum State {
    NOT_READY,
    READY
  }
}
