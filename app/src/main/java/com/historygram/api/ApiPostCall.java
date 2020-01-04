package com.historygram.api;

import java.util.HashMap;
import java.util.Map;

public class ApiPostCall {

    private String method;
    private Map<String, String> args;
    private String body;
    private Long timeout;

    private ApiPostCall() {}

    private ApiPostCall(Builder builder) {
        method = builder.method;
        args = builder.args;
        body = builder.body;
        timeout = builder.timeout;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public String getBody() {
        return body;
    }

    public Long getTimeout() {
        return timeout;
    }

    public static class Builder {
        private String method = "";
        private HashMap<String, String> args = new HashMap<>();
        private String body = "";
        private Long timeout = 0L;

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

        public Builder setTimeout(Long timeout) {
            this.timeout = timeout;
            return this;
        }

        public ApiPostCall build() {
            return new ApiPostCall(this);
        }

    }

}
