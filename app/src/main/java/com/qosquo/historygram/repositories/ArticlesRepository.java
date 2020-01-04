package com.qosquo.historygram.repositories;

import androidx.lifecycle.MutableLiveData;

import com.historygram.api.Api;
import com.historygram.api.ApiCallback;
import com.historygram.api.requests.ArticleRequest;
import com.qosquo.historygram.models.Article;

public class ArticlesRepository {
    private static final ArticlesRepository ourInstance = new ArticlesRepository();

    public static ArticlesRepository getInstance() {
        return ourInstance;
    }

    /**
     * Requests for Article object from repository
     * @param id Article ID
     * @return MutableLiveData of Article, can be null
     * @see Article
     * @see MutableLiveData
     */
    public MutableLiveData<Article> getArticle(final int id) {
        MutableLiveData<Article> data = new MutableLiveData<>();

        Api.execute(new ArticleRequest(id), new ApiCallback<Article>() {
            @Override
            public void success(Article result) {
                data.setValue(result);
            }

            @Override
            public void fail(Exception error) {
                error.printStackTrace();
                data.setValue(null);
            }
        });

        return data;
    }
}
