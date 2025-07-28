package net.aihelp.config.enums;


public enum PushPlatform {
  APNS(1),
  FIREBASE(2),
  GETUI(4),
  HUAWEI(6),
  JPUSH(3),
  ONE_SIGNAL(7);

  private int value;

  private PushPlatform(final int value) {
    this.value = value;
  }

  public static PushPlatform fromValue(final int n) {
    if(n == 1) {
      return PushPlatform.APNS;
    }
    if(n == 2) {
      return PushPlatform.FIREBASE;
    }
    if(n == 3) {
      return PushPlatform.JPUSH;
    }
    if(n == 4) {
      return PushPlatform.GETUI;
    }
    if(n == 6) {
      return PushPlatform.HUAWEI;
    }
    if(n != 7) {
      return null;
    }
    return PushPlatform.ONE_SIGNAL;
  }

  public int getValue() {
    return this.value;
  }
}
