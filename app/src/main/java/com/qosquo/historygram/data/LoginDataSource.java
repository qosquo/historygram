package com.qosquo.historygram.data;

import android.os.AsyncTask;
import android.util.Log;

import com.historygram.api.Api;
import com.historygram.api.exceptions.ApiException;
import com.historygram.api.models.Token;
import com.historygram.api.requests.ApiBooleanRequest;
import com.historygram.api.requests.LoginRequest;
import com.historygram.api.requests.RegisterRequest;
import com.qosquo.historygram.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        try {
            LoggedInUser loggedInUser = new LoginTask().execute(username, password).get();
            if (loggedInUser != null) {
                return new Result.Success<>(loggedInUser);
            } else {
                return new Result.Error(new ApiException("Login failed"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public Result<LoggedInUser> register(String username, String password) {
        try {
            LoggedInUser loggedInUser = new RegisterTask().execute(username, password).get();
            if (loggedInUser != null) {
                return new Result.Success<>(loggedInUser);
            } else {
                throw new Exception("The User with this name already exists");
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error registering in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    static class LoginTask extends AsyncTask<String, Void, LoggedInUser> {
        @Override
        protected LoggedInUser doInBackground(String... strings) {
            if (strings == null || strings.length == 0) {
                Log.e("ApiAuth", "Login failed: username or password are not provided");
                return null;
            }

            String username = strings[0];
            String password = strings[1];

            try {
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
                Token result = Api.executeSync(new LoginRequest(username, password));
                return new LoggedInUser(result.getToken(), result.getId(), username, password);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            return null;
        }
    }

    static class RegisterTask extends AsyncTask<String, Void, LoggedInUser> {
        @Override
        protected LoggedInUser doInBackground(String... strings) {
            if (strings == null || strings.length == 0) {
                Log.e("ApiAuth", "Login failed: username or password are not provided");
                return null;
            }

            String username = strings[0];
            String password = strings[1];

            try {
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
                Token result = Api.executeSync(new RegisterRequest(username, password));
                System.out.println(result.getToken());
                return new LoggedInUser(result.getToken(), result.getId(), username, password);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            return null;
        }
    }
}
