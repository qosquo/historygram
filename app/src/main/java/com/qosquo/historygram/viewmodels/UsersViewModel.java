package com.qosquo.historygram.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.qosquo.historygram.models.User;
import com.qosquo.historygram.repositories.UsersRepository;

import java.util.List;

public class UsersViewModel extends ViewModel {
    private MutableLiveData<List<User>> mUsers = new MutableLiveData<>();
    private UsersRepository mRepo = UsersRepository.getInstance();

    /**
     * Calls repository request to get user's followers
     * <p>
     * See {@link UsersRepository#getFollowers(int) repository implementation}
     *
     * @param userId  User ID
     */
    public void getFollowers(final int userId) {
        mUsers = mRepo.getFollowers(userId);
    }

    /**
     * Calls repository request to get who user follows
     * <p>
     * See {@link UsersRepository#getFollows(int) repository implementation}
     *
     * @param userId  User ID
     */
    public void getFollows(final int userId) {
        mUsers = mRepo.getFollows(userId);
    }

    public LiveData<List<User>> getUsers() {
        return mUsers;
    }

}
