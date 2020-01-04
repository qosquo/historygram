package com.historygram.api.requests;

import com.historygram.api.models.SearchResponse;

public class SearchQueryRequest extends ApiRequest<SearchResponse> {
    public SearchQueryRequest(final String query) {
        super("/search", SearchResponse.class);
        addParam("q", query);
    }
}
