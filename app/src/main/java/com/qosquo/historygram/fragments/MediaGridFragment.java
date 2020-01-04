package com.qosquo.historygram.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.historygram.api.models.Hashtag;
import com.qosquo.historygram.R;
import com.qosquo.historygram.adapters.MediaGridAdapter;
import com.qosquo.historygram.adapters.UsersAdapter;
import com.qosquo.historygram.viewmodels.MediaViewModel;
import com.qosquo.historygram.viewmodels.UsersViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class MediaGridFragment extends Fragment {
    private static final String TAG = "MediaGridFragment";

    public static final String ARGUMENT_SORTING = "ARGUMENT_SORTING";
    public static final int SORTING_TOP = 0;
    public static final int SORTING_RECENT = 1;

    private MediaViewModel mViewModel;

    private RecyclerView mRecyclerView;

    private int mHashtagId;
    private int mSorting;

    public MediaGridFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.media_grid_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mHashtagId = getArguments().getInt(HashtagFragment.ARGUMENT_HASHTAGID);
            mSorting = getArguments().getInt(ARGUMENT_SORTING);
        }

        mRecyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setAdapter(new MediaGridAdapter(getContext(), null));
        mRecyclerView.setLayoutManager(layoutManager);

        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(MediaViewModel.class);
        mViewModel.init();
        if (mSorting == SORTING_TOP) {
            mViewModel.hashtagTop(mHashtagId, Arrays.asList("id", "mediaUrl"));
        } else if (mSorting == SORTING_RECENT) {
            mViewModel.hashtagRecent(mHashtagId, Arrays.asList("id", "mediaUrl"));
        }

        mViewModel.getMedia().observe(this, media -> {
            if (mSorting == SORTING_TOP) {
                mRecyclerView.setAdapter(new MediaGridAdapter(getContext(),
                        media,
                        mHashtagId,
                        MediaFragment.MediaView.HASHTAG_TOP));
            } else if (mSorting == SORTING_RECENT) {
                mRecyclerView.setAdapter(new MediaGridAdapter(getContext(),
                        media,
                        mHashtagId,
                        MediaFragment.MediaView.HASHTAG_RECENT));
            } else {
                Collections.sort(media, (o1, o2) -> o2.getId().compareTo(o1.getId()));
                mRecyclerView.setAdapter(new MediaGridAdapter(getContext(), media));
            }
        });
    }
}
