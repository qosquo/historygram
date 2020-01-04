package com.historygram.api;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonIOException;
import com.historygram.api.exceptions.ApiException;
import com.historygram.api.requests.ApiRequest;
import com.historygram.api.utils.ApiErrorUtils;

import org.jetbrains.annotations.Contract;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ApiExecutor {
    private ApiConfig config;

    protected int timeoutDelay = 500;
    protected Context context;

    private ApiOkHttpProvider okHttpProvider;
    private String host;
    private volatile String accessToken;

    public ApiExecutor(ApiConfig config) {
        this.config = config;
        host = config.getHttpApiHost();
        accessToken = config.getAccessToken();
    }

    public ApiOkHttpProvider getOkHttpProvider() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IllegalStateException("UI Thread");
        }

        return config.getOkHttpProvider();
    }

    public String getHost() {
        return host;
    }

    public <T> T execute(ApiMethodCall call, ApiResponseParser parser) throws IOException, InterruptedException, ApiException {
        String accessToken = config.getAccessToken() != null ? config.getAccessToken() : "";

        Request request = null;
        if (call.getMapping() == ApiRequest.Mapping.GET) {
            request = new Request.Builder()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .url(host + call.getMethod() + parseQueryString(call.getArgs()))
                    .build();
        } else if (call.getMapping() == ApiRequest.Mapping.POST) {
            RequestBody requestBody = RequestBody.create(call.getBody() != null ? call.getBody() : "",
                    MediaType.get("application/json"));
            request = new Request.Builder()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .post(requestBody)
                    .url(host + call.getMethod() + parseQueryString(call.getArgs()))
                    .build();
        } else if (call.getMapping() == ApiRequest.Mapping.DELETE) {
            request = new Request.Builder()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .delete()
                    .url(host + call.getMethod() + parseQueryString(call.getArgs()))
                    .build();
        }

        if (request == null) {
            throw new ApiException("Why tf request is null??");
        }

        ResponseBody body = executeRequest(request).body();
        if (body == null) {
            throw new ApiException("Response returned null instead of a valid string response");
        }

        String json = body.string();
        if (ApiErrorUtils.hasSimpleError(json)) {
            throw ApiErrorUtils.toSimpleError(json, call.getMethod());
        }

        try {
            JSONObject jsonObject = new JSONObject(json);
            json = jsonObject.get("response").toString();
        } catch (JSONException ignored) {
            //throw new ApiException("Couldn't get 'response' object in response json: \n" + json);
        }

        try {
            return (T) parser.parse(json);
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    @Deprecated
    public <T> T execute(ApiPostCall call, ApiResponseParser parser) throws IOException, InterruptedException, ApiException {
        RequestBody requestBody = RequestBody.create(call.getBody() != null ? call.getBody() : "", MediaType.get("application/json"));
        String accessToken = config.getAccessToken() != null ? config.getAccessToken() : "";

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .post(requestBody)
                .url(config.getHttpApiHost() + call.getMethod() + parseQueryString(call.getArgs()))
                .build();

        ResponseBody body = executeRequest(request).body();
        if (body == null) {
            throw new ApiException("Response returned null instead of a valid string response");
        }

        String json = body.string();
        if (ApiErrorUtils.hasSimpleError(json)) {
            throw ApiErrorUtils.toSimpleError(json, call.getMethod());
        }

        try {
            JSONObject jsonObject = new JSONObject(json);
            json = jsonObject.get("response").toString();
        } catch (JSONException ignored) {}

        try {
            return (T) parser.parse(json);
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    private Response executeRequest(Request request) throws InterruptedException, IOException {
        OkHttpClient client = getOkHttpProvider().getClient();
        return client.newCall(request).execute();
    }

    private String parseQueryString(Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("?");
        for (String key : params.keySet()) {
            stringBuilder.append(key).append("=").append(params.get(key)).append("&");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        String query = stringBuilder.toString();

        System.out.println(query);

        return query;
    }
}
