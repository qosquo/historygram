package com.historygram.api.requests;

public class FollowRequest extends ApiRequest<Integer> {
    /**
     * Request to follow a User
     * @param userId User to follow
     */
    public FollowRequest(final int userId) {
        super("/users/" + userId + "/follows", Integer.class);
        setMapping(Mapping.POST);
    }
}
