package net.aihelp.config;

import net.aihelp.config.enums.PushPlatform;

public class UserConfig {
  private String formatCustomData;
  private boolean isSyncCrmInfo;
  private String serverId;
  private String userId;
  private String userName;
  private String userTags;

  private UserConfig(final String userId, final String userName, final String serverId, final String userTags, final String formatCustomData, final boolean isSyncCrmInfo) {
    super();
    this.userId = userId;
    this.userName = userName;
    this.serverId = serverId;
    this.userTags = userTags;
    this.formatCustomData = formatCustomData;
    this.isSyncCrmInfo = isSyncCrmInfo;
  }

  private UserConfig(final String userId, final String userName, final String serverId, final String userTags, final String formatCustomData, final boolean isSyncCrmInfo, final String s, final PushPlatform pushPlatform) {
    super();
    this.userId = userId;
    this.userName = userName;
    this.serverId = serverId;
    this.userTags = userTags;
    this.formatCustomData = formatCustomData;
    this.isSyncCrmInfo = isSyncCrmInfo;
  }

  UserConfig(final String s, final String s2, final String s3, final String s4, final String s5, final boolean b, final String s6, final PushPlatform pushPlatform, final UserConfig$1 userConfig) {
    this(s, s2, s3, s4, s5, b, s6, pushPlatform);
  }

  public static class Builder {
    private String customData;
    private boolean isSyncCrmInfo;
    private String serverId;
    private String userId;
    private String userName;
    private String userTags;

    public Builder() {
      super();
      this.userId = "";
      this.userName = "";
      this.serverId = "";
      this.userTags = "";
      this.customData = "";
    }

    private String getFormattedCustomData() {
      return "";
    }

    public UserConfig build() {
      return new UserConfig(this.userId, this.userName, this.serverId, this.userTags, this.getFormattedCustomData(), this.isSyncCrmInfo, "", null, null);
    }

    public UserConfig build(final String userId, final String userName, final String serverId, final String userTags, final String customData, final boolean syncCrmInfo) {
      return this.setUserId(userId).setUserName(userName).setServerId(serverId).setUserTags(userTags).setCustomData(customData).setSyncCrmInfo(syncCrmInfo).build();
    }

    public UserConfig build(final String userId, final String userName, final String serverId, final String userTags, final String customData, final boolean syncCrmInfo, final String s, final PushPlatform pushPlatform) {
      return this.setUserId(userId).setUserName(userName).setServerId(serverId).setUserTags(userTags).setCustomData(customData).setSyncCrmInfo(syncCrmInfo).build();
    }

    public Builder setCustomData(final String customData) {
      return this;
    }

    public Builder setServerId(final String serverId) {
      return this;
    }

    public Builder setSyncCrmInfo(final boolean isSyncCrmInfo) {
      return this;
    }

    public Builder setUserId(final String s) {
      return this;
    }

    public Builder setUserName(final String userName) {
      return this;
    }

    public Builder setUserTags(final String userTags) {
      return this;
    }
  }

  public static class UserConfig$1 {
  }
}
