package com.qosquo.historygram.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.historygram.api.Api;
import com.qosquo.historygram.R;
import com.qosquo.historygram.repositories.CollectionsRepository;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewCollectionFragment extends Fragment {
    private static final String TAG = "NewCollectionFragment";

    private Toolbar mToolbar;
    private EditText mNameEditText;

    public NewCollectionFragment() {
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
        return inflater.inflate(R.layout.new_collection_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) view.getContext()).setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_24);
        mToolbar.setNavigationOnClickListener(v ->
                Objects.requireNonNull(getActivity()).onBackPressed());

        mNameEditText = view.findViewById(R.id.editText_name);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            createNewCollection();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNewCollection() {
        CollectionsRepository repository = CollectionsRepository.getInstance();
        String name = mNameEditText.getText().toString();

        repository.newCollection(name, null);
        Objects.requireNonNull(getActivity()).onBackPressed();
    }
}
