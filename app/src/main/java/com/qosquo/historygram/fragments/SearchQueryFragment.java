package com.qosquo.historygram.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.Gson;
import com.historygram.api.models.Hashtag;
import com.historygram.api.models.SearchResponse;
import com.qosquo.historygram.R;
import com.qosquo.historygram.adapters.SearchResponseAdapter;
import com.qosquo.historygram.adapters.UsersAdapter;
import com.qosquo.historygram.models.User;
import com.qosquo.historygram.viewmodels.SearchQueryViewModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class SearchQueryFragment extends Fragment {

    private Toolbar mToolbar;
    private EditText mSearchEditText;

    private RecyclerView mRecyclerView;

    private SearchQueryViewModel mViewModel;

    public static SearchQueryFragment newInstance() {
        return new SearchQueryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_query_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(SearchQueryViewModel.class);
        mViewModel.init();

        mViewModel.getResponse().observe(this, response ->
                mRecyclerView.setAdapter(new SearchResponseAdapter(getContext(), response)));
//        mViewModel.getUsers().observe(this, users ->
//                mRecyclerView.setAdapter(new UsersAdapter(getContext(), users)));
        mViewModel.getQuery().setValue("");

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.getQuery().setValue(s.toString());
            }
        });

        initRecyclerView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Get toolbar and set it as a support action bar
        mToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) view.getContext()).setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_24);
        mToolbar.setNavigationOnClickListener(v ->
                Objects.requireNonNull(getActivity()).onBackPressed());

        // Get recycler view
        mRecyclerView = view.findViewById(R.id.recyclerView);

        // Get edit text search view
        mSearchEditText = view.findViewById(R.id.editText_search);
        mSearchEditText.requestFocus();
    }

    private void initRecyclerView() {
        // Use a linear layout manager for the view
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Set recycler view adapter
        //RecyclerView.Adapter mAdapter = new SearchResponseAdapter(getContext(), new SearchResponse(new Hashtag[]{}, "", "ok"));
        //mRecyclerView.setAdapter(mAdapter);
    }
}
