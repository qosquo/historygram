package com.historygram.api;

import com.historygram.api.exceptions.ApiException;

import java.io.IOException;

public abstract class ApiCommand<Response> {

    public Response execute(ApiConfig config) throws ApiException, IOException, InterruptedException {
        return onExecute(config);
    }

    protected abstract Response onExecute(ApiConfig config) throws IOException, InterruptedException, ApiException;
}
