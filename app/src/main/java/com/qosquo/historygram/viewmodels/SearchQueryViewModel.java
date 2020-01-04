package com.qosquo.historygram.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.historygram.api.models.SearchResponse;
import com.qosquo.historygram.repositories.UsersRepository;

public class SearchQueryViewModel extends ViewModel {
    private LiveData<SearchResponse> mResponse;

    private MutableLiveData<String> mQuery;

    private UsersRepository mRepo;

    public void init() {
        if (mQuery != null) {
            return;
        }

        mRepo = UsersRepository.getInstance();
        mQuery = new MutableLiveData<>();

        mResponse = Transformations.switchMap(mQuery, input -> {
            if (input != null) {
                return mRepo.getQuery(input);
            }

            return null;
        });
    }

    public MutableLiveData<String> getQuery() {
        return mQuery;
    }

    public LiveData<SearchResponse> getResponse() {
        return mResponse;
    }
}
