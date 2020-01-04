package com.qosquo.historygram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.historygram.api.Api;
import com.historygram.api.ApiCallback;
import com.historygram.api.requests.ApiBooleanRequest;
import com.qosquo.historygram.models.Media;
import com.squareup.picasso.Picasso;

public class MediaActivity extends AppCompatActivity {
    private static final String TAG = "MediaActivity";

    public static final String EXTRA_MEDIA = "EXTRA_MEDIA";

    private ImageView mProfileImageView;
    private TextView mUsernameTextView;
    private ImageView mMediaImageView;
    private EditText mCaptionEditText;

    private Media mMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_24);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());

        mMedia = (Media) getIntent().getSerializableExtra(EXTRA_MEDIA);

        mProfileImageView = findViewById(R.id.imageView_profile);
        mUsernameTextView = findViewById(R.id.textView_username);
        mMediaImageView = findViewById(R.id.imageView_media);
        mCaptionEditText = findViewById(R.id.editText_caption);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Load user profile picture
        Picasso.get()
                .load(mMedia.getOwner().getProfilePicture())
                .centerCrop()
                .fit()
                .into(mProfileImageView);

        // Set username
        mUsernameTextView.setText(mMedia.getOwner().getUsername());

        // Get media image
        Picasso.get()
                .load(mMedia.getMediaUrl())
                .into(mMediaImageView);

        // Set previous caption
        mCaptionEditText.setText(mMedia.getCaption());
        // Request focus for edit text
        mCaptionEditText.requestFocus();
        // Show keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.publish_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish) {
            saveMedia();
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveMedia() {
        int id = mMedia.getId();
        String caption = Uri.encode(mCaptionEditText.getText().toString());

        ApiBooleanRequest request = new ApiBooleanRequest("/media/" + id);
        request.addParam("caption", caption);

        Api.execute(request, new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean result) {
                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, InputMethodManager.HIDE_IMPLICIT_ONLY);
                finish();
            }

            @Override
            public void fail(Exception error) {
                error.printStackTrace();
            }
        });
    }
}
