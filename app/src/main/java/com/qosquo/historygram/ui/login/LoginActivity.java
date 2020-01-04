package com.qosquo.historygram.ui.login;

import android.Manifest;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.historygram.api.Api;
import com.historygram.api.ApiCallback;
import com.historygram.api.ApiConfig;
import com.historygram.api.exceptions.ApiException;
import com.historygram.api.models.Token;
import com.historygram.api.requests.EditUserRequest;
import com.qosquo.historygram.R;
import com.qosquo.historygram.data.model.LoggedInUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 52;
    private static final int SELECT_PICTURE_CODE = 1;

    private LoginViewModel loginViewModel;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar loadingProgressBar;
    private LinearLayout registerLayout;

    private ImageView profilePicture;
    private Button changePicture;
    private EditText username;
    private EditText fullName;
    private EditText biography;
    private Button register;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.register);
        loadingProgressBar = findViewById(R.id.loading);
        registerLayout = findViewById(R.id.linearLayout_register);
        register = findViewById(R.id.button_register);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                boolean isDataValid = loginFormState.isDataValid();
                loginButton.setEnabled(isDataValid);
                register.setEnabled(isDataValid);
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateSharedPreferences(loginResult.getSuccess());
                    //authenticate(loginResult.getSuccess());
                    setResult(Activity.RESULT_OK);

                    //Complete and destroy login activity once successful
                    finish();
                }
            }
        });

        loginViewModel.getRegisterResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateSharedPreferences(loginResult.getSuccess());
                updateUser(loginResult.getSuccess());
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        registerButton.setOnClickListener(v -> {
            registerLayout.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.GONE);

            initRegisterLayout();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE_CODE && data != null) {
                Picasso.get()
                        .load(data.getData())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .centerCrop()
                        .fit()
                        .into((ImageView) findViewById(R.id.imageView_profile));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"), SELECT_PICTURE_CODE);
            }
        }
    }

    private void initRegisterLayout() {
        profilePicture = findViewById(R.id.imageView_profile);
        changePicture = findViewById(R.id.button_changePhoto);
        username = findViewById(R.id.editText_username);
        fullName = findViewById(R.id.editText_name);
        biography = findViewById(R.id.editText_biography);
        register = findViewById(R.id.button_register);

        changePicture.setOnClickListener(v -> {
            // Firstly, check for image permissions
            if (getBaseContext().checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_PERMISSION_CODE);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"), SELECT_PICTURE_CODE);
            }
        });

        register.setOnClickListener(v -> loginViewModel.register(usernameEditText.getText().toString(),
                passwordEditText.getText().toString()));

    }

    private void updateUser(LoggedInUserView model) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("users/" + model.getId());
        StorageReference pictureReference = storageReference.child(UUID.randomUUID().toString());
        Bitmap bitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = pictureReference.putBytes(data);
        uploadTask.addOnSuccessListener(task -> {
            pictureReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d("ApiAuth", uri.toString());
                HashMap<String, String> fields = new HashMap<>();
                fields.put("username", username.getText().toString());
                fields.put("name", fullName.getText().toString());
                fields.put("profilePicture", uri.toString());
                fields.put("biography", biography.getText().toString());

                Api.execute(new EditUserRequest(model.getId(), fields), new ApiCallback<Integer>() {
                    @Override
                    public void success(Integer result) {
                        System.out.println("EDIT USER SUCCESS");
                        System.out.println(result);

                        setResult(Activity.RESULT_OK);
                        finish();
                    }

                    @Override
                    public void fail(Exception error) {
                        try {
                            throw new ApiException("Update user failed");
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                    }
                });
            });
        });
    }

    private void updateSharedPreferences(LoggedInUserView model) {
        SharedPreferences sharedPreferences = getSharedPreferences("api", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USERNAME", model.getLogin());
        editor.putString("PASSWORD", model.getPassword());
        editor.putString("TOKEN", model.getToken());
        editor.putInt("ID", model.getId());
        editor.putLong("LAST_AUTH", System.currentTimeMillis());
        editor.apply();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
