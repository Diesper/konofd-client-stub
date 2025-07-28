package com.google.firebase.auth;

public class PlayGamesAuthProvider {
  public static final String PLAY_GAMES_SIGN_IN_METHOD = "playgames.google.com";
  public static final String PROVIDER_ID = "playgames.google.com";

  private PlayGamesAuthProvider() {
    super();
  }

  public static AuthCredential getCredential(final String s) {
    throw new UnsupportedOperationException("Stub");
  }
}
