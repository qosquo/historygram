package com.qosquo.historygram.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qosquo.historygram.R;
import com.qosquo.historygram.adapters.UsersAdapter;
import com.qosquo.historygram.viewmodels.UsersViewModel;

import java.util.Collections;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowersFragment extends Fragment {
    private static final String TAG = "FollowersFragment";

    private UsersViewModel mViewModel;

    private RecyclerView mRecyclerView;

    private int mUserId;

    public FollowersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.followers_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mUserId = getArguments().getInt(UsersFragment.ARGUMENT_USERID);
        }

        mRecyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setAdapter(new UsersAdapter(getContext(), Collections.emptyList()));
        mRecyclerView.setLayoutManager(layoutManager);

        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(UsersViewModel.class);
        mViewModel.getFollowers(mUserId);

        mViewModel.getUsers().observe(this, users ->
                mRecyclerView.setAdapter(new UsersAdapter(getContext(), users)));
    }
}
