package com.qosquo.historygram.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qosquo.historygram.R;
import com.qosquo.historygram.adapters.CollectionsGridAdapter;
import com.qosquo.historygram.repositories.CollectionsRepository;
import com.qosquo.historygram.viewmodels.CollectionsViewModel;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionsModalBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "CollectionsModalBottomS";

    public static final String ARGUMENT_ITEMID = "ARGUMENT_ITEMID";

    private ImageButton mCreateButton;
    private RecyclerView mRecyclerView;

    private CollectionsViewModel mViewModel;
    private int mItemId;

    public CollectionsModalBottomSheet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.collections_modal_bottom_sheet_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(CollectionsViewModel.class);
        mViewModel.init();
        mViewModel.getAll();

        mCreateButton = view.findViewById(R.id.button_createNew);
        mCreateButton.setOnClickListener(v -> {
            NewCollectionModalBottomSheet modalBottomSheet = new NewCollectionModalBottomSheet();
            modalBottomSheet.setItemId(mItemId);
            FragmentManager manager = ((AppCompatActivity) Objects.requireNonNull(getContext())).getSupportFragmentManager();
            modalBottomSheet.show(manager, "newCollectionModalMenu");
            this.dismiss();
        });

        mRecyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                RecyclerView.HORIZONTAL,
                false);
        mRecyclerView.setLayoutManager(layoutManager);
        mViewModel.getCollections().observe(this, collections -> {
            RecyclerView.Adapter adapter = new CollectionsGridAdapter(getContext(),
                    collections,
                    this::onCollectionClick);
            mRecyclerView.setAdapter(adapter);
        });
    }

    private void onCollectionClick(final int collectionId) {
        CollectionsRepository repository = CollectionsRepository.getInstance();
        repository.addItem(collectionId, mItemId);
        this.dismiss();
    }

    public void setItemId(int itemId) {
        this.mItemId = itemId;
    }
}
