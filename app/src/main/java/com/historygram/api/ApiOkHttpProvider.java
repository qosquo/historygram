package com.historygram.api;

import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;

public abstract class ApiOkHttpProvider {

    public abstract OkHttpClient getClient();

    public static class DefaultProvider extends ApiOkHttpProvider {
        private volatile OkHttpClient okHttpClient;

        @Override
        public OkHttpClient getClient() {
            if (okHttpClient == null) {
                okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .build();
            }
            return okHttpClient;
        }
    }

}
