package com.historygram.api;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.historygram.api.exceptions.ApiException;
import com.historygram.api.models.Token;
import com.historygram.api.requests.ApiRequest;
import com.historygram.api.requests.LoginRequest;
import com.historygram.api.requests.RegisterRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;

public class Api {

    private static ApiScheduler scheduler = new ApiScheduler();
    private static ApiConfig config = new ApiConfig();

    public static ApiConfig getConfig() {
        return config;
    }

    public static void setConfig(ApiConfig config) {
        Api.config = config;
    }

    public static void authenticate(String username, String password, ApiCallback<Token> callback) {
        ApiScheduler.getNetworkExecutor().submit(() -> {
           try {
               Token result = executeSync(new LoginRequest(username, password));
               config.setAccessToken(result.getToken());
               config.setUserId(result.getId());

               System.out.println("ACCESS TOKEN: " + result.getToken());
               System.out.println("USER ID: " + result.getId());

               ApiScheduler.runOnMainThread(() -> callback.success(result));
           } catch (Exception e) {
               ApiScheduler.runOnMainThread(() -> callback.fail(e));
           }
        });
    }

    public static void register(String username, String password, ApiCallback<Token> callback) {
        ApiScheduler.getNetworkExecutor().submit(() -> {
            try {
                Token result = executeSync(new RegisterRequest(username, password));
                config.setAccessToken(result.getToken());
                config.setUserId(result.getId());

                ApiScheduler.runOnMainThread(() -> callback.success(result));
            } catch (Exception e) {
                ApiScheduler.runOnMainThread(() -> callback.fail(e));
            }
        });
    }

    public static <T> T executeSync(ApiCommand<T> cmd)
            throws ApiException, IOException, InterruptedException {
        return cmd.execute(config);
    }

    public static <T> void execute(ApiCommand<T> request, ApiCallback<T> callback) {
        ApiScheduler.getNetworkExecutor().submit(() -> {
           try {
               T result = executeSync(request);
               ApiScheduler.runOnMainThread(() -> callback.success(result));
           } catch (Exception e) {
               ApiScheduler.runOnMainThread(() -> callback.fail(e));
           }
        });
    }
}
