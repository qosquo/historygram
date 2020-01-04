package com.historygram.api.requests;

import com.google.gson.Gson;
import com.historygram.api.models.Token;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends ApiRequest<Token> {
    public RegisterRequest(String username, String password) {
        super("/register", Token.class);
        setMapping(Mapping.POST);
        Map<String, String> args = new HashMap<>();
        args.put("username", username);
        args.put("password", password);
        addBody(new Gson().toJson(args));
    }
}
