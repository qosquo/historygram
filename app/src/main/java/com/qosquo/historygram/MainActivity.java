package com.qosquo.historygram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.ablanco.zoomy.Zoomy;
import com.ablanco.zoomy.ZoomyConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.historygram.api.Api;
import com.historygram.api.ApiCallback;
import com.historygram.api.ApiConfig;
import com.historygram.api.models.Token;
import com.historygram.api.requests.UserRequest;
import com.qosquo.historygram.fragments.MediaFragment;
import com.qosquo.historygram.fragments.SearchFragment;
import com.qosquo.historygram.fragments.UserFragment;
import com.qosquo.historygram.models.User;
import com.qosquo.historygram.ui.login.LoginActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int LOGIN_REQUEST_CODE = 111;

    private MediaFragment mFeedFragment = new MediaFragment(MediaFragment.MediaView.FEED);
    private SearchFragment mSearchFragment = new SearchFragment();
    private UserFragment mUserFragment = new UserFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        setFragment(mFeedFragment);
                        return true;
                    case R.id.navigation_search:
                        setFragment(mSearchFragment);
                        return true;
                    case R.id.navigation_publish:
                        Intent intent = new Intent(getBaseContext(), PublishActivity.class);
                        startActivity(intent);
                        return false;
                    case R.id.navigation_notifications:
                        return false;
                    case R.id.navigation_user:
                        Bundle args = new Bundle();
                        args.putInt(UserFragment.ARGUMENT_USERID, Api.getConfig().getUserId());
                        setFragment(mUserFragment, args);
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        FrameLayout mFrameLayout = findViewById(R.id.frameLayout_main);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        checkLastAuth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LOGIN_REQUEST_CODE) {
                setFragment(mFeedFragment);
            }
        }
    }

    private void checkLastAuth() {
        SharedPreferences sharedPreferences = getSharedPreferences("api", Context.MODE_PRIVATE);
        long past = sharedPreferences.getLong("LAST_AUTH", 0);
        long now = System.currentTimeMillis();
        long difference = now - past;

        long inHours = TimeUnit.MILLISECONDS.toHours(difference);
        System.out.println("Past: " + past);
        System.out.println("Now: " + now);
        System.out.println("Difference in hours: " + inHours);

        if (inHours >= 5) {
            Log.d("ApiAuth", "Last authentication was earlier than 5 hours ago");

            String username = sharedPreferences.getString("USERNAME", "");
            String password = sharedPreferences.getString("PASSWORD", "");

            if ((username == null || username.isEmpty()) && (password == null || password.isEmpty())) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
            } else {
                Api.authenticate(username, password, new ApiCallback<Token>() {
                    @Override
                    public void success(Token result) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("TOKEN", result.getToken());
                        System.out.println("User ID: " + result.getId());
                        editor.putInt("ID", result.getId());
                        editor.putLong("LAST_AUTH", System.currentTimeMillis());
                        editor.apply();

                        setFragment(mFeedFragment);
                    }

                    @Override
                    public void fail(Exception error) {
                        Log.e("ApiAuth", "ЭЭЭЭ БЛЯТЬ ЧО ЗА ХУЙНЯ", error);
                    }
                });
            }
        } else {
            ApiConfig config = new ApiConfig();
            config.setAccessToken(sharedPreferences.getString("TOKEN", ""));
            config.setUserId(sharedPreferences.getInt("ID", 0));
            Api.setConfig(config);

            System.out.println("ACCESS TOKEN: " + Api.getConfig().getAccessToken());
            System.out.println("USER ID: " + Api.getConfig().getUserId());

            setFragment(mFeedFragment);
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack();
        fragmentTransaction.replace(R.id.frameLayout_main, fragment);
        fragmentTransaction.commit();
    }

    private void setFragment(Fragment fragment, Bundle args) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack();
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.frameLayout_main, fragment);
        fragmentTransaction.commit();
    }

}
