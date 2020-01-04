package com.historygram.api;

import com.historygram.api.exceptions.ApiException;

public interface ApiResponseParser<Result> {
    Result parse(String response) throws ApiException;
}
