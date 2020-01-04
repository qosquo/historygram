package com.qosquo.historygram.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidessence.lib.RichTextView;
import com.historygram.api.Api;
import com.historygram.api.ApiScheduler;
import com.qosquo.historygram.R;
import com.qosquo.historygram.adapters.MediaGridAdapter;
import com.qosquo.historygram.data.LoginDataSource;
import com.qosquo.historygram.data.LoginRepository;
import com.qosquo.historygram.models.Media;
import com.qosquo.historygram.models.User;
import com.qosquo.historygram.ui.login.LoginActivity;
import com.qosquo.historygram.util.Global;
import com.qosquo.historygram.viewmodels.MediaViewModel;
import com.qosquo.historygram.viewmodels.UserViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UserFragment extends Fragment {

    public static final String ARGUMENT_USERID = "ARGUMENT_USERID";

    private UserViewModel mViewModel;
    private MediaViewModel mMediaViewModel;

    private Toolbar mToolbar;
    private ImageView mVerifiedImageView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ImageView mProfileImageView;
    private RichTextView mMediaTextView;
    private RichTextView mFollowersTextView;
    private RichTextView mFollowsTextView;
    private TextView mNameTextView;
    private TextView mBiographyTextView;
    private RecyclerView mRecyclerView;

    private int mUserId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Firstly check for arguments
        if (getArguments() != null) {
            mUserId = getArguments().getInt(ARGUMENT_USERID);
        }

        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(UserViewModel.class);
        mViewModel.init(mUserId);

        mMediaViewModel = ViewModelProviders.of(getActivity())
                .get(MediaViewModel.class);
        mMediaViewModel.init();
        mMediaViewModel.userMedia(mUserId, Arrays.asList("id", "mediaUrl", "owner"));

        mSwipeRefreshLayout.setRefreshing(true);
        mViewModel.getUser().observe(this, user -> {
            if (user == null) {
                return;
            }
            setUserFragmentData(user);
        });
        mMediaViewModel.getMedia().observe(this, media -> {
            if (media == null) {
                return;
            }
            Collections.sort(media, (o1, o2) -> o2.getId().compareTo(o1.getId()));
            mRecyclerView.setAdapter(new MediaGridAdapter(getContext(), media));
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Get toolbar and set it as an action bar
        mToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) view.getContext()).setSupportActionBar(mToolbar);

        if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
            mToolbar.setNavigationIcon(R.drawable.ic_back_24);
            mToolbar.setNavigationOnClickListener(v ->
                    Objects.requireNonNull(getActivity()).onBackPressed());
        }

        mVerifiedImageView = view.findViewById(R.id.imageView_verified);

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.refreshUser();
            mMediaViewModel.refreshMedia();
        });

        mProfileImageView = view.findViewById(R.id.imageView_profile);
        mMediaTextView = view.findViewById(R.id.textView_media);
        mFollowersTextView = view.findViewById(R.id.textview_followers);
        mFollowsTextView = view.findViewById(R.id.textView_follows);
        mNameTextView = view.findViewById(R.id.textView_name);
        mBiographyTextView = view.findViewById(R.id.textView_biography);

        mRecyclerView = view.findViewById(R.id.recyclerView);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (mUserId == Api.getConfig().getUserId()) {
            inflater.inflate(R.menu.user_profile_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
            loginRepository.logout();

            if (getActivity() != null) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("api", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("USERNAME");
                editor.remove("PASSWORD");
                editor.remove("TOKEN");
                editor.remove("ID");
                editor.remove("LAST_AUTH");
                editor.apply();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                return true;
            }
        } else if (item.getItemId() == R.id.action_collections) {
            Global.handleReplaceFragment(getView(), new CollectionsGridFragment(), null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUserFragmentData(@Nullable User user) {
        if (user != null) {
            if (user.getRole() < 2 && getContext() != null) {
                mVerifiedImageView.setVisibility(View.VISIBLE);
                if (user.getRole() == 0) {
                    mVerifiedImageView.setColorFilter(getResources().getColor(R.color.colorAccent, getContext().getTheme()));
                }
                if (user.getRole() == 1) {
                    mVerifiedImageView.setColorFilter(getResources().getColor(R.color.blue, getContext().getTheme()));
                }
            }

            mSwipeRefreshLayout.setRefreshing(false);

            // Set user profile picture
            Picasso.get()
                    .load(user.getProfilePicture())
                    .into(mProfileImageView);

            // Set user's media, followers and follows counts
            String media = user.getMediaCount() + "\n" + getResources().getString(R.string.user_media).toLowerCase();
            mMediaTextView.setText(media);
            mMediaTextView.formatSpan(0,
                    user.getMediaCount().toString().length(),
                    RichTextView.FormatType.BOLD);

            String followers = user.getFollowersCount() + "\n" + getResources().getString(R.string.user_followers).toLowerCase();
            mFollowersTextView.setText(followers);
            mFollowersTextView.formatSpan(0,
                    user.getFollowersCount().toString().length(),
                    RichTextView.FormatType.BOLD);
            mFollowersTextView.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putSerializable(UsersFragment.ARGUMENT_USER, user);
                args.putInt(UsersFragment.ARGUMENT_SELECT_TAB, 0);
                Global.handleReplaceFragment(requireView(), new UsersFragment(), args);
            });

            String follows = user.getFollowsCount() + "\n" + getResources().getString(R.string.user_follows).toLowerCase();
            mFollowsTextView.setText(follows);
            mFollowsTextView.formatSpan(0,
                    user.getFollowsCount().toString().length(),
                    RichTextView.FormatType.BOLD);
            mFollowsTextView.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putSerializable(UsersFragment.ARGUMENT_USER, user);
                args.putInt(UsersFragment.ARGUMENT_SELECT_TAB, 1);
                Global.handleReplaceFragment(requireView(), new UsersFragment(), args);
            });

            // Set user main information
            mNameTextView.setText(user.getName());
            mBiographyTextView.setText(user.getBiography());

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
            mRecyclerView.setLayoutManager(mLayoutManager);
            RecyclerView.Adapter mAdapter = new MediaGridAdapter(getContext(), mMediaViewModel.getMedia().getValue());
            mRecyclerView.setAdapter(mAdapter);

            if (getView() != null) {
                // Set user action button
                if (mUserId == Api.getConfig().getUserId()) {
                    Button mEditProfileButton = getView().findViewById(R.id.button_edit);
                    mEditProfileButton.setVisibility(View.VISIBLE);
                    mEditProfileButton.setOnClickListener(v -> {
                        Bundle args = new Bundle();
                        args.putSerializable(EditProfileFragment.ARG_USER, user);
                        Global.handleReplaceFragment(v, new EditProfileFragment(), args);
                    });
                } else if (mViewModel.getUser().getValue() != null) {
                    System.out.println("User followed? " + mViewModel.getUser().getValue().isUserFollowed());
                    Button mFollowingButton = getView().findViewById(R.id.button_following);
                    Button mFollowButton = getView().findViewById(R.id.button_follow);
                    if (mViewModel.getUser().getValue().isUserFollowed()) {
                        mFollowingButton.setVisibility(View.VISIBLE);
                        mFollowButton.setVisibility(View.GONE);

                        mFollowingButton.setOnClickListener(v -> {
                            mSwipeRefreshLayout.setRefreshing(true);
                            mViewModel.follow(mUserId);
                        });
                    } else {
                        mFollowButton.setVisibility(View.VISIBLE);
                        mFollowingButton.setVisibility(View.GONE);

                        mFollowButton.setOnClickListener(v -> {
                            mSwipeRefreshLayout.setRefreshing(true);
                            mViewModel.follow(mUserId);
                        });
                    }
                }
            }

            // Set toolbar's title to user's username
            mToolbar.setTitle(user.getUsername());
        }

    }
}