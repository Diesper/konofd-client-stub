package com.google.firebase;


import android.content.Context;

public final class FirebaseOptions {
  private static final String API_KEY_RESOURCE_NAME = "google_api_key";
  private static final String APP_ID_RESOURCE_NAME = "google_app_id";
  private static final String DATABASE_URL_RESOURCE_NAME = "firebase_database_url";
  private static final String GA_TRACKING_ID_RESOURCE_NAME = "ga_trackingId";
  private static final String GCM_SENDER_ID_RESOURCE_NAME = "gcm_defaultSenderId";
  private static final String PROJECT_ID_RESOURCE_NAME = "project_id";
  private static final String STORAGE_BUCKET_RESOURCE_NAME = "google_storage_bucket";
  public final String apiKey;
  public final String applicationId;
  public final String databaseUrl;
  public final String gaTrackingId;
  public final String gcmSenderId;
  public final String projectId;
  public final String storageBucket;

  public FirebaseOptions(final String applicationId, final String apiKey, final String databaseUrl, final String gaTrackingId, final String gcmSenderId, final String storageBucket, final String projectId) {
    super();
    this.applicationId = applicationId;
    this.apiKey = apiKey;
    this.databaseUrl = databaseUrl;
    this.gaTrackingId = gaTrackingId;
    this.gcmSenderId = gcmSenderId;
    this.storageBucket = storageBucket;
    this.projectId = projectId;
  }

  public static FirebaseOptions fromResource(final Context context) {
    return new FirebaseOptions("", "", "", "", "", "", "");
  }

  public String getApiKey() {
    return this.apiKey;
  }

  public String getApplicationId() {
    return this.applicationId;
  }

  public String getDatabaseUrl() {
    return this.databaseUrl;
  }

  public String getGaTrackingId() {
    return this.gaTrackingId;
  }

  public String getGcmSenderId() {
    return this.gcmSenderId;
  }

  public String getProjectId() {
    return this.projectId;
  }

  public String getStorageBucket() {
    return this.storageBucket;
  }

  public static final class Builder {
    private String apiKey;
    private String applicationId;
    private String databaseUrl;
    private String gaTrackingId;
    private String gcmSenderId;
    private String projectId;
    private String storageBucket;

    public Builder() {
      super();
    }

    public Builder(final FirebaseOptions firebaseOptions) {
      super();
      this.applicationId = firebaseOptions.applicationId;
      this.apiKey = firebaseOptions.apiKey;
      this.databaseUrl = firebaseOptions.databaseUrl;
      this.gaTrackingId = firebaseOptions.gaTrackingId;
      this.gcmSenderId = firebaseOptions.gcmSenderId;
      this.storageBucket = firebaseOptions.storageBucket;
      this.projectId = firebaseOptions.projectId;
    }

    public FirebaseOptions build() {
      return new FirebaseOptions(this.applicationId, this.apiKey, this.databaseUrl, this.gaTrackingId, this.gcmSenderId, this.storageBucket, this.projectId);
    }

    public Builder setApiKey(final String s) {
      return this;
    }

    public Builder setApplicationId(final String s) {
      return this;
    }

    public Builder setDatabaseUrl(final String databaseUrl) {
      this.databaseUrl = databaseUrl;
      return this;
    }

    public Builder setGaTrackingId(final String gaTrackingId) {
      this.gaTrackingId = gaTrackingId;
      return this;
    }

    public Builder setGcmSenderId(final String gcmSenderId) {
      this.gcmSenderId = gcmSenderId;
      return this;
    }

    public Builder setProjectId(final String projectId) {
      this.projectId = projectId;
      return this;
    }

    public Builder setStorageBucket(final String storageBucket) {
      this.storageBucket = storageBucket;
      return this;
    }
  }
}
