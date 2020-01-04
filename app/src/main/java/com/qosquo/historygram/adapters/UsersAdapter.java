package com.qosquo.historygram.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidessence.lib.RichTextView;
import com.qosquo.historygram.R;
import com.qosquo.historygram.fragments.UserFragment;
import com.qosquo.historygram.models.Media;
import com.qosquo.historygram.models.User;
import com.qosquo.historygram.util.Global;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<User> mUsers;
    private LayoutInflater mInflater;

    public UsersAdapter(Context context, List<User> users) {
        this.mInflater = LayoutInflater.from(context);
        this.mUsers = users;
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view
        View view = mInflater.inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // - get element from your dataset at this position
        User user = mUsers.get(position);

        // Load profile picture
        Picasso.get()
                .load(user.getProfilePicture())
                .into(holder.profileImageView);

        // Set username
        holder.usernameTextView.setText(user.getUsername());

        // Set user's full name
        holder.nameTextView.setText(user.getName());

        holder.userLinearLayout.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt(UserFragment.ARGUMENT_USERID, user.getId());
            Global.handleReplaceFragment(holder.itemView, new UserFragment(), args);
        });
    }

    @Override
    public int getItemCount() {
        return mUsers != null ? mUsers.size() : 0;
    }

    // Stores and recycles view as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout userLinearLayout;
        ImageView profileImageView;
        TextView usernameTextView;
        TextView nameTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            userLinearLayout = itemView.findViewById(R.id.linearLayout_user);
            profileImageView = itemView.findViewById(R.id.imageView_profile);
            usernameTextView = itemView.findViewById(R.id.textView_username);
            nameTextView = itemView.findViewById(R.id.textView_name);
        }
    }
}

