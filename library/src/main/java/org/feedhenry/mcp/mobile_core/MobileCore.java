package org.feedhenry.mcp.mobile_core;

import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.mklimek.sslutilsandroid.SslUtils;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by summers on 9/26/17.
 */

public class MobileCore {

    private Retrofit retrofit;

    /*
    * This creates a mapping from ServiceConfig -> DefaultServiceConfig.  This is here so that tests
    * can inject their own gson.
    *
    * TODO: Make this better.  Ideas, extract gson to a utility class that tests can manipulate
    * TODO: Self signed certificate niceties (part of build when in debug?)
    *
    * */
    private Gson gson = new GsonBuilder().registerTypeAdapter(ServiceConfig.class, new JsonDeserializer<ServiceConfig>() {
        @Override
        public ServiceConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Map<String, JsonElement> configuration = new Gson().fromJson(json, new TypeToken<Map<String, JsonElement>>(){}.getType());
            return new DefaultServiceConfig(configuration);
        }
    }).create();

    //TODO Find a way to have this initialization somewhere else
    OkHttpClient client;
    public MobileCore(Context context) {
            SSLContext sslContext = SslUtils.getSslContextForCertificateFile(context, "localhost.cer");
            client = new OkHttpClient.Builder().hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            }).sslSocketFactory(sslContext.getSocketFactory()).build();
    }

    public Single<ServiceConfig> configure(final Context context) {
        Resources resources = context.getResources();
        String host = resources.getString(R.string.mcp_host);
        String appId = resources.getString(R.string.mcp_appId);
        String apiKey = resources.getString(R.string.mcp_apiKey);

        return configure(new ServerConfig(host, apiKey, appId));
    }


    public Single<ServiceConfig> configure(final ServerConfig configuration) {


        retrofit = new Retrofit.Builder()
                .baseUrl(configuration.host)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        //TODO turn into lambda, while coding IntelliJ gave an error.
        return Single.fromCallable(
                new Callable<ServiceConfig>() {
                    @Override
                    public ServiceConfig call() throws Exception {
                        return retrofit.create(ConfigService.class)
                                .getConfig(configuration.appID, configuration.apiKey)
                                .execute()
                                .body();
                    }
                }
        ).subscribeOn(Schedulers.newThread());


    }

}
