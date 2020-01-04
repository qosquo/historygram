package com.qosquo.historygram.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.qosquo.historygram.models.Article;
import com.qosquo.historygram.repositories.ArticlesRepository;

public class ArticleViewModel extends ViewModel {
    private MutableLiveData<Article> mArticle = new MutableLiveData<>();
    private ArticlesRepository mRepo = ArticlesRepository.getInstance();

    public void init(final int id) {
        mArticle = mRepo.getArticle(id);
    }

    public LiveData<Article> getArticle() {
        return mArticle;
    }

}
