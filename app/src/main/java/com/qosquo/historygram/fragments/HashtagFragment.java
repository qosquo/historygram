package com.qosquo.historygram.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.historygram.api.models.Hashtag;
import com.qosquo.historygram.R;
import com.qosquo.historygram.adapters.TabAdapter;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class HashtagFragment extends Fragment {
    private static final String TAG = "HashtagFragment";

    public static final String ARGUMENT_HASHTAG = "ARGUMENT_HASHTAG";
    public static final String ARGUMENT_HASHTAGID = "ARGUMENT_HASHTAGID";

    private Hashtag hashtag;

    public HashtagFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.hashtag_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getContext() == null) {
            return;
        }
        if (getArguments() != null) {
            hashtag = (Hashtag) getArguments().getSerializable(ARGUMENT_HASHTAG);
        }

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("#" + hashtag.getName());
        ((AppCompatActivity) view.getContext()).setSupportActionBar(toolbar);

        if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
            toolbar.setNavigationIcon(R.drawable.ic_back_24);
            toolbar.setNavigationOnClickListener(v ->
                    Objects.requireNonNull(getActivity()).onBackPressed());
        }

        ViewPager viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        TabAdapter mAdapter = new TabAdapter((getChildFragmentManager()));
        Bundle args = new Bundle();
        args.putInt(ARGUMENT_HASHTAGID, hashtag.getId());

        MediaGridFragment topMediaFragment = new MediaGridFragment();
        topMediaFragment.setArguments(args);
        MediaGridFragment recentMediaFragment = new MediaGridFragment();
        recentMediaFragment.setArguments(args);

        mAdapter.addFragment(topMediaFragment, getResources().getString(R.string.hashtag_top));
        mAdapter.addFragment(recentMediaFragment, getResources().getString(R.string.hashtag_recent));

        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
