package com.qosquo.historygram.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.historygram.api.Api;
import com.historygram.api.ApiCallback;
import com.historygram.api.requests.ApiBooleanRequest;
import com.historygram.api.requests.ApiRequest;
import com.qosquo.historygram.models.Collection;
import com.qosquo.historygram.models.Media;

import java.util.Arrays;
import java.util.List;

public class CollectionsRepository {
    private static final String TAG = "CollectionsRepository";

    private static final CollectionsRepository ourInstance = new CollectionsRepository();

    public static CollectionsRepository getInstance() {
        return ourInstance;
    }

    private CollectionsRepository() {
    }

    /**
     * Requests to get all authorized User's Collections
     *
     * @return List of Collections
     * @see Collection
     */
    public MutableLiveData<List<Collection>> getAll() {
        MutableLiveData<List<Collection>> data = new MutableLiveData<>();

        Api.execute(new ApiRequest<>("/collections/all", Collection[].class), new ApiCallback<Collection[]>() {
            @Override
            public void success(Collection[] result) {
                data.setValue(Arrays.asList(result));
                Log.d(TAG, "success: collections: " + Arrays.toString(result));
            }

            @Override
            public void fail(Exception error) {
                data.setValue(null);
                error.printStackTrace();
            }
        });

        return data;
    }

    /**
     * Requests to get Collection with passed ID
     *
     * @param collectionId  Collection ID
     * @return Collection object
     * @see Collection
     */
    public MutableLiveData<List<Media>> getCollection(final int collectionId) {
        MutableLiveData<List<Media>> data = new MutableLiveData<>();
        ApiRequest<Media[]> request = new ApiRequest<>("/collections/" + collectionId, Media[].class);
        request.addParam("extended", "true");

        Api.execute(request, new ApiCallback<Media[]>() {
            @Override
            public void success(Media[] result) {
                data.setValue(Arrays.asList(result));
            }

            @Override
            public void fail(Exception error) {
                data.setValue(null);
                error.printStackTrace();
            }
        });

//        Api.execute(request, new ApiCallback<Collection>() {
//            @Override
//            public void success(Collection result) {
//                data.setValue(result);
//            }
//
//            @Override
//            public void fail(Exception error) {
//                data.setValue(null);
//                error.printStackTrace();
//            }
//        });

        return data;
    }

    /**
     * Creates new Collection object for authorized User
     *
     * @param name  New Collection's name, not null
     * @param itemId  Item ID to add to a new collection, may be null
     * @return Newly created Collection ID
     */
    public MutableLiveData<Integer> newCollection(@NonNull final String name,
                                                  @Nullable final Integer itemId) {
        MutableLiveData<Integer> data = new MutableLiveData<>();

        ApiRequest<Integer> request = new ApiRequest<>("/collections/new", Integer.class);
        request.setMapping(ApiRequest.Mapping.POST);
        request.addParam("name", name);
        if (itemId != null) {
            request.addParam("itemId", itemId);
        }

        Api.execute(request, new ApiCallback<Integer>() {
            @Override
            public void success(Integer result) {
                data.setValue(result);
            }

            @Override
            public void fail(Exception error) {
                data.setValue(null);
                error.printStackTrace();
            }
        });

        return data;
    }

    public void renameCollection(final int collectionId,
                                 @NonNull final String name) {
        ApiBooleanRequest request = new ApiBooleanRequest("/collections/rename");
        request.addParam("id", collectionId);
        request.addParam("name", name);

        Api.execute(request, new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean result) {
                Log.d(TAG, "success: " + result);
            }

            @Override
            public void fail(Exception error) {
                error.printStackTrace();
            }
        });
    }

    /**
     * Adds an item to a Collection.
     * If Collection ID is null, then item is added to a default collection.
     *
     * @param collectionId  Collection ID, may be null
     * @param itemId  Item ID to add, not null
     */
    public void addItem(@Nullable final Integer collectionId,
                        @NonNull final Integer itemId) {
        ApiBooleanRequest request = new ApiBooleanRequest("/collections/add");
        request.addParam("itemId", itemId);
        if (collectionId != null) {
            request.addParam("id", collectionId);
        }

        Api.execute(request, new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean result) {
                Log.d(TAG, "success: " + result);
            }

            @Override
            public void fail(Exception error) {
                error.printStackTrace();
            }
        });
    }

    /**
     * Removes an item from a Collection.
     * If Collection ID is null, then item is removed from all User's Collections.
     *
     * @param collectionId  Collection ID, may be null
     * @param itemId  Item ID to remove, not null
     */
    public void removeItem(@Nullable final Integer collectionId,
                           @NonNull final Integer itemId) {
        ApiBooleanRequest request = new ApiBooleanRequest("/collections/remove");
        request.addParam("itemId", itemId);
        if (collectionId != null) {
            request.addParam("id", collectionId);
        }

        Api.execute(request, new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean result) {
                Log.d(TAG, "success: " + result);
            }

            @Override
            public void fail(Exception error) {
                error.printStackTrace();
            }
        });
    }

    public void deleteCollection(final int collectionId) {
        ApiBooleanRequest request = new ApiBooleanRequest("/collections/" + collectionId);
        request.setMapping(ApiRequest.Mapping.DELETE);

        Api.execute(request, new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean result) {
                Log.d(TAG, "success: " + result);
            }

            @Override
            public void fail(Exception error) {
                error.printStackTrace();
            }
        });
    }
}
