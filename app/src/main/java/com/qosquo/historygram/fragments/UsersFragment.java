package com.qosquo.historygram.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.historygram.api.ApiScheduler;
import com.qosquo.historygram.R;
import com.qosquo.historygram.adapters.TabAdapter;
import com.qosquo.historygram.models.User;

import java.util.Arrays;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {
    private static final String TAG = "UsersFragment";

    public static final String ARGUMENT_USER = "ARGUMENT_USER";
    public static final String ARGUMENT_USERID = "ARGUMENT_USER";
    public static final String ARGUMENT_SELECT_TAB = "ARGUMENT_SELECT_TAB";

    private User user;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.users_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        int selectedTab = 0;
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARGUMENT_USER);
            selectedTab = getArguments().getInt(ARGUMENT_SELECT_TAB);
        }
        if (getContext() == null) {
            return;
        }

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(user.getUsername());
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
        args.putInt(ARGUMENT_USERID, user.getId());

        FollowersFragment followersFragment = new FollowersFragment();
        followersFragment.setArguments(args);
        FollowsFragment followsFragment = new FollowsFragment();
        followsFragment.setArguments(args);

        mAdapter.addFragment(followersFragment, getResources().getString(R.string.user_followers));
        mAdapter.addFragment(followsFragment, getResources().getString(R.string.user_follows));

        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
        Objects.requireNonNull(tabLayout.getTabAt(selectedTab)).select();
    }
}
