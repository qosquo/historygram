package com.historygram.api.requests;

import com.google.gson.Gson;
import com.qosquo.historygram.models.User;

import java.util.Map;

public class EditUserRequest extends ApiRequest<Integer> {
    public EditUserRequest(final int userId, final Map<String, String> fields) {
        super("/users/" + userId, Integer.class);
        setMapping(Mapping.POST);
        addBody(new Gson().toJson(fields));
    }
}
