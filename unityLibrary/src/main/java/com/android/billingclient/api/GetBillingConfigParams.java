package com.android.billingclient.api;

public final class GetBillingConfigParams {
  private GetBillingConfigParams() {
    super();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private Builder() {
      super();
    }

    public GetBillingConfigParams build() {
      return new GetBillingConfigParams();
    }
  }
}
