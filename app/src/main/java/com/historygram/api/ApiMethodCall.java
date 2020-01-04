package com.historygram.api;

import androidx.annotation.Nullable;

import com.historygram.api.requests.ApiRequest;

import java.util.HashMap;
import java.util.Map;

public class ApiMethodCall {

    private String method;
    private Map<String, String> args;
    private String body;
    private ApiRequest.Mapping mapping;

    private ApiMethodCall() {}

    private ApiMethodCall(Builder builder) {
        method = builder.method;
        args = builder.args;
        body = builder.body;
        mapping = builder.mapping;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    @Nullable
    public String getBody() {
        return body;
    }

    public ApiRequest.Mapping getMapping() {
        return mapping;
    }

    public static class Builder {
        private String method = "";
        private HashMap<String, String> args = new HashMap<>();
        private String body = null;
        private ApiRequest.Mapping mapping = ApiRequest.Mapping.GET;

        public Builder() {}

        public Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder setArgs(Map<String, String> args) {
            this.args.putAll(args);
            return this;
        }

        public Builder setArgs(String key, String value) {
            this.args.put(key, value);
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setMapping(ApiRequest.Mapping mapping) {
            this.mapping = mapping;
            return this;
        }

        public ApiMethodCall build() {
            return new ApiMethodCall(this);
        }
    }

}
