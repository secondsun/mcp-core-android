package org.feedhenry.mcp.mobile_core;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by summers on 9/26/17.
 */

interface ConfigService {
//"/sdk/mobileapp/" + configuration.appID + "/config"
    @GET("/sdk/mobileapp/{appId}/config")
    Call<ServiceConfig> getConfig(@Path("appId") String appId, @Header("x-app-api-key") String apiKey);

}
