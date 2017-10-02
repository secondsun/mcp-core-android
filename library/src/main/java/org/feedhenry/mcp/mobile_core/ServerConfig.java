package org.feedhenry.mcp.mobile_core;

/**
 * Created by summers on 9/26/17.
 */

public class ServerConfig {

    final String host;
    final String apiKey;
    final String appID;

    public ServerConfig(String host, String apiKey, String appId) {
        this.host = host;
        this.apiKey = apiKey;
        this.appID = appId;
    }


}
