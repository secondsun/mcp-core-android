package org.feedhenry.mcp.mobile_core;

import com.google.gson.JsonElement;

import java.util.Map;
import java.util.Optional;

/**
 * Created by summers on 9/26/17.
 */

public interface ServiceConfig {

    Map<String, JsonElement> getConfigFor(String serviceName);

}
