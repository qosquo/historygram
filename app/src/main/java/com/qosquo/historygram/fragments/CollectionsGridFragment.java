package com.qosquo.historygram.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.qosquo.historygram.R;
import com.qosquo.historygram.adapters.CollectionsGridAdapter;
import com.qosquo.historygram.util.Global;
import com.qosquo.historygram.viewmodels.CollectionsViewModel;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionsGridFragment extends Fragment {
    private static final String TAG = "CollectionsGridFragment";

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;

    private CollectionsViewModel mViewModel;

    public CollectionsGridFragment() {
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
        return inflater.inflate(R.layout.collections_grid_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getActivity() == null) {
            return;
        }

        mViewModel = ViewModelProviders.of(getActivity()).get(CollectionsViewModel.class);
        mViewModel.init();
        mViewModel.getAll();

        mToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) view.getContext()).setSupportActionBar(mToolbar);

        mRecyclerView = view.findViewById(R.id.recyclerView);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
            mToolbar.setNavigationIcon(R.drawable.ic_back_24);
            mToolbar.setNavigationOnClickListener(v ->
                    Objects.requireNonNull(getActivity()).onBackPressed());
        }

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mViewModel.getCollections().observe(this, collections -> {
            RecyclerView.Adapter adapter = new CollectionsGridAdapter(getContext(),
                    collections,
                    null);
            mRecyclerView.setAdapter(adapter);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.collections_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_create_collection) {
            Global.handleReplaceFragment(Objects.requireNonNull(getView()),
                    new NewCollectionFragment(),
                    null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
