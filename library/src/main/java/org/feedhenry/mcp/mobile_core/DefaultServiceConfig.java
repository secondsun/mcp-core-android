package org.feedhenry.mcp.mobile_core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

/**
 * Created by summers on 9/26/17.
 */

public class DefaultServiceConfig implements ServiceConfig {

    public final Map<String, JsonElement> config;

    DefaultServiceConfig(Map<String, JsonElement> config) {
        this.config = config;
    }

    @Override
    public Map<String, JsonElement> getConfigFor(String serviceName) {
        JsonElement serviceConfig = config.get(serviceName);
        if (serviceConfig == null) {
            return null;
        }

        //TODO this looks silly, find a better way to handle the nested JSON
        Map<String, JsonElement> map = new Gson().fromJson(serviceConfig, new TypeToken<Map<String, JsonElement>>() {
        }.getType());

        if (map.containsKey("config")) {
            return new Gson().fromJson(map.get("config").getAsJsonObject(), new TypeToken<Map<String, JsonElement>>() {
            }.getType());
        }
        return null;
    }
}
