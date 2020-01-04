package com.historygram.api.requests;

import com.qosquo.historygram.models.User;

public class UserRequest extends ApiRequest<User> {
    public UserRequest(final int id) {
        super("/users/" + id, User.class);
    }
}
