package com.qosquo.historygram.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.qosquo.historygram.R;
import com.qosquo.historygram.adapters.MediaGridAdapter;
import com.qosquo.historygram.adapters.MediaListAdapter;
import com.qosquo.historygram.models.Media;
import com.qosquo.historygram.util.Global;
import com.qosquo.historygram.viewmodels.MediaViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {

    private MediaViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView mSearchTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(MediaViewModel.class);
        mViewModel.init();

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mViewModel.refreshMedia());

        // Initialize recycler view
        mRecyclerView =  view.findViewById(R.id.recyclerView);
        // Use a linear layout manager
        mLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MediaGridAdapter(getContext(), new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);

        mViewModel.media();
        mViewModel.getMedia().observe(getActivity(), media -> {
            mSwipeRefreshLayout.setRefreshing(false);
            if (getContext() == null) {
                return;
            }
            Collections.sort(media, (o1, o2) -> o2.getId().compareTo(o1.getId()));
            mRecyclerView.setAdapter(new MediaGridAdapter(getContext(),
                    media,
                    MediaFragment.MediaView.EXPLORE));
        });

        // Get search text view
        mSearchTextView = view.findViewById(R.id.textView_search);
        mSearchTextView.setOnClickListener(v ->
                Global.handleReplaceFragment(view, new SearchQueryFragment(), null));
    }
}
