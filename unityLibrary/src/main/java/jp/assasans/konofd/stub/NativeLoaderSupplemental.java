package jp.assasans.konofd.stub;

public class NativeLoaderSupplemental {
  public static native boolean supplemental_verify();

  public static native boolean configure(
    String serverUrl,
    String publicKey,
    int method,
    boolean skipLogo
  );
}
