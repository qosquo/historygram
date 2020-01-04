package com.historygram.api.requests;

import com.historygram.api.exceptions.ApiException;

public class ApiBooleanRequest extends ApiRequest<Boolean> {
    public ApiBooleanRequest(String method) {
        super(method, Boolean.class);
        this.setMapping(Mapping.POST);
    }

    @Override
    public Boolean parse(String response) throws ApiException {
        return true;
    }
}
