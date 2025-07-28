package com.google.firebase.platforminfo;

import com.google.android.gms.common.internal.LibraryVersion;

import java.util.HashSet;
import java.util.Set;

public class GlobalLibraryVersionRegistrar {
  public static GlobalLibraryVersionRegistrar getInstance() {
    return new GlobalLibraryVersionRegistrar();
  }

  Set<LibraryVersion> getRegisteredVersions() {
    return new HashSet<LibraryVersion>();
  }

  public void registerVersion(final String s, final String s2) {
  }
}
