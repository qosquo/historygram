package com.qosquo.historygram.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.qosquo.historygram.CreateArticleActivity;
import com.qosquo.historygram.R;
import com.qosquo.historygram.adapters.MediaListAdapter;
import com.qosquo.historygram.viewmodels.MediaViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class MediaFragment extends Fragment {
    public static final int ADD_ARTICLE_REQUEST_CODE = 522;

    public static final String ARG_POSITION = "ARG_POSITION";
    public static final String ARG_ITEMID = "ARG_ITEMID";

    private MediaViewModel mViewModel;

    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerView;
    private MediaListAdapter mAdapter;

    private int mPosition;
    private int mItemId;
    private MediaFragment.MediaView mView;
    private boolean mScroll;

    private Sort mSortBy;

    public MediaFragment() {
        this.mView = MediaView.USER;
    }

    public MediaFragment(MediaView view) {
        this.mView = view;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.media_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSortBy = Sort.NEWEST;

        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(MediaViewModel.class);
        mViewModel.init();

        switch (mView) {
            case FEED:
                mViewModel.feed();
                break;
            case EXPLORE:
                mViewModel.media();
                break;
            case USER:
                mViewModel.userMedia(mItemId);
                break;
            case HASHTAG_TOP:
                mViewModel.hashtagTop(mItemId);
                break;
            case HASHTAG_RECENT:
                mViewModel.hashtagRecent(mItemId);
                break;
            case COLLECTION:
                mViewModel.collection(mItemId);
                break;
        }

        mViewModel.getMedia().observe(this, media -> {
            if (media == null) {
                return;
            }

            mSwipeRefreshLayout.setRefreshing(false);
            mScroll = mAdapter.getItemCount() == 0;
            if (mSortBy == Sort.NEWEST) {
                Collections.sort(media, (o1, o2) -> o2.getId().compareTo(o1.getId()));
            } else if (mSortBy == Sort.DATE_OLDEST) {
                Collections.sort(media, (o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp()));
            } else if (mSortBy == Sort.DATE_NEWEST) {
                Collections.sort(media, (o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
            }
            mAdapter.update(media);
            if (mScroll) {
                mRecyclerView.scrollToPosition(mPosition);
            }
        });

        initRecyclerView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getContext() == null) {
            return;
        }
        if (getArguments() != null) {
            mItemId = getArguments().getInt(ARG_ITEMID);
        }

        // Set support action bar toolbar
        mToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) view.getContext()).setSupportActionBar(mToolbar);
        if (mView == MediaView.USER) {
            mToolbar.setTitle(view.getContext().getString(R.string.posts));
        } else if (mView == MediaView.EXPLORE) {
            mToolbar.setTitle(view.getContext().getString(R.string.explore));
        } else if (mView == MediaView.HASHTAG_TOP || mView == MediaView.HASHTAG_RECENT) {
            mToolbar.setTitle(view.getContext().getString(R.string.posts));
        }
        if (mView != MediaView.FEED) {
            mToolbar.setNavigationIcon(R.drawable.ic_back_24);
            mToolbar.setNavigationOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());
            mToolbar.setTitleTextColor(view.getContext().getColor(R.color.black));
        }

        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.media_sort_array, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mSortBy = Sort.NEWEST;
                        break;
                    case 1:
                        mSortBy = Sort.DATE_OLDEST;
                        break;
                    case 2:
                        mSortBy = Sort.DATE_NEWEST;
                        break;
                }
                mViewModel.refreshMedia();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Initialize swipe refresh layout
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mViewModel.refreshMedia());

        // Initialize recycler view
        mRecyclerView = view.findViewById(R.id.recyclerView);

        // Get position argument from fragment bundle
        mPosition = getArguments() != null ? getArguments().getInt(ARG_POSITION) : 0;
    }

    private void initRecyclerView() {
        // Use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new MediaListAdapter(getContext(), new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mRecyclerView.scrollToPosition(mPosition);
    }

    public enum MediaView {
        FEED,
        EXPLORE,
        USER,
        HASHTAG_TOP,
        HASHTAG_RECENT,
        COLLECTION
    }

    private enum Sort {
        NEWEST,
        DATE_OLDEST,
        DATE_NEWEST
    }
}
