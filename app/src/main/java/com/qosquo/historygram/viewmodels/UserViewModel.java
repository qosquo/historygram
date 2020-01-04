package com.qosquo.historygram.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.qosquo.historygram.models.User;
import com.qosquo.historygram.repositories.UserRepository;

public class UserViewModel extends ViewModel {
    private MutableLiveData<Boolean> reloadTrigger = new MutableLiveData<>();
    private LiveData<User> mUser;
    private UserRepository mRepo;

    /**
     * Initializes User ViewModel. Gets User object of ID from repository
     * <p>
     * See {@link UserRepository#getUser(int) repository implementation}
     *
     * @param id User ID
     * @see User
     */
    public void init(int id) {
        if (mUser != null && mUser.getValue() != null) {
            if (mUser.getValue().getId() == id) {
                return;
            }
        }

        refreshUser();

        mRepo = UserRepository.getInstance();
        mUser = Transformations.switchMap(reloadTrigger, input -> mRepo.getUser(id));
    }

    /**
     * Calls repository request to follow User
     * <p>
     * See {@link UserRepository#follow(int) repository implementation}
     *
     * @param userId  User ID
     */
    public void follow(final int userId) {
        refreshUser();

        mRepo.follow(userId);
    }

    public void refreshUser() {
        reloadTrigger.setValue(true);
    }

    public LiveData<User> getUser() {
        return mUser;
    }
}
