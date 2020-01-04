package com.qosquo.historygram.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.historygram.api.Api;
import com.historygram.api.ApiCallback;
import com.historygram.api.requests.PublishRequest;
import com.qosquo.historygram.CreateArticleActivity;
import com.qosquo.historygram.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CaptionFragment extends Fragment {

    public static final String ARGUMENT_BITMAP = "ARGUMENT_BITMAP";
    public static final String ARGUMENT_BITMAPLIST = "ARGUMENT_BITMAPLIST";
    public static final String EXTRA_ARTICLEID = "EXTRA_ARTICLEID";
    public static final int NEW_ARTICLE_REQUEST_CODE = 155;

    private Toolbar mToolbar;

    private ImageView mMediaImageView;
    private EditText mCaptionEditText;
    private EditText mDateEditText;
    private Switch mEraSwitch;
    private Button mArticleButton;

    private Bitmap mBitmap;
    private ArrayList<Bitmap> mBitmapList;
    private FirebaseStorage mStorage;
    private Set<Integer> mMediaIdList;

    private Integer articleId;

    private boolean mCommonEra;
    private int mYear;
    private int mMonth;
    private int mDay;
    private long mTimestamp;

    public CaptionFragment() {
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
        return inflater.inflate(R.layout.caption_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar = view.findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_24);
        ((AppCompatActivity) view.getContext()).setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(v ->
                Objects.requireNonNull(getActivity()).onBackPressed());

        mMediaImageView = view.findViewById(R.id.imageView_media);
        if (getArguments() != null) {
            if (getArguments().getParcelable(ARGUMENT_BITMAP) != null) {
                mBitmap = getArguments().getParcelable(ARGUMENT_BITMAP);
                mMediaImageView.setImageBitmap(mBitmap);
            }

            if (getArguments().getParcelableArrayList(ARGUMENT_BITMAPLIST) != null) {
                mBitmapList = getArguments().getParcelableArrayList(ARGUMENT_BITMAPLIST);
                if (mBitmapList != null) {
                    mMediaImageView.setImageBitmap(mBitmapList.get(0));
                    mMediaIdList = new HashSet<>();
                    System.out.println(mBitmapList.size());
                }
            }
        }

        mCaptionEditText = view.findViewById(R.id.editText_caption);
        mDateEditText = view.findViewById(R.id.editText_date);
        mDateEditText.setText(getSimpleDateFormat().format(new Date(System.currentTimeMillis())));
        mDateEditText.setOnClickListener(v -> {
            Calendar currentDate = Calendar.getInstance();
            mYear = currentDate.get(Calendar.YEAR);
            mMonth = currentDate.get(Calendar.MONTH);
            mDay = currentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()),
                    new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    if (mCommonEra) {
                        calendar.set(Calendar.ERA, GregorianCalendar.AD);
                    } else {
                        calendar.set(Calendar.ERA, GregorianCalendar.BC);
                    }

                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    mDateEditText.setText(getSimpleDateFormat().format(calendar.getTime()));

                    mTimestamp = calendar.getTimeInMillis();
                    mYear = year;
                    mMonth = month;
                    mDay = dayOfMonth;
                }
            }, mYear, mMonth, mDay);
            currentDate.set(1, 0, 1);
            datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
            datePickerDialog.show();
        });
        mEraSwitch = view.findViewById(R.id.switch_era);
        mEraSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mCommonEra = isChecked;

            mDateEditText.setText(getSimpleDateFormat()
                    .format(new Date(System.currentTimeMillis())));
        });
        mCommonEra = true;

        mArticleButton = view.findViewById(R.id.button_article);
        mArticleButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateArticleActivity.class);
            startActivityForResult(intent, NEW_ARTICLE_REQUEST_CODE);
        });

        mStorage = FirebaseStorage.getInstance();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.publish_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_publish) {
//            uploadToStorage();
            if (mBitmapList != null) {
                for (int i = 0; i < mBitmapList.size(); i++) {
                    uploadToStorageMultipleFiles(i);
                }
            }
//            if (getActivity() != null) {
//                getActivity().finish();
//            }
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_ARTICLE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                articleId = data.getIntExtra(EXTRA_ARTICLEID, 0);
                mArticleButton.setEnabled(false);
                Log.d("Article", "Article ID: " + articleId);
            }
        }
    }

    @Deprecated
    private void uploadToStorage() {
        StorageReference storageReference = mStorage.getReference("media/" + Api.getConfig().getUserId());
        StorageReference mediaRef = storageReference.child(UUID.randomUUID().toString());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // disable 'publish' menu item in toolbar
        MenuItem menuItem = mToolbar.getMenu().findItem(R.id.action_publish);
        menuItem.setEnabled(false);

        mCaptionEditText.clearFocus();
        mCaptionEditText.setEnabled(false);
        mDateEditText.setEnabled(false);
        mEraSwitch.setEnabled(false);
        mArticleButton.setEnabled(false);

        // Download media to firebase storage
        UploadTask uploadTask = mediaRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            System.out.println("COOL!");
            mediaRef.getDownloadUrl().addOnCompleteListener(this::uploadMedia);
        });
    }

    private void uploadToStorageMultipleFiles(final int position) {
        StorageReference storageReference = mStorage.getReference("media/" + Api.getConfig().getUserId());
        StorageReference mediaRef = storageReference.child(UUID.randomUUID().toString());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mBitmapList.get(position).compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // disable 'publish' menu item in toolbar
        MenuItem menuItem = mToolbar.getMenu().findItem(R.id.action_publish);
        menuItem.setEnabled(false);

        mCaptionEditText.clearFocus();
        mCaptionEditText.setEnabled(false);
        mDateEditText.setEnabled(false);
        mEraSwitch.setEnabled(false);
        mArticleButton.setEnabled(false);

        // Download media to firebase storage
        UploadTask uploadTask = mediaRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            System.out.println("COOL! POSITION " + position);
            mediaRef.getDownloadUrl().addOnCompleteListener(this::uploadMedia);
        });
    }

    private void uploadMedia(@NonNull Task<Uri> uriTask) {
        Uri imageUrl = uriTask.getResult();
        System.out.println("Caption edit text: " + mCaptionEditText.getText().toString());
        System.out.println("User ID: " + Api.getConfig().getUserId());
        if (imageUrl != null) {
            Api.execute(new PublishRequest(
                    Api.getConfig().getUserId(),
                    Uri.encode(imageUrl.toString()),
                    Uri.encode(mCaptionEditText.getText().toString()),
                    mTimestamp,
                    articleId),
                    new ApiCallback<Integer>() {
                @Override
                public void success(Integer result) {
                    System.out.println("COOL Media published to the server");
                    mMediaIdList.add(result);
                    System.out.println(mMediaIdList);
                    if (mMediaIdList.size() == mBitmapList.size()) {
                        publishMedia();
                    }
                }

                @Override
                public void fail(Exception error) {
                    error.printStackTrace();
                    Toast.makeText(getActivity(), "Media publishing failed: \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void publishMedia() {
        Api.execute(new PublishRequest(Api.getConfig().getUserId(), mMediaIdList),
                new ApiCallback<Integer>() {
                    @Override
                    public void success(Integer result) {
                        System.out.println("COOL!");
                        System.out.println(result);
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void fail(Exception error) {
                        error.printStackTrace();
                    }
                });
    }

    private SimpleDateFormat getSimpleDateFormat() {
        String format = "dd MMMM yyyy G";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return new SimpleDateFormat(format,
                    Objects.requireNonNull(getContext()).getResources().getConfiguration().getLocales().get(0));
        } else {
            return new SimpleDateFormat(format,
                    Objects.requireNonNull(getContext()).getResources().getConfiguration().locale);
        }
    }
}
