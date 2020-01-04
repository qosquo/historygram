package com.qosquo.historygram.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.historygram.api.models.Hashtag;
import com.historygram.api.models.SearchResponse;
import com.qosquo.historygram.R;
import com.qosquo.historygram.fragments.HashtagFragment;
import com.qosquo.historygram.fragments.UserFragment;
import com.qosquo.historygram.models.User;
import com.qosquo.historygram.util.Global;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchResponseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_HASHTAG = 0;
    private static final int ITEM_TYPE_USER = 1;

    private List<Object> items;
    private LayoutInflater mInflater;

    public SearchResponseAdapter(Context context, SearchResponse response) {
        this.mInflater = LayoutInflater.from(context);

        this.items = new ArrayList<>();
        this.items.addAll(Arrays.asList(response.getHashtags()));
        this.items.addAll(Arrays.asList(new Gson().fromJson(response.getUsers(), User[].class)));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        // Create a new view
//        View view = mInflater.inflate(R.layout.item_user, parent, false);
//        return new ViewHolder(view);

        if (viewType == ITEM_TYPE_HASHTAG) {
            View view = mInflater.inflate(R.layout.item_hashtag, parent, false);

            return new HashtagViewHolder(view);
        } else {
            View view = mInflater.inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Hashtag) {
            return ITEM_TYPE_HASHTAG;
        } else {
            return ITEM_TYPE_USER;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);

        if (holder instanceof HashtagViewHolder) {
            ((HashtagViewHolder) holder).bind(((Hashtag) item));
        } else {
            ((UserViewHolder) holder).bind((User) item);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // Stores and recycles view as they are scrolled off screen
    class HashtagViewHolder extends RecyclerView.ViewHolder {
        LinearLayout hashtagLinearLayout;
        ImageView pictureImageView;
        TextView nameTextView;

        HashtagViewHolder(@NonNull View itemView) {
            super(itemView);
            hashtagLinearLayout = itemView.findViewById(R.id.linearLayout_hashtag);
            pictureImageView = itemView.findViewById(R.id.imageView_profile);
            nameTextView = itemView.findViewById(R.id.textView_name);
        }

        @SuppressLint("SetTextI18n")
        void bind(Hashtag hashtag) {
            // Load hashtag picture
            Picasso.get()
                    .load("https://www.instagram.com/static/images/hashtag/search-hashtag-default-avatar.png/1d8417c9a4f5.png")
                    .centerCrop()
                    .fit()
                    .into(pictureImageView);

            // Set hashtag name
            nameTextView.setText("#" + hashtag.getName());

            hashtagLinearLayout.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putSerializable(HashtagFragment.ARGUMENT_HASHTAG, hashtag);
                Global.handleReplaceFragment(itemView, new HashtagFragment(), args);
            });
        }
    }

    // Stores and recycles view as they are scrolled off screen
    class UserViewHolder extends RecyclerView.ViewHolder {
        LinearLayout userLinearLayout;
        ImageView profileImageView;
        TextView usernameTextView;
        ImageView verifiedImageView;
        TextView nameTextView;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userLinearLayout = itemView.findViewById(R.id.linearLayout_user);
            profileImageView = itemView.findViewById(R.id.imageView_profile);
            usernameTextView = itemView.findViewById(R.id.textView_username);
            verifiedImageView = itemView.findViewById(R.id.imageView_verified);
            nameTextView = itemView.findViewById(R.id.textView_name);
        }

        void bind(User user) {
            // Load profile picture
            Picasso.get()
                    .load(user.getProfilePicture())
                    .into(profileImageView);

            // Set username
            usernameTextView.setText(user.getUsername());

            // Set user verified
            if (user.getRole() < 2) {
                verifiedImageView.setVisibility(View.VISIBLE);
                if (user.getRole() == 0) {
                    verifiedImageView.setColorFilter(itemView
                            .getResources()
                            .getColor(R.color.colorAccent, itemView.getContext().getTheme()));
                }
                if (user.getRole() == 1) {
                    verifiedImageView.setColorFilter(itemView
                            .getResources()
                            .getColor(R.color.blue, itemView.getContext().getTheme()));
                }
            }

            // Set user's full name
            nameTextView.setText(user.getName());

            userLinearLayout.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putInt(UserFragment.ARGUMENT_USERID, user.getId());
                Global.handleReplaceFragment(itemView, new UserFragment(), args);
            });
        }
    }
}

