package com.qosquo.historygram.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.historygram.api.Api;
import com.historygram.api.ApiCallback;
import com.historygram.api.exceptions.ApiException;
import com.historygram.api.requests.EditUserRequest;
import com.qosquo.historygram.R;
import com.qosquo.historygram.models.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    public static final String ARG_USER = "ARG_USER";
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 52;
    private static final int SELECT_PICTURE_CODE = 1;

    private Toolbar mToolbar;

    private ImageView mProfileImageView;
    private Button mChangePhotoButton;
    private EditText mUsernameEditText;
    private EditText mNameEditText;
    private EditText mBiographyEditText;

    private User user;
    private boolean changedPhoto;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.edit_profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Get toolbar view and set it as a support action bar
        mToolbar = view.findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_cancel_24);
        ((AppCompatActivity) view.getContext()).setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(v ->
                Objects.requireNonNull(getActivity()).onBackPressed());

        // Get user argument
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_USER);
        }

        mProfileImageView = view.findViewById(R.id.imageView_profile);
        Picasso.get()
                .load(user.getProfilePicture())
                .into(mProfileImageView);

        mChangePhotoButton = view.findViewById(R.id.button_changePhoto);

        mUsernameEditText = view.findViewById(R.id.editText_username);
        mUsernameEditText.setText(user.getUsername());

        mNameEditText = view.findViewById(R.id.editText_name);
        mNameEditText.setText(user.getName());

        mBiographyEditText = view.findViewById(R.id.editText_biography);
        mBiographyEditText.setText(user.getBiography());
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getContext() == null || getActivity() == null) {
            return;
        }

        mChangePhotoButton.setOnClickListener(v -> {
            // Firstly, check for image permissions
            if (getContext().checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_PERMISSION_CODE);
            } else {
                selectPicture();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE_CODE && data != null) {
                Picasso.get()
                        .load(data.getData())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .centerCrop()
                        .fit()
                        .into(mProfileImageView);

                changedPhoto = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectPicture();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_profile_menu, menu);

        menu.getItem(0).getIcon().setTint(getResources().getColor(R.color.blue, null));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if (changedPhoto) {
                uploadPhoto();
            } else {
                updateUser(null);
            }
            return true;
        }

        return false;
    }

    private void uploadPhoto() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("users/" + user.getId());
        StorageReference pictureReference = storageReference.child(UUID.randomUUID().toString());
        Bitmap bitmap = ((BitmapDrawable) mProfileImageView.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = pictureReference.putBytes(data);
        uploadTask.addOnSuccessListener(task -> pictureReference.getDownloadUrl().addOnSuccessListener(uri -> {
            updateUser(uri);
//            Log.d("Api", uri.toString());
//            HashMap<String, String> fields = new HashMap<>();
//            fields.put("username", mUsernameEditText.getText().toString());
//            fields.put("name", mNameEditText.getText().toString());
//            fields.put("profilePicture", uri.toString());
//            fields.put("biography", mBiographyEditText.getText().toString());
//
//            Api.execute(new EditUserRequest(user.getId(), fields), new ApiCallback<Integer>() {
//                @Override
//                public void success(Integer result) {
//                    System.out.println("EDIT USER SUCCESS");
//                    System.out.println(result);
//                    Objects.requireNonNull(getActivity()).onBackPressed();
//                }
//
//                @Override
//                public void fail(Exception error) {
//                    Toast.makeText(getActivity(), "Update user request failed", Toast.LENGTH_SHORT).show();
//                }
//            });
        }));
    }

    private void updateUser(@Nullable Uri uri) {
        HashMap<String, String> fields = new HashMap<>();
        fields.put("username", mUsernameEditText.getText().toString());
        fields.put("name", mNameEditText.getText().toString());
        fields.put("biography", mBiographyEditText.getText().toString());

        if (uri != null) {
            Log.d("Api", uri.toString());
            fields.put("profilePicture", uri.toString());
        }

        Api.execute(new EditUserRequest(user.getId(), fields), new ApiCallback<Integer>() {
            @Override
            public void success(Integer result) {
                System.out.println("EDIT USER SUCCESS");
                System.out.println(result);
                Objects.requireNonNull(getActivity()).onBackPressed();
            }

            @Override
            public void fail(Exception error) {
                Toast.makeText(getActivity(), "Update user request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), SELECT_PICTURE_CODE);
    }
}
