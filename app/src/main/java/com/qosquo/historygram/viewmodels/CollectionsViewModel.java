package com.qosquo.historygram.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.qosquo.historygram.models.Collection;
import com.qosquo.historygram.repositories.CollectionsRepository;

import java.util.List;

public class CollectionsViewModel extends ViewModel {
    private MutableLiveData<Boolean> reloadTrigger = new MutableLiveData<>();
    private LiveData<List<Collection>> mCollections;
//    private LiveData<Collection> mCollection;
    private CollectionsRepository mRepo;

    public void init() {
        if (mCollections != null) {
            return;
        }

        refreshMedia();
        mRepo = CollectionsRepository.getInstance();
    }

    public void getAll() {
        refreshMedia();
        mCollections = Transformations.switchMap(reloadTrigger, input -> mRepo.getAll());
    }

//    public void getCollection(final int collectionId) {
//        refreshMedia();
//        mCollection = Transformations.switchMap(reloadTrigger, input -> mRepo.getCollection(collectionId));
//    }

    private void refreshMedia() {
        reloadTrigger.setValue(true);
    }

    public LiveData<List<Collection>> getCollections() {
        return mCollections;
    }
}
