package com.qosquo.historygram.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.historygram.api.Api;
import com.historygram.api.ApiCallback;
import com.historygram.api.requests.FollowRequest;
import com.historygram.api.requests.UserRequest;
import com.qosquo.historygram.models.User;

public class UserRepository {
    private static final UserRepository ourInstance = new UserRepository();
    private User user;

    public static UserRepository getInstance() {
        return ourInstance;
    }

    /**
     * Requests to get User object from repository
     * @param id  User ID
     * @return MutableLiveData of User
     * @see User
     * @see MutableLiveData
     */
    public MutableLiveData<User> getUser(final int id) {
        MutableLiveData<User> data = new MutableLiveData<>();

        Api.execute(new UserRequest(id), new ApiCallback<User>() {
            @Override
            public void success(User result) {
//                System.out.println("Repository: " + result.toString());
                data.setValue(result);
            }

            @Override
            public void fail(Exception error) {
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Requests to add current User ID to follows list of another User
     * @param userId User ID to follow
     */
    public void follow(final int userId) {
        Api.execute(new FollowRequest(userId), new ApiCallback<Integer>() {
            @Override
            public void success(Integer result) {
                Log.d("Api", result.toString());
            }

            @Override
            public void fail(Exception error) {
                Log.e("Api", "Something went wrong", error);
            }
        });
    }

}
