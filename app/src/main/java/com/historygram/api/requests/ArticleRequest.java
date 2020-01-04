package com.historygram.api.requests;

import com.google.gson.Gson;
import com.qosquo.historygram.models.Article;

import java.util.HashMap;

public class ArticleRequest extends ApiRequest<Article> {
    /**
     * Get Article object with Article ID
     * @param id Article ID
     */
    public ArticleRequest(final int id) {
        super("/articles/" + id, Article.class);
    }
}
