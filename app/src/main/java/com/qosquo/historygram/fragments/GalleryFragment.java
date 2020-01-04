package com.qosquo.historygram.fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.qosquo.historygram.R;
import com.qosquo.historygram.adapters.GalleryGridAdapter;
import com.qosquo.historygram.util.FilePaths;
import com.qosquo.historygram.util.FileSearch;
import com.qosquo.historygram.util.Global;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 343;
    private static final int SELECT_PICTURE_CODE = 766;
    private Toolbar mToolbar;
    private Spinner mSpinner;

    private ImageView mMediaImageView;
    private ImageView mSizeImageView;
    private ImageView mMultipleImageView;

    private ArrayList<String> directories;

    private boolean mIsMultipleSelected;
    private ArrayList<Bitmap> mSelectedBitmaps;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Firstly, check for image permissions
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.gallery_fragment, container, false);
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if (getContext() != null && getActivity() != null) {
//            if (getContext().checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(getActivity(),
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        READ_EXTERNAL_STORAGE_PERMISSION_CODE);
//            }
//        }
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mToolbar = view.findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_cancel_24);
        ((AppCompatActivity) view.getContext()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) view.getContext()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbar.setNavigationOnClickListener(v ->
                Objects.requireNonNull(getActivity()).onBackPressed());

        mSpinner = view.findViewById(R.id.spinner);
        directories = new ArrayList<>();
        initSpinner();

        mMediaImageView = view.findViewById(R.id.imageView_media);

        mSizeImageView = view.findViewById(R.id.imageView_size);
        mSizeImageView.setOnClickListener(v -> {
            if (mMediaImageView.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
                mMediaImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else if (mMediaImageView.getScaleType() == ImageView.ScaleType.FIT_CENTER) {
                mMediaImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

        });

        mMultipleImageView = view.findViewById(R.id.imageView_multiple);
        mSelectedBitmaps = new ArrayList<>();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.gallery_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_next) {
            if (getView() != null) {
                if (mMediaImageView.getDrawable() == null || mSelectedBitmaps.size() == 0) {
                    return false;
                }

                Bundle args = new Bundle();
                ArrayList<Bitmap> bitmaps = new ArrayList<>();
                for (Bitmap bitmap : mSelectedBitmaps) {
                    if (mMediaImageView.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
                        Bitmap dstBtm = cropImage(bitmap);
                        bitmaps.add(dstBtm);
                    } else {
                        bitmaps.add(bitmap);
                    }
                }
//                if (mIsMultipleSelected) {
//                    for (Bitmap bitmap : mSelectedBitmaps) {
//                        Bitmap dstBtm = cropImage(bitmap);
//                        bitmaps.add(dstBtm);
//                    }
//                } else {
//                    bitmaps.clear();
//                    Bitmap srcBtm = mSelectedBitmaps.get(0);
//                    if (mMediaImageView.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
//                        Bitmap dstBtm = cropImage(srcBtm);
//                        bitmaps.add(dstBtm);
//                    } else {
//                        bitmaps.add(srcBtm);
//                    }
//                }

                args.putParcelableArrayList(CaptionFragment.ARGUMENT_BITMAPLIST, bitmaps);
//                if (mIsMultipleSelected) {
//                    ArrayList<Bitmap> bitmaps = new ArrayList<>();
//                    for (Bitmap bitmap : mSelectedBitmaps) {
//                        Bitmap dstBtm = cropImage(bitmap);
//                        bitmaps.add(dstBtm);
//                    }
//                    args.putParcelableArrayList(CaptionFragment.ARGUMENT_BITMAPLIST, bitmaps);
//                    Global.handleReplaceFragment(getView(), new CaptionFragment(), args);
//                    return true;
//                }
//
//                Bitmap srcBtm = ((BitmapDrawable) mMediaImageView.getDrawable()).getBitmap();
//                if (mMediaImageView.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
//                    Bitmap dstBtm = cropImage(srcBtm);
//                    args.putParcelable(CaptionFragment.ARGUMENT_BITMAP, dstBtm);
//                } else {
//                    args.putParcelable(CaptionFragment.ARGUMENT_BITMAP, srcBtm);
//                }

                Global.handleReplaceFragment(getView(), new CaptionFragment(), args);
                return true;
            }
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Picasso.get()
                        .load(data.getData())
                        .into(mMediaImageView);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initSpinner();
            }
        }
    }

    private void initSpinner() {
        if (FileSearch.getDirectoryPaths(FilePaths.DOWNLOAD) != null) {
            directories.add(FilePaths.DOWNLOAD);
        }
        if (FileSearch.getDirectoryPaths(FilePaths.SCREENSHOTS) != null) {
            directories.add(FilePaths.SCREENSHOTS);
        }

        ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < directories.size(); i++) {
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }
        directoryNames.add(getResources().getString(R.string.choose_picture));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                Objects.requireNonNull(getContext()),
                R.layout.support_simple_spinner_dropdown_item, directoryNames);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!Integer.valueOf(position).equals(directoryNames.size()-1)) {
                    System.out.println("onItemClick: selected: " + directories.get(position));
                    //setup our image grid for the directory chosen
                    setupGridView(directories.get(position));
                    mMultipleImageView.setOnClickListener(v -> {
                        mIsMultipleSelected = !mIsMultipleSelected;
                        mSelectedBitmaps.clear();
//                        mMediaImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                        mSizeImageView.setEnabled(!mIsMultipleSelected);
                        setupGridView(directories.get(position));
                    });
                } else {
                    selectPictureFromDirectory();
                    mSpinner.setSelection(position-1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void setupGridView(String selectedDirectory) {
        final ArrayList<String> imgUrls = FileSearch.getFilePaths(selectedDirectory);
        Picasso.get()
                .load("file://" + imgUrls.get(0))
                .into(mMediaImageView);

        if (getView() != null) {
            RecyclerView mRecyclerView = getView().findViewById(R.id.recyclerView);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
            mRecyclerView.setLayoutManager(mLayoutManager);

            RecyclerView.Adapter mAdapter = new GalleryGridAdapter(getContext(),
                    imgUrls,
                    this::selectPicture,
                    mIsMultipleSelected);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private void selectPicture(@NonNull String url) {
//        Picasso.get()
//                .load(url)
//                .into(mMediaImageView);

        Picasso.get()
                .load(url)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mMediaImageView.setImageBitmap(bitmap);

                        if (mIsMultipleSelected) {
                            if (mSelectedBitmaps.contains(bitmap)) {
                                mSelectedBitmaps.remove(bitmap);
                            } else {
                                mSelectedBitmaps.add(bitmap);
                            }
                            System.out.println("Selected bitmaps: " + mSelectedBitmaps.size());
                        } else {
                            mSelectedBitmaps.clear();
                            mSelectedBitmaps.add(bitmap);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        mMediaImageView.setImageDrawable(placeHolderDrawable);
                    }
                });

//        if (mIsMultipleSelected) {
//            if (mSelected.contains(url)) {
//                mSelected.remove(url);
//            } else {
//                mSelected.add(url);
//            }
//            System.out.println("Selected photos: " + mSelected);
//        }
    }

    private void selectPictureFromDirectory() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), SELECT_PICTURE_CODE);
    }

    private Bitmap cropImage(Bitmap srcBmp) {
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()) {
            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );
        } else {
            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }

        return dstBmp;
    }

}
