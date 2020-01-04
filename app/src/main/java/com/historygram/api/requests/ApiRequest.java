package com.historygram.api.requests;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.historygram.api.ApiCommand;
import com.historygram.api.ApiConfig;
import com.historygram.api.ApiResponseParser;
import com.historygram.api.ApiExecutor;
import com.historygram.api.ApiMethodCall;
import com.historygram.api.exceptions.ApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import kotlin.Suppress;

public class ApiRequest<T> extends ApiCommand<T> implements ApiResponseParser<T> {
    private String method;
    private Class<T> clazz;
    private LinkedHashMap<String, String> params = new LinkedHashMap<>();
    private String body;
    private Mapping mapping = Mapping.GET;

    public ApiRequest(String method, Class<T> clazz) {
        this.method = method;
        this.clazz = clazz;
    }

    public void addParam(@NonNull String name, @NonNull String value) {
        params.put(name, value);
    }
    public void addParam(@NonNull String name, int value) {
        params.put(name, Integer.toString(value));
    }
    public void addParam(@NonNull String name, long value) {
        params.put(name, Long.toString(value));
    }
    public void addParam(@NonNull CharSequence name, @NonNull ArrayList<?> values) {
        params.put(name.toString(), TextUtils.join(",", values));
    }
    public void addParam(@NonNull CharSequence name, @NonNull Iterable<?> values) {
        params.put(name.toString(), TextUtils.join(",", values));
    }
    public void addBody(@NonNull String body) {
        this.body = body;
    }

    public void setMapping(@NonNull Mapping mapping) {
        this.mapping = mapping;
    }

    @Suppress(names = {"UNCHECKED_CAST"})
    @Override
    public T parse(String response) throws ApiException {
        try {
            return new Gson().fromJson(response, clazz);
        } catch (Throwable e) {
            throw new ApiException("Can't resolve response json object. Method: " + method + ". Response: \n" + response + "\nError: " + e);
        }
    }

    @Override
    protected T onExecute(ApiConfig config) throws IOException, InterruptedException, ApiException {
//        if (mapping == Mapping.GET) {
//            return new ApiExecutor(config).execute(new ApiMethodCall.Builder()
//                    .setArgs(params)
//                    .setMethod(method)
//                    .build(), this);
//        } else if (mapping == Mapping.POST) {
//            return new ApiExecutor(config).execute(new ApiPostCall.Builder()
//                    .setArgs(params)
//                    .setMethod(method)
//                    .setBody(body)
//                    .build(), this);
//        }
        return new ApiExecutor(config).execute(new ApiMethodCall.Builder()
                .setArgs(params)
                .setMethod(method)
                .setBody(body)
                .setMapping(mapping)
                .build(), this);

        //throw new ApiException("Unknown mapping for the request");
    }

    public enum Mapping {
        GET,
        POST,
        DELETE
    }
}
