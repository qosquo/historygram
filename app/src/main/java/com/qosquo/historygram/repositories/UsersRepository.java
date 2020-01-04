package com.qosquo.historygram.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.historygram.api.Api;
import com.historygram.api.ApiCallback;
import com.historygram.api.models.SearchResponse;
import com.historygram.api.requests.ApiRequest;
import com.historygram.api.requests.SearchQueryRequest;
import com.qosquo.historygram.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsersRepository {
    private static final UsersRepository ourInstance = new UsersRepository();

    private List<User> users = new ArrayList<>();

    public static UsersRepository getInstance() {
        return ourInstance;
    }

    /**
     * Requests to get User or Hashtag objects with given {@code query} from repository
     *
     * @param query Search query, can be user or hashtag names, not null
     * @return Search query response
     * @see SearchResponse
     */
    public MutableLiveData<SearchResponse> getQuery(@NonNull String query) {
        MutableLiveData<SearchResponse> data = new MutableLiveData<>();

        Api.execute(new SearchQueryRequest(query), new ApiCallback<SearchResponse>() {
            @Override
            public void success(SearchResponse result) {
//                System.out.println("Repository: " + result.toString());
                data.setValue(result);
            }

            @Override
            public void fail(Exception error) {
                Log.e("ApiSearch", "Search query request failed", error);
            }
        });

        return data;
    }

    /**
     * Requests to get all User's who follows given {@code User}
     * @param userId User ID
     * @return MutableLiveData of List of Users
     * @see MutableLiveData
     */
    public MutableLiveData<List<User>> getFollowers(final int userId) {
        MutableLiveData<List<User>> data = new MutableLiveData<>();

        ApiRequest<User[]> request = new ApiRequest<>("/users/" + userId + "/followers", User[].class);
        Api.execute(request, new ApiCallback<User[]>() {
            @Override
            public void success(User[] result) {
//                System.out.println("Repository: " + Arrays.toString(result));
                data.setValue(Arrays.asList(result));
            }

            @Override
            public void fail(Exception error) {
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Requests to get all User's whom current {@code User} follows
     * @param userId User ID
     * @return MutableLiveData of List of Users
     * @see MutableLiveData
     */
    public MutableLiveData<List<User>> getFollows(final int userId) {
        MutableLiveData<List<User>> data = new MutableLiveData<>();

        ApiRequest<User[]> request = new ApiRequest<>("/users/" + userId + "/follows", User[].class);
        Api.execute(request, new ApiCallback<User[]>() {
            @Override
            public void success(User[] result) {
                data.setValue(Arrays.asList(result));
            }

            @Override
            public void fail(Exception error) {
                data.setValue(null);
            }
        });

        return data;
    }

}
